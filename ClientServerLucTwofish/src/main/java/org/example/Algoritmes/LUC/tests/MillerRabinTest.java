package org.example.Algoritmes.LUC.tests;

import java.math.BigInteger;
import java.util.Random;

public class MillerRabinTest extends SimplicityTest {
    public MillerRabinTest(){
        super(new Random(),0.25F);
    }
    @Override
    boolean isNumberSimple(BigInteger number){
        BigInteger exponent=number.subtract(BigInteger.ONE);
        BigInteger a=nextRandomBigInteger(1,exponent.subtract(BigInteger.ONE));
        if(!a.gcd(number).equals(BigInteger.ONE)){
            return false;
        }
        BigInteger t=exponent;
        long s=0;
        while ((t.toByteArray()[t.toByteArray().length-1]&1)==0){
            s++;
            t=t.shiftRight(1);
        }
      //  BigInteger s=BigInteger.valueOf(count);
        BigInteger aPowT=a.modPow(t, number);
        if(aPowT.equals(exponent)||aPowT.equals(BigInteger.ONE)){
            return true;
        }
        for(int i=1; i<s-1; i++){
            aPowT=aPowT.multiply(aPowT).mod(number);
            if(aPowT.equals(exponent)){
                return true;
            }
        }
        return false;
    }
}
