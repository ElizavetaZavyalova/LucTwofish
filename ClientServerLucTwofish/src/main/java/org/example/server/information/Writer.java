package org.example.server.information;

import org.example.massege.*;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.Algoritmes.debug.DebugFunctions;

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Builder
public class Writer implements Runnable {
    Integer id;
    DataOutputStream dataOutputStream;
    Stop exit;
    Stop serverStop;
    ConcurrentLinkedQueue<Massege> date;
    @Override
    public void run() {
        log.debug("-WRITER_"+id+"_START-");
        try {
        while((!exit.stop)&&(!serverStop.stop)) {
            if(!date.isEmpty()) {
                writeMassage(date.poll());
            }
        }
        } catch (IOException e) {
            log.debug("-WRITER_"+id+"_"+e.getMessage());
        }
        stopWriter();
        log.debug("-WRITER_"+id+"_STOP-");
    }
    void stopWriter(){
        try {
            dataOutputStream.flush();
            exit.stop=true;
            dataOutputStream.close();
        } catch (IOException e) {
            log.debug("-WRITER_"+id+"_NOT_CLOSE-");
        }

    }
    void writeMassage(Massege massage) throws IOException {
        log.debug("-WRITER_"+id+"_READ_MASSAGE_"+Commands.getNameForDebug(massage.getCommand())+"-");
        switch (massage.getCommand()){
            case(Commands.PUBLIC_KEY):{
                assert massage instanceof MassagePublicKey;
                publicKey((MassagePublicKey) massage);//public key
                return;
            }
            case(Commands.CHANEL_KEY):{
                assert massage instanceof MassegeKey;
                chanelKey((MassegeKey) massage);
                return;
            }
            case(Commands.ACCOUNT):{
                assert massage instanceof MassageUser;
                writeAccountInformation((MassageUser)massage);
                return;
            }
            case(Commands.NEW_FILE):{
                assert massage instanceof MassegeNewFile;
                newFile((MassegeNewFile)massage);
                return;
            }
            default:{
                log.debug("-NOT_CORRECT_READ"+id+"_"+massage.getCommand()+"-");
            }
        }
    }
    private void newFile(MassegeNewFile massage) throws IOException {
        dataOutputStream.writeInt(massage.getCommand());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_COMMAND_"+Commands.getNameForDebug(massage.getCommand()));
        dataOutputStream.writeInt(massage.getToId());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_TO_ID"+ massage.getToId());
        dataOutputStream.writeInt(massage.getFileId());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_FILE_ID"+ massage.getFileId());
        dataOutputStream.writeInt(massage.getFileName().length);dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_FILE_NAME_SIZE"+massage.getFileName().length);
        dataOutputStream.write(massage.getFileName());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_FILE_NAME"+new String(massage.getFileName()));
    }
    private void writeAccountInformation(MassageUser massage) throws IOException {
        //TODO not nead mabe
        dataOutputStream.writeInt(massage.getCommand());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_COMMAND_"+Commands.getNameForDebug(massage.getCommand()));
        dataOutputStream.writeInt(massage.getId());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_ACCOUNT_COMMAND_"+ AddRemoveCommands.getCommandForDebug(massage.getCommand()));
        dataOutputStream.writeInt(massage.getId());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_USER_ID_"+massage.getId()+":"+massage.getNameToDebug());
        dataOutputStream.writeInt(massage.getUserName().length);dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_USER_NAME_SIZE"+massage.getUserName().length);
        dataOutputStream.write(massage.getUserName());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_USER_NAME"+DebugFunctions.byteArrayToHexString(massage.getUserName()));
    }
    private void chanelKey(MassegeKey massage) throws IOException {
        dataOutputStream.writeInt(massage.getCommand()); dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_COMMAND_"+Commands.getNameForDebug(massage.getCommand()));
        dataOutputStream.writeInt(massage.getFrom());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_FROM_"+massage.getFrom());
        dataOutputStream.writeInt(massage.getKey().length);dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_CHANEL_KEY_SIZE"+massage.getKey().length);
        dataOutputStream.write(massage.getKey());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_CHANEL_KEY"+DebugFunctions.byteArrayToHexString(massage.getKey()));
    }
    private void publicKey(MassagePublicKey massage) throws IOException {
        dataOutputStream.writeInt(massage.getCommand());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_COMMAND_"+Commands.getNameForDebug(massage.getCommand()));
        dataOutputStream.writeInt(massage.getFrom());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_FROM_"+massage.getFrom());
        //WRITE E
        dataOutputStream.writeInt(massage.getKey().length);dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_PUBLIC_KEY_E_SIZE"+massage.getKey().length);
        dataOutputStream.write(massage.getKey());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_PUBLIC_E_KEY"+DebugFunctions.byteArrayToHexString(massage.getKey()));
        //WRITE N
        dataOutputStream.writeInt(massage.getMod().length);dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_PUBLIC_KEY_N_SIZE"+massage.getKey().length);
        dataOutputStream.write(massage.getMod());dataOutputStream.flush();
        log.debug("-WRITER_"+id+"_WRITE_PUBLIC_N_KEY"+DebugFunctions.byteArrayToHexString(massage.getKey()));
    }


}
