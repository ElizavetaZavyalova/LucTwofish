package org.example.Algoritmes.modes.Settings.padding;

import java.util.Random;

public class PaddingISO10126 implements Padding {
    Random rnd=new Random();
    @Override
    public void eddPadding(byte[] block) {
        int emptyBlock=block.length;
        for(int i=(block.length-1); (i>=0)&&(block[i]==0);i--){
            emptyBlock--;
        }
        for(int i=emptyBlock; i<block.length-1;i++){
            block[i]= (byte)(rnd.nextInt(256));
        }
        block[block.length-1]=(byte)(block.length-emptyBlock-1);

    }

    @Override
    public byte[] deletePadding(byte[] block) {
        byte[] result=new byte[block[block.length-1]];
        System.arraycopy(block, 0, result, 0, result.length);
        return result;
    }
}
