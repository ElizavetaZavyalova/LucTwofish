package org.example.Algoritmes.Twofish.help;

public record H() {
    static byte[][][] q= ConstantValues.makeQ();
    static byte[][]mDS=ConstantValues.makeMDS();
    static int[][]qOrder=ConstantValues.makeQOrder();
    static byte doQ(byte x, int qIndex){
        byte a0 = (byte)((x >> 4)&0x0F);
        byte b0 = (byte)(x&0x0F);
        byte a1 = (byte)(a0 ^ b0);
        byte b1 = (byte)(a0 ^ RO.rOR(b0, 1,4) ^ (a0 << 3));
        byte a2 = q[qIndex][0][a1 & 0xF];
        byte b2 = q[qIndex][1][b1 & 0xF];
        byte a3 = (byte)(a2 ^ b2);
        byte b3 = (byte)(a2 ^ RO.rOR(b2, 1,4) ^ (a2 << 3));
        byte a4 = q[qIndex][2][a3 & 0xF];
        byte b4 = q[qIndex][3][b3 & 0xF];
        return (byte)((b4 << 4)|(a4&0xFF));
    }
    public static int makeQ(int result, int count){
        int resultShift=0;
        for(int j=0;j<4;j++){
            int shift=(3-j)<<3;
            byte val=(byte)(((0xFF<<(shift))&result)>>shift);
            resultShift=(doQ(val,qOrder[count][j])<<shift);
        }
        return resultShift;
    }

    public static int doH(int x, int[]l){
        int result=x;
        int count= qOrder.length-1;
        for(int i=l.length-1;i>=0;i--){
            result=makeQ(result,count)^l[i];
            count--;
        }
        result=makeQ(result,count);
        return MatrixMult.makeMult(mDS,result,(byte)0b0110_1001);
    }
}
