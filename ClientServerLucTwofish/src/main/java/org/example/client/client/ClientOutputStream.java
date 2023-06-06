package org.example.client.client;

import org.example.Algoritmes.debug.DebugFunctions;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.massege.CommandsLoad;
import org.example.server.information.Stop;

import java.io.*;
import java.net.Socket;
@Builder
@Slf4j
public class ClientOutputStream implements Runnable,ClientStream  {
    DataInputStream dateInputStream;
    DataOutputStream dateOutputStream;
    String fileName;
    FileOutputStream out;
    int toId;
    Stop stop;
    Stop stopUpload;

    @Override
    public void run() {
        while((!stop.stop)&&(!stopUpload.stop)){
            try {
                if(dateInputStream.available()!=0){
                    int command=dateInputStream.readInt();
                    if(CommandsLoad.STOP==command){
                        setStop();
                    }
                }
            } catch (IOException e) {
                  log.debug("ClientOutputStream"+e.getMessage());
            }

        }
    }/*
    public ClientOutputStream(String name){
        try {
            out=new FileOutputStream(name);
        } catch (FileNotFoundException e) {
            log.debug("ClientOutputStream"+e.getMessage());
        }
    }
    public void write(byte[] date) throws IOException {
        out.write(date);
    }
    public void flush() throws IOException {
        out.close();
    }
    @Override
    public void setStop() throws IOException {
        out.close();
    }*/

    public void write(byte[] date) throws IOException {
        if ((!stopUpload.stop)&&(!stop.stop)) {
            dateOutputStream.writeInt(date.length);dateOutputStream.flush();
            log.debug("WAS_WRITE_COUNT:"+date.length);
            dateOutputStream.write(date);dateOutputStream.flush();
            log.debug("WAS_WRITE:"+ DebugFunctions.byteArrayToHexString(date));
            return;
        }
        throw new IOException("CLIENT_WAS_STOP");
    }
    public void flush() throws IOException {
        if((!stop.stop)&&(!stopUpload.stop)) {
            dateOutputStream.writeInt(CommandsLoad.LAST);dateOutputStream.flush();
            setStop();
            return;
        }
        throw new IOException("CLIENT_WAS_STOP");
    }
    @Override
    public synchronized void setStop() throws IOException {
        dateOutputStream.writeInt(CommandsLoad.STOP);dateOutputStream.flush();
        stopUpload.stop=true;
    }
}
