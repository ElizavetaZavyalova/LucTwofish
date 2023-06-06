package org.example.Algoritmes.modes.CFB;

import org.example.Algoritmes.modes.BaseMode;
import org.example.Algoritmes.modes.Settings.BitOperations;
import org.example.Algoritmes.modes.enums.Mode;
import org.example.Algoritmes.modes.enums.PaddingType;
import org.example.TwofishSettings;
import org.example.client.client.ClientInputStream;
import org.example.client.client.ClientOutputStream;

import java.io.*;
import java.util.concurrent.ExecutionException;

public class CFBMode extends BaseMode {

    private static final int CURRENT_BLOCK=0;
    public CFBMode(TwofishSettings settings) {
        super(settings);

    }
    void makeShift(byte[] block){
        for(int i=0;i<block.length-this.settings.getCountByte();i++){
            block[i]=block[i+this.settings.getCountByte()];
        }
    }
    void eddBlock(byte[]block,byte[]eddBlock){
        int firstEmptyBlock=block.length-eddBlock.length;
        for(int i=0;i<eddBlock.length;i++){
            block[i+firstEmptyBlock]=eddBlock[i];
        }
    }
    @Override
    public void encrypt(BufferedInputStream input,  ClientOutputStream output) throws IOException {//-
        byte[]bytes=new byte[this.settings.getCountByte()];
        byte[]currentBlock=settings.getInitialBlock();
        byte[]information=this.getModInformation(Mode.getInt(Mode.CFB), PaddingType.getInt(settings.getPaddingType()));
        output.write(settings.encryption.encrypt(information));
        output.write(settings.encryption.encrypt(currentBlock));
        while(!isFileEnd(input,bytes)){
            byte[]lastBlock=settings.encryption.encrypt(currentBlock);
            bytes= BitOperations.makeXor(bytes,lastBlock);//bytes.length
            output.write(bytes);
            makeShift(currentBlock);
            eddBlock(currentBlock,bytes);
            bytes=new byte[settings.getCountByte()];
        }
        output.flush();
    }

    @Override
    public void decrypt(ClientInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {//+
        futures.clear();
        byte[]bytes=new byte[settings.getCountByte()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        while(input.read(bytes)!=-1){
             futures.add(settings.service.submit(new ModeRun(bytes.clone(), (Object) currentBlock.clone()) {
                     @Override
                     public byte[] call() {
                         assert(objects[CURRENT_BLOCK] instanceof byte[]);
                         byte[] lastBlock = settings.encryption.encrypt((byte[]) objects[CURRENT_BLOCK]);
                         block = BitOperations.makeXor(block, lastBlock);//bytes.length
                         return block;
                     }
             }));
             makeShift(currentBlock);
             eddBlock(currentBlock,bytes);
             bytes=new byte[settings.getCountByte()];
        }
        writingAll(output);
    }
}
