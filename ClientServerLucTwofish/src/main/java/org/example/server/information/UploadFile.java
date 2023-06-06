package org.example.server.information;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.Algoritmes.debug.DebugFunctions;
import org.example.massege.Commands;
import org.example.massege.CommandsLoad;
import org.example.massege.Massege;
import org.example.massege.MassegeNewFile;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Builder
public class UploadFile implements Runnable {
    ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Massege>> date;
    Integer id;
    Integer toId;
    Socket socket;
    DataInputStream dateInputStream;
    DataOutputStream dateOutputStream;
    String fileName;
    Stop exit;
    DateBase dateBase;
    BufferedOutputStream out;
    Blob blob;
    Stop serverStop;

    @Override
    public void run() {
        log.debug("-UPLOAD_" + id + "_START-");
        if ((!exit.stop) && (!serverStop.stop)) {
            try {
                makeBlob();
                uploadFile();
            } catch (IOException e) {
                log.debug("-UPLOAD_" + id + "_CANT_READ_CLIENT_INFORMATION-");
                e.printStackTrace();
            } catch (SQLException e) {
                log.debug("-UPLOAD_" + id + "_CANT_MAKE_BLOB-");
                e.printStackTrace();
            }
        }
        stopServer();
    }
    void makeBlob() throws SQLException {
        blob=dateBase.makeBlob();
        //OutputStream out=blob.setBinaryStream(1);
        out=new BufferedOutputStream(blob.setBinaryStream(1));
    }
    void uploadFile() throws IOException{
        int count=dateInputStream.readInt();
        while(CommandsLoad.hasNext(count)){
            log.debug("-UPLOAD_"+id+"_READ_COUNT_"+count+"-");
            byte[] date=new byte[count];
            dateInputStream.read(date);
            log.debug("-UPLOAD_"+id+"_READ_FILE_"+ DebugFunctions.byteArrayToHexString(date) +"-");
            writeFile(date,out);
            count=dateInputStream.readInt();
        }
        log.debug("-UPLOAD_"+id+"_READ_COMMAND_"+CommandsLoad.getCommandForDebug(count)+"-");
        switch (count){
            case(CommandsLoad.LAST):{
                writeDateToBD();
            }
        }
    }
    void stopServer(){
        try {
            dateOutputStream.writeInt(CommandsLoad.STOP);dateOutputStream.flush();
            dateInputStream.close();
            dateOutputStream.close();
            socket.close();
        } catch (IOException e) {
            log.debug("-UPLOAD_" + id +"Cant_Close");
        }
    }
    synchronized void   writeDateToBD() throws IOException {
        out.flush();
        out.close();
        int fileId=dateBase.setFile(blob,fileName,toId,id);
        if(fileId!=-1) {//Не удалось добавить в бд
            log.debug("-UPLOAD_" + id +"ADD_TO_BD_FILE:"+fileName+" ID:"+fileId+"("+toId+","+id+")");
            MassegeNewFile massegeNewFileId = new MassegeNewFile(toId, fileId, fileName.getBytes());
            massegeNewFileId.setCommand(Commands.NEW_FILE);
            MassegeNewFile massegeNewFileToId = new MassegeNewFile(id, fileId, fileName.getBytes());
            massegeNewFileToId.setCommand(Commands.NEW_FILE);
            if(date.containsKey(toId)){
                date.get(toId).add(massegeNewFileToId);
            }
            if(date.containsKey(id)) {
                date.get(id).add(massegeNewFileId);
            }
            log.debug("-UPLOAD_" + id +"WRITE_MASSEGE_FILE:"+fileName+" ID:"+fileId+"("+toId+","+id+")");
        }

    }
    void writeFile(byte[] date,BufferedOutputStream out) throws IOException {
        log.debug("UPLOAD_FILE_READ:"+DebugFunctions.byteArrayToHexString(date));
        out.write(date);
        log.debug("UPLOAD_FILE_WRITE:"+DebugFunctions.byteArrayToHexString(date));
    }
}
