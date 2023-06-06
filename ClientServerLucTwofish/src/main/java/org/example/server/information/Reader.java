package org.example.server.information;

import org.example.massege.Commands;
import org.example.massege.Massege;
import org.example.massege.MassegeKey;
import lombok.Builder;
import org.example.massege.MassagePublicKey;
import lombok.extern.slf4j.Slf4j;
import org.example.Algoritmes.debug.DebugFunctions;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Builder
public class Reader implements Runnable{
    ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Massege>> date;
    Integer id;
    DataInputStream dateInputStream;
    Socket socket;
    Stop exit;
    Stop block;
    DateBase dateBase;
    @Override
    public void run() {
        log.debug("-READER_"+id+"_START-");
        while((!exit.stop)){
            try {
                readNext();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stopRead();
        log.debug("-READER_"+id+"_STOP-");
    }
    void stopRead(){
        try {
            dateInputStream.close();
            socket.close();
        } catch (IOException e) {
            log.debug("-READER_"+id+"_CLOSE-");
        }
    }
    void readNext() throws IOException {
        if (dateInputStream.available()!=0) {
            read();
        }
    }
    void read() throws IOException {
        int command =dateInputStream.readInt();
        log.debug("-READER_"+id+"_READ_"+ Commands.getNameForDebug(command));
        switch (command){
            case(Commands.PUBLIC_KEY):{
                publicKey();//public key
                return;
            }
            case(Commands.CHANEL_KEY):{
                chanelKey();
                return;

            }
            case(Commands.BLOCK):{

            }
            case(Commands.STOP):{
                stop();
                return;

            }
            default:{
                log.debug("-NOT_CORRECT_READ"+id+"_"+Commands.getNameForDebug(command)+"-");
            }

        }
    }
    private void stop() {
        this.exit.stop=true;
    }
    private void deleteChanel() {
        //id
    }
    private void chanelKey() throws IOException {
        MassegeKey massegeKey=new MassegeKey();
        int toID=dateInputStream.readInt();
        log.debug("-READER_"+id+"_READ_CHANEL_KEY_TO"+toID);
        int sizeKey=dateInputStream.readInt();
        log.debug("-READER_"+id+"_READ_CHANEL_KEY_SIZE"+sizeKey);
        byte[] chanelKey = new byte[sizeKey];
        dateInputStream.readFully(chanelKey, 0,chanelKey.length); // read the message
        log.debug("-READER_"+id+"_READ_CHANEL_KEY"+DebugFunctions.byteArrayToHexString(chanelKey));
        massegeKey.setCommand(Commands.CHANEL_KEY);
        massegeKey.setFrom(id);
        massegeKey.setKey(chanelKey);
        if(!date.containsKey(toID)){
            date.put(toID,new ConcurrentLinkedQueue<>());
        }
        date.get(toID).add(massegeKey);
        log.debug("-READER_"+id+"_MASSAGE_CHANEL_KEY_WAS_WRITE_IN_QUEUE-");
        dateBase.createNewChanel(toID,id);
    }
    private void block(){
        block.stop=true;
        while(block.stop);
    }

    private void publicKey() throws IOException {
        MassagePublicKey massagePublicKey=new MassagePublicKey();
        int toID=dateInputStream.readInt();
        log.debug("-READER_"+id+"_READ_PUBLIC_KEY_TO_"+toID);
        //READ E
        int sizeKeyE=dateInputStream.readInt();
        log.debug("-READER_"+id+"_READ_PUBLIC_KEY_E_SIZE_"+sizeKeyE);
        byte[] publicKeyE = new byte[sizeKeyE];
        dateInputStream.readFully(publicKeyE, 0,publicKeyE.length); // read the message
        log.debug("-READER_"+id+"_READ_PUBLIC_KEY_E_"+DebugFunctions.byteArrayToHexString(publicKeyE));
        //READ N
        int sizeKeyN=dateInputStream.readInt();
        log.debug("-READER_"+id+"_READ_PUBLIC_KEY_E_SIZE_"+sizeKeyN);
        byte[] publicKeyN = new byte[sizeKeyN];
        dateInputStream.readFully(publicKeyN, 0,publicKeyN.length); // read the message
        log.debug("-READER_"+id+"_READ_PUBLIC_KEY_E_"+DebugFunctions.byteArrayToHexString(publicKeyN));
        //WRITE MASSAGE
        massagePublicKey.setCommand(Commands.PUBLIC_KEY);
        massagePublicKey.setFrom(id);
        massagePublicKey.setKey(publicKeyE);
        massagePublicKey.setMod(publicKeyN);
        if(!date.containsKey(toID)){
            date.put(toID,new ConcurrentLinkedQueue<>());
            //TODO not correct id
        }
        date.get(toID).add(massagePublicKey);
        log.debug("-READER_"+id+"_MASSAGE_PUBLIC_KEY_WAS_WRITE_IN_QUEUE-");
    }
}
