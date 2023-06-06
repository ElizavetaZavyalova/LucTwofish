package org.example.Algoritmes.Twofish;

import org.example.Algoritmes.Twofish.Interfase.BaseRoundKeysGeneration;
import org.example.Algoritmes.Twofish.help.H;
import org.example.Algoritmes.Twofish.help.RO;


import java.util.ArrayList;

public class RoundKeysForTwofish implements BaseRoundKeysGeneration {
    static final int e=0;
    static final int o=1;
    int[][]makeM(byte[] roundKey){
        int[][]m={new int[roundKey.length>>3],new int[roundKey.length>>3]};
        for(int i=0; i<roundKey.length;i+=4){
            int mi=0;
            for(int j=0;j<4; j++){
                mi|=(roundKey[i+((3-j)&0b11)]<<((3-j)*8));
            }
            m[(i>>2)&1][(i>>3)]=mi;
        }
        return m;
    }
    @Override
    public ArrayList<Integer> generateRoundKeys(byte[] roundKey) {
        int[][] m=makeM(roundKey);
        ArrayList<Integer> k=new ArrayList<>();
        for(int i=0; i<20;i++){
            int p2i=0x01010101*(i<<1);
            int a= H.doH(p2i ,m[e]);
            int p2i1=0x01010101*((i<<1)|1);
            int b=(int) RO.rOL(H.doH(p2i1,m[o]),8,32);
            k.add(a+b);
            k.add((int) RO.rOL(a+ 2L *b,9,32));
        }
        return k;
    }


}
