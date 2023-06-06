package org.example.client.client;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.massege.CommandsLoad;
import org.example.server.information.Stop;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

@Slf4j
@Builder
public class ClientInputStream implements ClientStream {
    DataInputStream dateInputStream;
    DataOutputStream dateOutputStream;
    Socket socket;
    //BufferedInputStream inTest;
    Stop stop;
    Stop upload;

   /* @Override
    public void run() {
        while((!stop.stop)&&(!upload.stop)){
            try {
                if(dateInputStream.available()!=0){
                }
            } catch (IOException e) {

            }

        }
    }*/
    /*public ClientInputStream(String name) {
        try {
            inTest=new BufferedInputStream(new FileInputStream(name));
        } catch (FileNotFoundException e) {
            log.debug("in test:"+e.getMessage());
        }
    }
    public int read(byte[] date) throws IOException{
        return inTest.read(date);
    }
    public int available() throws IOException{
        return inTest.available();
    }
    @Override
    public void setStop() throws IOException {
        inTest.close();
    }*/
    BufferedInputStream in;
    int count;
    public int read(byte[] date) throws IOException {
        if(in==null?(true):(in.available()==0)) {
            count = dateInputStream.readInt();///тут
            if (count == CommandsLoad.LAST) {
                return -1;
            }
            byte[] inf=new byte[count];
            dateInputStream.read(inf);
            if(in!=null) {
                in.close();
            }
            in=new BufferedInputStream(new ByteArrayInputStream(inf));
        }
        if(count==CommandsLoad.STOP){
            setStop();
            throw new IOException("SERVER_WAS_STOP");
        }
        if ((!upload.stop)&&(!stop.stop)) {
            return in.read(date);
        }
        setStop();
        throw new IOException("SERVER_WAS_STOP");
    }

    public int available() throws IOException {
        return in.available()+4;
    }
    @Override
    public void setStop() throws IOException {
        dateOutputStream.writeInt(CommandsLoad.STOP);
        dateOutputStream.flush();
        dateInputStream.close();
        dateOutputStream.close();
        socket.close();
        in.close();
    }
}
