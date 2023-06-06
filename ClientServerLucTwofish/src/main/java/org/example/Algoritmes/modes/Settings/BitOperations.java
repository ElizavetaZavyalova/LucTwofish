package org.example.Algoritmes.modes.Settings;
public record BitOperations() {
    public static byte[] makeXor(byte[] blockLeft,byte[]blockRight){

        byte[] newBlock=new byte[blockLeft.length];

        for(int i=0;i< blockLeft.length;i++){
            newBlock[i]=(byte)(blockLeft[i]^blockRight[i]);
        }
        return newBlock;
    }
    public static byte[] makePermutation(byte[] block, int[] p){
        byte[] newBlock=new byte[p.length/8];
        for (int bitNumber=0;bitNumber<p.length;bitNumber++) {
            if(p[bitNumber]!=-1) {
                byte b = getBitFromByteArray(p[bitNumber] - 1, block);
                setBitToByteArray(bitNumber, newBlock, b);
            }
        }
        return newBlock;
    }
    public static byte makeReplace(byte block, byte[][]s, int k){
        int selection=(1<<k)-1;
        int stringIndex = (block&selection&0xF0)>>4;
        int columnIndex = block&0x0F;
        return s[stringIndex][columnIndex];
    }
    public static byte[] makeCircularShift(byte[] block, int shift){
        byte[]result=new byte[block.length];
        if(shift<0){
            shift+=result.length;
        }
        System.arraycopy(block, shift, result, 0, result.length - shift);
        System.arraycopy(block, 0, result, result.length - shift, shift);
        return result;
    }
    public static int makeByteXor(byte number){
        int result=number&0xFF;
        for(int i=4;i>0; i=i>>1){
            int value=(1<<i)-1;
            result=((result&(value<<i))>>i)^(result&(value));
        }
        return result;
    }
    public static byte getBitFromByteArray(Integer index,byte[] block){
        int biteIndex=(index%8+1);
        int elemIndex=index/8;
        return (byte)(((block[elemIndex]&0xFF)>>(8-biteIndex))&1);
    }
    public static void setBitToByteArray(Integer index, byte[] block, byte b){
        int biteIndex=(index%8+1);
        int elemIndex=index/8;
        b=(byte)((b& 0xFF)<<(8-biteIndex));
        block[elemIndex]=(byte)(block[elemIndex]|(b& 0xFF));
    }

}
