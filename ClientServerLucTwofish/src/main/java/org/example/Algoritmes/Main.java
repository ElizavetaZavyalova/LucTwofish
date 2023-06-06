package org.example.Algoritmes;


import org.example.Algoritmes.Twofish.RoundKeysForTwofish;
import org.example.Algoritmes.Twofish.Twofish;
import org.example.Algoritmes.modes.BaseModeEncryption;
import org.example.TwofishSettings;
import org.example.Algoritmes.modes.enums.Mode;
import org.example.Algoritmes.modes.enums.PaddingType;
import org.example.client.client.ClientInputStream;

public class Main {
    public static void main(String[] args) {

        byte[] k={(byte)0x59, (byte)0x41, (byte)0x44, (byte)0x41, (byte)0x30,
                  (byte)0x36, (byte)0x31, (byte)0x33, (byte)0x50, (byte)0x4a,
                  (byte)0x54, (byte)0x59, (byte)0x32, (byte)0x5a, (byte)0x30,
                  (byte)0x53,(byte)0x59, (byte)0x41, (byte)0x44, (byte)0x41,
                  (byte)0x30, (byte)0x36, (byte)0x31, (byte)0x33};

        Twofish twofish=new Twofish(new RoundKeysForTwofish());
        twofish.makeRoundKeys(k);
        TwofishSettings settings= new TwofishSettings(Mode.RDH, PaddingType.PCS7,twofish);
        String path="./src/testFiles/test.txt";
        String path1="./src/testFiles/test1.txt";
        String path2="./src/testFiles/test2.txt";
        //ClientOutputStream out=new ClientOutputStream(path1);
        //BaseModeEncryption.encrypt(path,out,settings);
    //    ClientInputStream in=new ClientInputStream(path1);
      //  BaseModeEncryption.decrypt(in,path2,twofish);


    }
}
