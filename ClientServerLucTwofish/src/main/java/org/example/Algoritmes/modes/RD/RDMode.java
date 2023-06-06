package org.example.Algoritmes.modes.RD;


import org.example.Algoritmes.modes.BaseMode;
import org.example.Algoritmes.modes.Settings.BitOperations;
import org.example.Algoritmes.modes.enums.Mode;
import org.example.Algoritmes.modes.enums.PaddingType;
import org.example.TwofishSettings;
import org.example.client.client.ClientInputStream;
import org.example.client.client.ClientOutputStream;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RDMode extends BaseMode {
    private static final int CURRENT_BLOCK=0;
    boolean hash=false;
    public RDMode(TwofishSettings settings, boolean hash) {
        super(settings);
        this.hash=hash;
    }
    byte[] makeNextCurrentBlock(byte[] currentBlock){
        return currentBlock;
    }
    void eddHash(){
        if(hash) {
            int count = 0;
            while (count != settings.getCountByte()) {
                futures.add(settings.service.submit(new ModeRun(settings.makeRandomBlock()) {
                    @Override
                    public byte[] call() {
                        return settings.encryption.encrypt(block);
                    }
                }));
                count++;
            }
        }
    }
    @Override
    public void encrypt(BufferedInputStream input,  ClientOutputStream output) throws IOException, ExecutionException, InterruptedException {
        futures.clear();
        byte[]currentBlock= settings.getInitialBlock();
        byte[]information=this.getModInformation(Mode.getInt(hash?(Mode.RDH):(Mode.RD)), PaddingType.getInt(settings.getPaddingType()));
        output.write(settings.encryption.encrypt(information));
        output.write(settings.encryption.encrypt(currentBlock));
        byte [] bytes = new byte [settings.encryption.getCountBlock()];
        eddHash();
        while(!isFileEnd(input,bytes)){
            futures.add(settings.service.submit(new ModeRun(bytes,(Object) currentBlock.clone()) {
                @Override
                public byte[] call(){
                    block= BitOperations.makeXor(block,(byte[])objects[CURRENT_BLOCK]);
                    return settings.encryption.encrypt(block);
                }

            }));
            currentBlock=makeNextCurrentBlock(currentBlock);
            bytes = new byte [settings.encryption.getCountBlock()];
        }int count=0;
        for(Future<byte[]> result:futures) {
            output.write(result.get());
            count++;
            setProgress(count);
        }
        output.flush();
    }
    private void missHash(ClientInputStream input) throws IOException {
        if(hash){
            byte[] hash=new byte[settings.encryption.getCountBlock()];
            int count = 0;
            while (count != settings.getCountByte()) {
                input.read(hash);
                count++;
            }

        }
    }
    @Override
    public void decrypt(ClientInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {
        futures.clear();
        byte[]bytes=new byte[settings.encryption.getCountBlock()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        missHash(input);
        while(input.read(bytes)!=-1){
            currentBlock=makeNextCurrentBlock(currentBlock);
            futures.add(settings.service.submit(new ModeRun(bytes,(Object) currentBlock.clone()) {
                @Override
                public byte[] call(){
                    block=settings.encryption.decrypt(block);
                    return BitOperations.makeXor(block,(byte[])objects[CURRENT_BLOCK]);
                }
            }));
            bytes=new byte[settings.encryption.getCountBlock()];
        }
        writingAll(output);
    }
}
