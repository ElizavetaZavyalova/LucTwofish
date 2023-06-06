package org.example.Algoritmes.modes;

import org.example.Algoritmes.Twofish.Twofish;
import lombok.extern.slf4j.Slf4j;
import org.example.Algoritmes.modes.CBC.CBCMode;
import org.example.Algoritmes.modes.OFB.OFBMode;
import org.example.Algoritmes.modes.RD.RDMode;
import org.example.Algoritmes.modes.CFB.CFBMode;
import org.example.Algoritmes.modes.CTR.CTRMode;
import org.example.Algoritmes.modes.ECB.ECBMode;
import org.example.TwofishSettings;
import org.example.Algoritmes.modes.enums.Mode;
import org.example.Algoritmes.modes.enums.PaddingType;
import org.example.client.client.ClientInputStream;
import org.example.client.client.ClientOutputStream;

import java.io.*;
import java.util.concurrent.*;


@Slf4j
public record BaseModeEncryption() {
    private static BaseMode getMode(TwofishSettings settings){
        switch (settings.getMode()){
            case CBC -> {
                return new CBCMode(settings);
            }
            case CFB -> {
                return new CFBMode(settings);
            }
            case CTR -> {
                return new CTRMode(settings);
            }
            case OFB -> {
                return new OFBMode(settings);
            }
            case RD -> {
                return new RDMode(settings, false);
            }
            case RDH -> {
                return  new RDMode(settings,true);
            }
            default -> {return new ECBMode(settings);}
        }
    }

    public static void encrypt(File selectedFile, ClientOutputStream output, TwofishSettings settings) {
            try (BufferedInputStream input  =new BufferedInputStream(new FileInputStream(selectedFile))) {
                getMode(settings).encrypt(input, output);
            } catch (IOException | ExecutionException | InterruptedException e) {
                log.debug("encrypt"+e.getMessage());
            }
        log.debug("ENCYPT_DONE");
    }

    public static void decrypt(ClientInputStream input, String outFile, Twofish twofish){
        int paddingInf=1,modInf=0, countInf=2; TwofishSettings settings=new TwofishSettings(Mode.ECB, PaddingType.PCS7,twofish);
            try (FileOutputStream output = new FileOutputStream(outFile)) {
                 byte[] information=new byte[settings.encryption.getCountBlock()];
                 input.read(information);
                 information=settings.encryption.decrypt(information);
                 settings.setCountByte((int)information[countInf]);
                 settings.setPaddingType(PaddingType.getPadding((int)information[paddingInf]));
                 settings.setMode(Mode.getMode((int)information[modInf]));
                 getMode(settings).decrypt(input, output);
            } catch (IOException | ExecutionException | InterruptedException e) {
                log.debug("decrypt"+e.getMessage());
                e.printStackTrace();
            }
        log.debug("DECYPT_DONE");
    }
}
