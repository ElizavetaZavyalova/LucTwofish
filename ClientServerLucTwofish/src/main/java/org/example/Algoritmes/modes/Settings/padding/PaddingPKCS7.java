package org.example.Algoritmes.modes.Settings.padding;


public class PaddingPKCS7 implements Padding {
    @Override
    public void eddPadding(byte[] block) {
        int emptyBlock=block.length;
        for(int i=(block.length-1); (i>=0)&&(block[i]==0);i--){
            emptyBlock--;
        }
        for(int i=emptyBlock; i<block.length;i++){
            block[i]=(byte)(block.length-emptyBlock);
        }
    }

    @Override
    public byte[] deletePadding(byte[] block) {
        byte[] result=new byte[block.length-block[block.length-1]];
        System.arraycopy(block, 0, result, 0, result.length);
        return result;
    }
}
