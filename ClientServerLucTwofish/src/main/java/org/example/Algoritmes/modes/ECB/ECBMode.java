package org.example.Algoritmes.modes.ECB;


import javafx.application.Platform;
import org.example.Algoritmes.modes.BaseMode;
import org.example.TwofishSettings;
import org.example.client.client.ClientInputStream;
import org.example.client.client.ClientOutputStream;
import org.example.Algoritmes.modes.enums.Mode;
import org.example.Algoritmes.modes.enums.PaddingType;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ECBMode extends BaseMode {
    public ECBMode(TwofishSettings settings) {
        super(settings);
    }

    @Override
    public void encrypt(BufferedInputStream input,  ClientOutputStream output) throws IOException, ExecutionException, InterruptedException {//+
        futures.clear();
        byte[]information=this.getModInformation(Mode.getInt(Mode.ECB), PaddingType.getInt(settings.getPaddingType()));
        output.write(settings.encryption.encrypt(information));
        byte [] bytes = new byte [settings.encryption.getCountBlock()];
        while(!isFileEnd(input,bytes)){
            futures.add(settings.service.submit(new ModeRun(bytes) {
                @Override
                public byte[] call(){
                    return settings.encryption.encrypt(block);
                }
            }));
            bytes = new byte [settings.encryption.getCountBlock()];
        }
         int count=0;
        for(Future<byte[]> result:futures) {
            output.write(result.get());count++;
            setProgress(count);
       }
        output.flush();
    }

    @Override
    public void decrypt(ClientInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {//+
        futures.clear();
        byte [] bytes = new byte [settings.encryption.getCountBlock()];
        while(input.read(bytes)!=-1){
            futures.add(settings.service.submit(new ModeRun(bytes) {
                @Override
                public byte[] call(){
                    return settings.encryption.decrypt(block);
                }
            }));
            bytes = new byte [settings.encryption.getCountBlock()];
        }
        writingAll(output);
    }
}
