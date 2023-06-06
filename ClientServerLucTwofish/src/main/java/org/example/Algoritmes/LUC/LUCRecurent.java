package org.example.Algoritmes.LUC;

import java.math.BigInteger;

public record LUCRecurent() {
        public static BigInteger v(BigInteger p, BigInteger e, BigInteger mod) {
            BigInteger vN_1=BigInteger.TWO;
            BigInteger vN=p;
            for(int bit=e.bitLength()-2;bit>=0;bit--){
                if(!e.testBit(bit)){
                    BigInteger v2n=(vN).multiply(vN).subtract(BigInteger.TWO);
                    BigInteger v2n_1=vN.multiply(vN_1).subtract(p);
                    vN=v2n.mod(mod);
                    vN_1=v2n_1.mod(mod);
                }
                else{
                    BigInteger vNx1=vN.multiply(p).subtract(vN_1);
                    BigInteger v2nX1=vN.multiply(vNx1).subtract(p);
                    BigInteger v2n=(vN).multiply(vN).subtract(BigInteger.TWO);
                    vN=v2nX1.mod(mod);
                    vN_1=v2n.mod(mod);
                }
            }
            return vN.mod(mod);
        }

        static boolean   isEven(BigInteger e){
            return (e.toByteArray()[e.toByteArray().length-1]&1)==0;
        }

}
