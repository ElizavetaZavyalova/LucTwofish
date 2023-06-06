package org.example.Algoritmes.Twofish;


import org.example.Algoritmes.Twofish.Interfase.BaseEncryption;
import org.example.Algoritmes.Twofish.help.ConstantValues;
import org.example.Algoritmes.Twofish.help.H;
import org.example.Algoritmes.Twofish.help.MatrixMult;
import org.example.Algoritmes.Twofish.help.RO;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

public class Twofish implements BaseEncryption {
    RoundKeysForTwofish roundKeysForTwofish;
    ArrayList<Integer>roundKeys;
    public Twofish(RoundKeysForTwofish roundKeysForTwofish){
        this.roundKeysForTwofish=roundKeysForTwofish;
    }
    byte[][]rS= ConstantValues.makeRS();

    int[]sBox=null;

    public void makeS(byte[]roundKey){
        sBox=new int[roundKey.length>>3];
        for(int i=0; i<roundKey.length;i+=8) {
            byte[] mi = new byte[8];
            System.arraycopy(roundKey,i,mi,0,8);
            sBox[i>>3]= MatrixMult.makeMult(rS,ByteBuffer.wrap(mi).getLong(),(byte)0b1001101);
        }
    }


    @Override
    public void makeRoundKeys(byte[] key) throws IllegalArgumentException {
         makeS(key);
         roundKeys=roundKeysForTwofish.generateRoundKeys(key);
    }
    int[] whitening(int[] block, int from){
        for(int i=from; i<from+block.length;i++){
            block[i-from]^=roundKeys.get(i);
        }
        return block;
    }
    @Override
    public int getCountBlock() {
        return 16;
    }
    int[] splitTheBlock(byte[] block){
        int[]result =new int[4];
        for(int i=0; i<result.length; i++){
            byte[] resultBlock=new byte[block.length>>2];
            System.arraycopy(block,i*resultBlock.length,resultBlock,0,resultBlock.length);
            result[i]=ByteBuffer.wrap(resultBlock).getInt();
        }
        return result;
    }
    byte[] mergeTheBlock(int[] block){
        byte[]result =new byte[16];
        for(int i=0; i<block.length;i++){
            byte[] array=ByteBuffer.allocate(4).putInt(block[i]).array();
            System.arraycopy(array,0,result,i*array.length,array.length);
        }
        return result;
    }
    int[] f(int block0, int block1){
        int text0 = H.doH(block0,sBox);
        int num= (int) RO.rOL(block1,8,32);
        int text1= H.doH(num,sBox);
        return new int[]{text0,text1};
    }
    int[] pHP(int[] text){
        int[] resault=new int[2];
        resault[0]=text[0]+text[1];
        resault[1]=text[0]+(text[1]<<1);
        return resault;
    }
    static Random rand=new Random();
    public static byte[] createKey(){
        byte[] key=new byte[16];
        for(int b=0;b<key.length;b++){
            key[b]=(byte)rand.nextInt(256);
        }
        return key;
    }

    @Override
    public byte[] encrypt(byte[] block) throws IllegalArgumentException{
        int[] splitBlock=splitTheBlock(block);
        splitBlock=whitening(splitBlock, 0);
        for (int round = 0; round < 16; round++) {
            int[] text=f(splitBlock[0],splitBlock[1]);
            int[] pHPResult=pHP(text);
            pHPResult[0]+=roundKeys.get((round<<1) + 8);
            pHPResult[1]+=roundKeys.get((round<<1)+ 9);
            splitBlock[2]^=pHPResult[0];
            splitBlock[2]=(int)RO.rOR(splitBlock[2],1,32);
            splitBlock[3] = ((int)RO.rOL(  splitBlock[3], 1,32))^pHPResult[1];
            splitBlock  = swap(splitBlock);
        }

        return mergeTheBlock(whitening(splitBlock, 4));
    }
    int[] swap(int[] splitBlock){
        int swap0 = splitBlock[0];
        int swap1 = splitBlock[1];
        splitBlock[0] = splitBlock[2];
        splitBlock[1] = splitBlock[3];
        splitBlock[2] = swap0;
        splitBlock[3] = swap1;
        return splitBlock;
    }


    @Override
    public byte[] decrypt(byte[] block) throws IllegalArgumentException{
        int[] splitBlock=splitTheBlock(block);
        splitBlock=whitening(splitBlock, 4);
        for (int round = 15; round>=0; round--) {
            splitBlock=swap(splitBlock);
            int[] text=f(splitBlock[0],splitBlock[1]);
            int[] pHPResult=pHP(text);
            pHPResult[0]+=roundKeys.get((round<<1) + 8);
            pHPResult[1]+=roundKeys.get((round<<1) + 9);
            splitBlock[2] = ((int)RO.rOL(  splitBlock[2], 1,32))^pHPResult[0];
            splitBlock[3]^=pHPResult[1];
            splitBlock[3]=(int)RO.rOR(splitBlock[3],1,32);
        }
        return mergeTheBlock(whitening(splitBlock, 0));
    }


}
