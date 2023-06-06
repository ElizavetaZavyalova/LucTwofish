package org.example.Algoritmes.LUC;

import org.example.Algoritmes.LUC.simbols.Symbol;
import lombok.*;

import java.math.BigInteger;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LUCKey {
    BigInteger e;
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  D{
        BigInteger p;
        BigInteger q;
        static final int pPqP=0,pMqP=1,pMqM=2,pPqM=3;

        BigInteger[] d=new BigInteger[4];
        

        private static class GSDResult{
            BigInteger k;
            BigInteger d;
            BigInteger getResult(){
                if(d.compareTo(BigInteger.ZERO)<0) {
                    return k;
                }
                return d;
            }
        }
        BigInteger gcd(BigInteger e, BigInteger s, GSDResult gSDResult){
            if (e.compareTo(BigInteger.ZERO)==0)
            {
                gSDResult.d=BigInteger.ZERO;
                gSDResult.k=BigInteger.ONE;
                return s;
            }
            GSDResult currentGSDResult=new GSDResult();
            BigInteger nod = gcd(s.mod(e),e, currentGSDResult);
            gSDResult.d=currentGSDResult.k.subtract(s.divide(e).multiply(currentGSDResult.d));
            gSDResult.k=currentGSDResult.d;
            return nod;
        }
        public boolean canMake(BigInteger e){
            d[0]=lcm(p.add(BigInteger.ONE),q.add(BigInteger.ONE));//(p-(-1))(q-(-1))
            d[1]=lcm(p.subtract(BigInteger.ONE),q.add(BigInteger.ONE));//(p-(+1))(q-(-1))
            d[2]=lcm(p.subtract(BigInteger.ONE),q.subtract(BigInteger.ONE));//(p-(+1))(q-(+1))
            d[3]=lcm(p.add(BigInteger.ONE),q.subtract(BigInteger.ONE));//(p-(-1))(q-(+1))
            GSDResult res=new GSDResult();
            for(int i=0;i<4;i++) {
                gcd(e, d[i], res);
                d[i] = res.d;
                if (d[i].compareTo(BigInteger.ZERO) < 0) {
                    return false;
                }
            }
            return true;

        }
        BigInteger lcm(BigInteger a, BigInteger b){
            return a.multiply(b).divide(a.gcd(b));//(a*b)/gcd(a,b)
        }
        public BigInteger getN(){
            return p.multiply(q);
        }

        public BigInteger getD(BigInteger massege){
            BigInteger D= massege.multiply(massege).subtract(BigInteger.valueOf(4)); //m*m-4
            int pD= Symbol.L(D,p);
            int pQ= Symbol.L(D,q);
            if(pD==-1&&pQ==-1){
                return d[pPqP];
            }
            else if(pD==1&&pQ==-1){
                return d[pMqP];
            }
            else if(pD==-1&&pQ==1){
                return d[pPqM];
            }
            return d[pMqM];
        }

    }
    D dKeys=new D();

    public BigInteger getD(BigInteger massege){
        return dKeys.getD(massege);
    }

    public BigInteger[] getEncryptPair(){
        return new BigInteger[]{getE(), getN()};
    }
    public BigInteger[] getDecryptPair(BigInteger massege){
        return new BigInteger[]{dKeys.getD(massege), getN()};
    }

    BigInteger n;
}
