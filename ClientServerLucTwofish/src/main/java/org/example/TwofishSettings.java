package org.example;
import javafx.scene.control.ProgressBar;
import lombok.Getter;
import lombok.Setter;
import org.example.Algoritmes.Twofish.Interfase.BaseEncryption;
import org.example.Algoritmes.Twofish.RoundKeysForTwofish;
import org.example.Algoritmes.Twofish.Twofish;
import org.example.Algoritmes.modes.Settings.padding.Padding;
import org.example.Algoritmes.modes.Settings.padding.PaddingPKCS7;
import org.example.Algoritmes.modes.Settings.padding.PaddingANSIX923;
import org.example.Algoritmes.modes.Settings.padding.PaddingISO10126;
import org.example.Algoritmes.modes.enums.Mode;
import org.example.Algoritmes.modes.enums.PaddingType;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Map.entry;


public class TwofishSettings {
    Random rnd=new Random();

    public ExecutorService service;
    @Getter
    @Setter
    public BaseEncryption encryption;
   // @Setter
    //LoadProgressVbox loadProgressVbox;
   static public ProgressBar loadProgressBar;

    @Getter
    @Setter
    Mode mode;
    protected Map<PaddingType, Padding> padding = Map.ofEntries(entry(PaddingType.ISO_10126, new PaddingISO10126()),
            entry(PaddingType.PCS7,new PaddingPKCS7()), entry(PaddingType.ANSI_X923, new PaddingANSIX923()));
    @Getter
    @Setter
    protected PaddingType paddingType;
    @Setter
    protected byte[] initialBlock;
    @Setter
    protected int countByte;
    public void eddPadding(byte[]block) {
        if (block.length > 1) {
            padding.get(paddingType).eddPadding(block);
        }
    }
    public byte[] deletePadding(byte[]block){
        if(block.length>1) {
          return   padding.get(paddingType).deletePadding(block);
        }
        return block;
    }

    public TwofishSettings(Mode mode, PaddingType paddingType, BaseEncryption encryption) {
        this.mode=mode;
        this.service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        this.setPaddingType(paddingType);
        this.setEncryption(encryption);
        this.countByte=17;
    }
    public TwofishSettings(TwofishSettings settings){
        this.mode=settings.getMode();
        this.service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        this.initialBlock= settings.initialBlock;
        this.countByte= settings.countByte;
        this.setPaddingType(settings.getPaddingType());
        this.setEncryption(settings.encryption);
    }
    public void setKey(byte[] key)throws IllegalArgumentException{
        encryption=new Twofish(new RoundKeysForTwofish());
        encryption.makeRoundKeys(key);
    }
    public int getCountByte() {
        if(countByte>encryption.getCountBlock()){
            this.countByte= rnd.nextInt(1, encryption.getCountBlock());
        }
        return countByte;
    }
    public byte[] makeRandomBlock(){
        byte[] block=new byte[encryption.getCountBlock()];
        for (int b=0; b<block.length;b++){
            block[b]=(byte)(rnd.nextInt(256));
        }
        return block;
    }
    public byte[] getInitialBlock(){
        if(initialBlock==null||initialBlock.length!=encryption.getCountBlock()){
            this.initialBlock= makeRandomBlock();
        }
        return initialBlock;
    }
}
