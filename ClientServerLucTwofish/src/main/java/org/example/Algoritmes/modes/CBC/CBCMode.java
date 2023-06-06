package org.example.Algoritmes.modes.CBC;

import org.example.Algoritmes.modes.BaseMode;
import org.example.Algoritmes.modes.Settings.BitOperations;
import org.example.Algoritmes.modes.enums.Mode;
import org.example.Algoritmes.modes.enums.PaddingType;
import org.example.TwofishSettings;
import org.example.Algoritmes.debug.DebugFunctions;
import org.example.client.client.ClientInputStream;
import org.example.client.client.ClientOutputStream;

import java.io.*;
import java.util.concurrent.ExecutionException;

public class CBCMode extends BaseMode {
    private static final  int CURRENT_BLOCK=0;
    public CBCMode(TwofishSettings settings) {
        super(settings);

    }
    @Override
    public void encrypt(BufferedInputStream input, ClientOutputStream output) throws IOException {//-
           byte[]bytes=new byte[settings.encryption.getCountBlock()];
           byte[]information=this.getModInformation(Mode.getInt(Mode.CBC), PaddingType.getInt(settings.getPaddingType()));
           output.write(settings.encryption.encrypt(information));
           byte[]currentBlock= settings.getInitialBlock();
           DebugFunctions.debugByteHexArray(currentBlock);
           output.write(settings.encryption.encrypt(currentBlock));
           while(!isFileEnd(input,bytes)){
               bytes= BitOperations.makeXor(bytes,currentBlock);
               bytes=settings.encryption.encrypt(bytes);
               output.write(bytes);
               currentBlock=bytes;
               bytes=new byte[settings.encryption.getCountBlock()];
           }
           output.flush();
    }
    @Override
    public void decrypt(ClientInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {//+
        futures.clear();
        byte[]bytes=new byte[settings.encryption.getCountBlock()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        DebugFunctions.debugByteHexArray(currentBlock);
        while(input.read(bytes)!=-1){
            futures.add(settings.service.submit(new ModeRun(bytes.clone(), (Object) currentBlock) {
                @Override
                public byte[] call(){
                    assert(objects[CURRENT_BLOCK] instanceof byte[]);
                    block=settings.encryption.decrypt(block);
                    block= BitOperations.makeXor(block,(byte[])objects[CURRENT_BLOCK]);
                    return block;
                }
                }));
            currentBlock=bytes;
            bytes = new byte [settings.encryption.getCountBlock()];
        }
        writingAll(output);
    }
}
