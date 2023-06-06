package org.example.Algoritmes.modes.OFB;




import org.example.Algoritmes.modes.BaseMode;
import org.example.Algoritmes.modes.Settings.BitOperations;
import org.example.TwofishSettings;
import org.example.client.client.ClientInputStream;
import org.example.client.client.ClientOutputStream;
import org.example.Algoritmes.modes.enums.Mode;
import org.example.Algoritmes.modes.enums.PaddingType;

import java.io.*;

public class OFBMode extends BaseMode {

    public OFBMode(TwofishSettings settings) {
        super(settings);
    }
    @Override
    public void encrypt(BufferedInputStream input,  ClientOutputStream output) throws IOException {//-
        byte[]bytes=new byte[settings.getCountByte()];
        byte[]currentBlock=settings.getInitialBlock();
        byte[]information=this.getModInformation(Mode.getInt(Mode.OFB), PaddingType.getInt(settings.getPaddingType()));
        output.write(settings.encryption.encrypt(information));
        output.write(settings.encryption.encrypt(currentBlock));
        while(!isFileEnd(input,bytes)) {
            currentBlock=settings.encryption.encrypt(currentBlock);
            bytes= BitOperations.makeXor(bytes,currentBlock);
            output.write(bytes);
            bytes=new byte[settings.getCountByte()];
        }
        output.flush();
    }
    @Override
    public void decrypt(ClientInputStream input, FileOutputStream output) throws IOException {//-
        byte[]bytes=new byte[settings.getCountByte()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        while(input.read(bytes)!=-1) {
            currentBlock=settings.encryption.encrypt(currentBlock);
            bytes= BitOperations.makeXor(bytes,currentBlock);
            if(input.available()<=0) {
                bytes=settings.deletePadding(bytes);
            }
            output.write(bytes);
            bytes=new byte[settings.getCountByte()];
        }
    }
}
