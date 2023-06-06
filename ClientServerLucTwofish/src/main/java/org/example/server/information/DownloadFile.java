package org.example.server.information;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.Algoritmes.debug.DebugFunctions;
import org.example.massege.CommandsLoad;

import java.io.*;
import java.net.Socket;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

@Slf4j
@Builder
public class DownloadFile implements Runnable {

    int fileid;
    int id;

    DataOutputStream dateOutputStream;
    DataInputStream dateInputStream;
    Socket socket;

    Stop exit;
    Stop serverStop;

    DateBase dateBase;
    BufferedInputStream in;
    Blob blob;
    void makeBlob() throws SQLException {
        blob=dateBase.getFile(fileid);
        in=new BufferedInputStream(blob.getBinaryStream());
    }
    @Override
    public void run() {
        log.debug("-DOWNLOAD_"+id+"_START:_"+fileid+"-");
        if((!exit.stop)&&(!serverStop.stop)){
            try {
                makeBlob();
                load();
            } catch (IOException e) {
                log.debug("DOWNLOAD_"+id+"_SQL_EXEPTION"+e.getMessage());
            } catch (SQLException e) {
                log.debug("DOWNLOAD_"+id+"FILE_SQL_EXEPTION"+e.getMessage());
            }
        }
        log.debug("DOWNLOAD_"+id+"FILE"+"END_WORK");

    }
    public void runStop(){
        while((!exit.stop)&&(!serverStop.stop)) {
            try {
                if (dateInputStream.available() != 0) {
                    int command=dateInputStream.readInt();
                    switch (command){
                        case(CommandsLoad.STOP):{
                            exit.stop=true;
                            continue;
                        }
                        default: {
                            log.debug("NOT_CORRECT_COMMAND");
                        }
                    }
                }
            }
            catch (IOException e) {
                log.debug("DOWNLOAD_"+id+"FILE_EXEPTION"+e.getMessage());
            }
        }
        if(serverStop.stop){
            exit.stop=true;
        }
        log.debug("DOWNLOAD_"+id+"FILE"+"END_WORK");
    }

    void load() throws IOException {
        int bufferSize=16*15*7*13*11*3+16*2;//для первого блока
        int setBufferSize=16*15*7*13*11*3;//число кратно всем [1,16] нужно для корректной расшифровки при любом режиме
        while(in.available()!=0) {
            byte[] array;
            if (in.available() > bufferSize) {
                array = new byte[bufferSize];
            }
            else{
                array=new byte[in.available()];
            }
            in.read(array);
            if((!exit.stop)&&(!serverStop.stop)){
                dateOutputStream.writeInt(array.length);dateOutputStream.flush();
                log.debug("DOWNLOAD_"+id+"_WRITE:array.length:"+array.length);
                dateOutputStream.write(array);dateOutputStream.flush();
                log.debug("DOWNLOAD_"+id+"_WRITE:array:"+ DebugFunctions.byteArrayToHexString(array));//отладка
            }
            else{
                break;
            }
            bufferSize=setBufferSize;
        }
        if(serverStop.stop||exit.stop){
            dateOutputStream.writeInt(CommandsLoad.STOP);dateOutputStream.flush();
            log.debug("DOWNLOAD_"+id+"_WRITE:"+CommandsLoad.getCommandForDebug(CommandsLoad.STOP));
        }
        else {
            dateOutputStream.writeInt(CommandsLoad.LAST);dateOutputStream.flush();
            log.debug("DOWNLOAD_"+id+"_WRITE:"+CommandsLoad.getCommandForDebug(CommandsLoad.LAST));
            exit.stop=true;
        }
        dateOutputStream.close();
        dateInputStream.close();
        socket.close();
    }

}
