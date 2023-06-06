package org.example.client.client;

import com.example.client.SettingsApplication;
import org.example.Algoritmes.LUC.LUC;
import org.example.Algoritmes.LUC.LUCKey;
import org.example.Algoritmes.Twofish.Twofish;
import org.example.Algoritmes.debug.DebugFunctions;
import org.example.massege.Commands;
import javafx.application.Platform;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.server.information.Stop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
@Builder
public class ClientInformation implements Runnable {
    DataInputStream dateInputStream;
    DataOutputStream dateOutputStream;
    SettingsApplication settingsApplication;

    Socket socket;
    int id;
    Stop stop;
    ClientBD dateBase;

    @Override
    public void run() {
        log.debug("-READER"+"_START-");
        while(!stop.stop){
            try {
                readNext();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
        try {
            dateOutputStream.close();
            dateInputStream.close();
            socket.close();
        } catch (IOException e) {
            log.debug("-READER"+e.getMessage());
        }
        log.debug("-READER"+"_STOP-");
    }
    void readNext() throws IOException {
        if (dateInputStream.available()!=0) {
            read();
        }
    }
    void read() throws IOException {
        int command =dateInputStream.readInt();
        log.debug("-READER_READ_"+ Commands.getNameForDebug(command));
        switch (command){
            case(Commands.PUBLIC_KEY):{
                publicKey();
                return;
            }
            case(Commands.NEW_FILE):{
                newFile();
                return;

            }
            case(Commands.CHANEL_KEY):{
                chanelKey();
                return;

            }
            case(Commands.STOP):{
                stopClient();
                return;

            }
            default:{
                log.debug("-NOT_CORRECT_READ_"+Commands.getNameForDebug(command)+"-");
            }

        }
    }
    private void stopClient(){
        stop.stop=true;
    }
    private void publicKey() throws IOException {
        byte[] key= Twofish.createKey();
        int fromId=dateInputStream.readInt();
        log.debug("-READER_"+id+"_READ_FROM_"+fromId);
        BigInteger[] publicKey=new BigInteger[2];
        //READ E
        int eSize=dateInputStream.readInt();
        log.debug("-READER"+id+"_READE_PUBLIC_KEY_E_SIZE"+eSize);
        byte[] e=new byte[eSize];
        dateInputStream.read(e,0,eSize);
        publicKey[0]=new BigInteger(e);
        log.debug("-READER_"+id+"_READE_PUBLIC_E_KEY"+ DebugFunctions.byteArrayToHexString(e));
        //READ N
        int nSize=dateInputStream.readInt();
        log.debug("-READER"+id+"_READE_PUBLIC_KEY_N_SIZE"+nSize);
        byte[] n=new byte[nSize];
        dateInputStream.read(n,0,nSize);
        publicKey[1]=new BigInteger(n);
        log.debug("-READER_"+id+"_READE_PUBLIC_N_KEY"+DebugFunctions.byteArrayToHexString(n));

        byte[] keyDecrypt= LUC.encrypt(key,publicKey);
        writeChanelKey(fromId,keyDecrypt);
        dateBase.setChanel(fromId,String.valueOf(fromId),key);
        Platform.runLater(() ->settingsApplication.addChanel(fromId,String.valueOf(fromId)));

    }
    void writeChanelKey( int toId,byte[]key) throws IOException {
        dateOutputStream.writeInt(Commands.CHANEL_KEY);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_COMMAND_"+Commands.getNameForDebug(Commands.CHANEL_KEY));
        dateOutputStream.writeInt(toId);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_CHANEL_KEY_TO"+toId);
        dateOutputStream.writeInt(key.length);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_CHANEL_KEY_SIZE"+key.length);
        dateOutputStream.write(key);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_CHANEL_KEY"+DebugFunctions.byteArrayToHexString(key));
    }
    private void newFile() throws IOException {
        int toId=dateInputStream.readInt();
        log.debug("-READER"+id+"_READE_TO_ID"+ toId);
        int fileId=dateInputStream.readInt();
        log.debug("-READER"+id+"_READE_FILE_ID"+ fileId);
        int fileNameLen=dateInputStream.readInt();
        log.debug("-READER"+id+"_READE_FILENAME_LEN"+ fileNameLen);
        byte[] fileNameArray=new byte[fileNameLen];
        dateInputStream.readFully(fileNameArray);
        String fileName=new String(fileNameArray);
        log.debug("-READER"+id+"_READE_FILENAME:"+ fileName);
        Platform.runLater(() ->dateBase.setFile(toId,fileName,fileId));
    }
    private void chanelKey() throws IOException {
        int fromId=dateInputStream.readInt();
        log.debug("-READER"+id+"_READE_FROM_"+fromId);
        int keySize=dateInputStream.readInt();
        log.debug("-READER"+id+"_READE_CHANEL_KEY_SIZE"+keySize);
        byte[] key=new byte[keySize];
        dateInputStream.readFully(key,0,keySize);
        log.debug("-READER"+id+"_READE_CHANEL_KEY"+DebugFunctions.byteArrayToHexString(key));
       dateBase.setAndDecryptChanel(fromId,key);
        Platform.runLater(() ->settingsApplication.addChanel(fromId,String.valueOf(fromId)));

    }
    public void sendStop() throws IOException{
        if(!stop.stop) {
            dateOutputStream.writeInt(Commands.STOP);
            dateOutputStream.flush();
        }
        while (!stop.stop){log.debug("EXIT");}
    }
    public void sendPublicKey(int toId, String name, LUCKey lucKey) throws IOException {
        if(!stop.stop) {
            dateOutputStream.writeInt(Commands.PUBLIC_KEY);
            dateOutputStream.flush();
            dateOutputStream.writeInt(toId);
            dateOutputStream.flush();
            log.debug("-WRITER_" + id + "_WRITE_PUBLIC_KEY_TO_" + toId);
            //WRITE E
            byte[] e = lucKey.getE().toByteArray();
            dateOutputStream.writeInt(e.length);
            dateOutputStream.flush();
            log.debug("-WRITE_" + id + "_READ_PUBLIC_KEY_E_SIZE_" + e.length);
            dateOutputStream.write(e);
            dateOutputStream.flush();
            log.debug("-READER_" + id + "_READ_PUBLIC_KEY_E_" + DebugFunctions.byteArrayToHexString(e));
            //WRITE N
            byte[] n = lucKey.getN().toByteArray();
            dateOutputStream.writeInt(n.length);
            dateOutputStream.flush();
            log.debug("-WRITE_" + id + "_READ_PUBLIC_KEY_N_SIZE_" + n.length);
            dateOutputStream.write(n);
            dateOutputStream.flush();
            log.debug("-READER_" + id + "_READ_PUBLIC_KEY_N_" + DebugFunctions.byteArrayToHexString(e));
            dateBase.setPrivateKey(lucKey.getDKeys(), toId, name);
        }
    }/*
    void uploadFile(int chanelId, String fileName, byte[] date) throws IOException {
        dateOutputStream.writeInt(Commands.UPLOAD_FILE);dateOutputStream.flush();
        log.debug("-WRITER_"+chanelId+"_WRITE_COMMAND_"+Commands.getNameForDebug(Commands.CHANEL_KEY));
        dateOutputStream.writeInt(chanelId);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_CHANEL_TO"+chanelId);
        byte[]fileNameArray=fileName.getBytes();
        dateOutputStream.writeInt(fileNameArray.length);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_FILENAME_SIZE"+fileNameArray.length);
        dateOutputStream.write(fileNameArray);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_NAME_KEY"+fileName);
        dateOutputStream.writeInt(date.length);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_FILENAME_SIZE"+fileNameArray.length);
        dateOutputStream.write(date);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_NAME_KEY"+fileName);
    }*/
    /*void downloadFile(int fileId, String fileName, byte[] date) throws IOException {
        dateOutputStream.writeInt(Commands.UPLOAD_FILE);dateOutputStream.flush();
        log.debug("-WRITER_"+chanelId+"_WRITE_COMMAND_"+Commands.getNameForDebug(Commands.CHANEL_KEY));
        dateOutputStream.writeInt(chanelId);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_CHANEL_TO"+chanelId);
        byte[]fileNameArray=fileName.getBytes();
        dateOutputStream.writeInt(fileNameArray.length);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_FILENAME_SIZE"+fileNameArray.length);
        dateOutputStream.write(fileNameArray);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_NAME_KEY"+fileName);
        dateOutputStream.writeInt(date.length);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_FILENAME_SIZE"+fileNameArray.length);
        dateOutputStream.write(date);dateOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_NAME_KEY"+fileName);
    }*/
}
