package org.example.Algoritmes.modes.CTR;

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

public class CTRMode extends BaseMode {
    static private final int CURRENT_BLOCK=0;

    public CTRMode(TwofishSettings settings) {
        super(settings);
    }
    byte[] makeNextCurrentBlock(byte[] currentBlock){
        return currentBlock;
    }

    @Override
    public void encrypt(BufferedInputStream input,  ClientOutputStream output) throws IOException, ExecutionException, InterruptedException {
        futures.clear();
        byte[]currentBlock= settings.getInitialBlock();
        byte[]information=this.getModInformation(Mode.getInt(Mode.CTR), PaddingType.getInt(settings.getPaddingType()));
        output.write(settings.encryption.encrypt(information));
        output.write(settings.encryption.encrypt(currentBlock));
        byte [] bytes = new byte [settings.getCountByte()];
        while(!isFileEnd(input,bytes)){
            currentBlock=makeNextCurrentBlock(currentBlock);
            futures.add(settings.service.submit(new ModeRun(bytes,(Object) currentBlock.clone()) {
                @Override
                public byte[] call(){
                    byte[] currentBlock=settings.encryption.encrypt((byte[])objects[CURRENT_BLOCK]);
                    return BitOperations.makeXor(block,currentBlock);
                }
            }));
            bytes = new byte [settings.getCountByte()];
        }int count=0;
        for(Future<byte[]> result:futures) {
            output.write(result.get());
            count++;
            setProgress(count);
        }
        output.flush();
    }

    @Override
    public void decrypt(ClientInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {
        futures.clear();
        byte[]bytes=new byte[settings.getCountByte()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        while(input.read(bytes)!=-1){
            currentBlock=makeNextCurrentBlock(currentBlock);
            futures.add(settings.service.submit(new ModeRun(bytes,(Object) currentBlock.clone()) {
                @Override
                public byte[] call(){
                    byte[] currentBlock=settings.encryption.encrypt((byte[])objects[CURRENT_BLOCK]);
                    return BitOperations.makeXor(block,currentBlock);
                }
            }));
            bytes=new byte[settings.getCountByte()];
        }
        writingAll(output);
    }
}
