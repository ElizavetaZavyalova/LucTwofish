package org.example.Algoritmes.modes;
import javafx.application.Platform;
import org.example.TwofishSettings;
import org.example.client.client.ClientInputStream;
import org.example.client.client.ClientOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class BaseMode {
    protected TwofishSettings settings=null;
    private boolean eddPadding=false;
    protected List<Future<byte[]>> futures=new ArrayList<>();

    protected BaseMode(TwofishSettings settings) {
        this.settings=settings;

    }
    protected byte[] getModInformation(int mod, int padding){
        byte[] information=new byte[settings.encryption.getCountBlock()];
        information[0]=(byte)mod;
        information[1]=(byte)padding;
        information[2]=(byte)settings.getCountByte();
        settings.eddPadding(information);
        return information;
    }
    protected boolean isFileEnd(BufferedInputStream input,byte[]bytes) throws IOException {
        if(bytes.length<=1){
            return input.read(bytes)==-1;
        }
        if(input.available()<bytes.length){
            if(eddPadding){
                input.read(bytes);
                settings.eddPadding(bytes);
                eddPadding=false;
                return false;
            }
            return true;
        }
        if(input.available()<2*bytes.length){
            eddPadding=true;
            input.read(bytes);
            return false;
        }
        input.read(bytes);
        return false;
    }
    protected double len=0;
    protected void setProgress(int count){
        len=(futures.size()==0)?(1):((double) count)/((double)futures.size());
        Platform.runLater(() -> TwofishSettings.loadProgressBar.setProgress(len));
    }
    protected  void writingAll(FileOutputStream output) throws ExecutionException, InterruptedException, IOException {
        int count=0;
        for(int rezult=0; rezult<futures.size()-1; rezult++) {
            output.write(futures.get(rezult).get());count++;
            setProgress(count);
        }
        byte[]block=settings.deletePadding(futures.get(futures.size()-1).get());
        output.write(block);count++;
        setProgress(count);
    }
    public abstract void encrypt(BufferedInputStream input, ClientOutputStream output) throws IOException, ExecutionException, InterruptedException;
    public abstract void decrypt(ClientInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException;
    public abstract static class ModeRun implements Callable<byte[]> {
        protected byte[] block;
        protected Object[]objects;

        protected ModeRun(byte[] block,Object...objects){
            this.block=block;
            this.objects=objects;
        }
    }
}
