package org.example.Algoritmes.LUC;


import org.example.Algoritmes.LUC.tests.FarmTest;
import org.example.Algoritmes.LUC.tests.MillerRabinTest;
import org.example.Algoritmes.LUC.tests.SimplicityCheck;
import org.example.Algoritmes.LUC.tests.SolovayStrassenTest;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LUC {
    static class GenerateKeys {
        Map<SimplicityMode, SimplicityCheck> simplicityModes = new HashMap<>();
        Random random=new Random();
        @Setter
        SimplicityMode testMode;
        @Setter
        int bitLength;
        @Setter
        float probability;
        public GenerateKeys(SimplicityMode testMode, float probability, int biteLength){
            setProbability(probability);
            setTestMode(testMode);
            setBitLength(biteLength);
            simplicityModes.put(SimplicityMode.farm,new FarmTest());
            simplicityModes.put(SimplicityMode.millerRabin,new MillerRabinTest());
            simplicityModes.put(SimplicityMode.solovayStrassen,new SolovayStrassenTest());

        }
        BigInteger generateBigInteger(){
            return BigInteger.probablePrime(bitLength,random);
            //return new BigInteger(bitLength,random).setBit(bitLength-1).setBit(0);
        }
        boolean isBigIntegerSizeCorrect(BigInteger number){
            return number.bitLength()==bitLength;
        }


        BigInteger generateSimpleBigInteger(BigInteger generate){
            while(!simplicityModes.get(testMode).isSimple(generate,probability)){
                generate=generate.add(BigInteger.TWO);
                if(!isBigIntegerSizeCorrect(generate)){
                    generate=BigInteger.ONE.shiftLeft(bitLength-1).setBit(0);
                }
            }
            return generate;
        }
        public LUCKey generateLUCKey(){
            LUCKey lucKey=new LUCKey();
            BigInteger p=generateSimpleBigInteger(generateBigInteger());
            int addLength= random.nextInt(bitLength>>1,(bitLength-1));
            BigInteger addNum=new BigInteger(addLength,random).setBit(addLength>>2-1).clearBit(0);
            BigInteger q=generateSimpleBigInteger(p.add(addNum));
            lucKey.setN(p.multiply(q));
            lucKey.getDKeys().setP(p);
            lucKey.getDKeys().setQ(q);
            do {
                lucKey.setE(makeE(p, q));
            }while(!lucKey.getDKeys().canMake(lucKey.getE()));
            return  lucKey;
        }
        BigInteger makeMul(BigInteger p, BigInteger q){
            BigInteger mul1=p.add(BigInteger.ONE);
            BigInteger mul2=q.add(BigInteger.ONE);
            BigInteger mul3=p.subtract(BigInteger.ONE);
            BigInteger mul4=q.subtract(BigInteger.ONE);
            return mul1.multiply(mul2).multiply(mul3).multiply(mul4);
        }
        BigInteger makeE(BigInteger p, BigInteger q){
            BigInteger mul=makeMul(p,q);
            BigInteger e=generateE(p.bitLength());
            while(mul.gcd(e).compareTo(BigInteger.ONE)!=0){ //gcd(e,(p^2-1)(q^2-1)!=1
                e=generateE(p.bitLength());
            }
            return e;
        }
        public BigInteger generateE(int len){
            int oneCount= random.nextInt(1,7);
            BigInteger e=BigInteger.ONE.shiftLeft(len).setBit(0);
            for(int i=0; i<oneCount; i++) {
                e = e.setBit(random.nextInt(2, len- 1));
            }
            return e;
        }
    }
    GenerateKeys generateKeys=null;
    public LUC(SimplicityMode testMode, float probability, int biteLength){
        generateKeys=new GenerateKeys(testMode,probability,biteLength);
    }
    public void setTestMode(SimplicityMode testMode){
        generateKeys.setTestMode(testMode);
    }
    public void setProbability(float probability){
        generateKeys.setProbability(probability);
    }
    public void setBiteLength(int biteLength){
        generateKeys.setBitLength(biteLength);
    }
    public LUCKey generateKey(){
        return generateKeys.generateLUCKey();
    }
    public static byte[] encrypt(byte[] bytes, BigInteger[] lucKey){
        //Решила шифровать масив bites. по n байт;
        int eValue=0,nValue=1;
        int count=(lucKey[nValue].bitLength()/8);
        int nCount=(lucKey[nValue].bitLength())/8+1;
        int blockCount=bytes.length%count==0?(bytes.length/count):bytes.length/count+1;
        BigInteger e=lucKey[eValue];
        BigInteger n=lucKey[nValue];
        byte[]result=new byte[(nCount)*(blockCount)];
        int index=0;
        for(int i=0; i<bytes.length;i+=count){
            if(bytes.length-i<count){
                count=bytes.length-i;
            }
            BigInteger encryptNum=new BigInteger(1,bytes,i,count);
            encryptNum=LUCRecurent.v(encryptNum,e,n);
            byte[] encryptBytes=encryptNum.toByteArray();
            System.arraycopy(encryptBytes, 0, result, ((index+1)*nCount)-encryptBytes.length,(encryptBytes.length));
            index++;
        }
        return result;
    }
    public static byte[] decrypt(byte[] bytes,LUCKey.D lucKey){
        int nCount=(lucKey.getN().bitLength())/8+1;
        int count=(lucKey.getN().bitLength()/8);
        BigInteger encryptNum=new BigInteger(1,bytes,bytes.length-nCount,nCount);
        BigInteger n=lucKey.getN();
        encryptNum=LUCRecurent.v(encryptNum,lucKey.getD(encryptNum),n);
        byte[] encryptBytes=encryptNum.toByteArray();// возвращает больше на один байт если предыдущие заполнены полностью.. т.е лишний 0 байт
        int srcPos=(encryptBytes[0]==0)?(1):(0);
        int countShift= (encryptBytes[0]==0)?(encryptBytes.length-1):(encryptBytes.length);
        byte[]result=new byte[(count)*(bytes.length/nCount-1)+countShift];
        System.arraycopy(encryptBytes, srcPos, result, (count)*(bytes.length/nCount-1),countShift);
        int index=0;
        for(int i=0; i<bytes.length-nCount;i+=nCount){
            encryptNum=new BigInteger(1,bytes,i,nCount);
            encryptNum=LUCRecurent.v(encryptNum,lucKey.getD(encryptNum),n);
            encryptBytes=encryptNum.toByteArray();
            srcPos=encryptBytes.length>count?(encryptBytes.length-count):0;
            countShift= Math.min(encryptBytes.length, count);
            System.arraycopy(encryptBytes, srcPos, result, index*count,countShift);
            index++;
        }
        return result;
    }

}
