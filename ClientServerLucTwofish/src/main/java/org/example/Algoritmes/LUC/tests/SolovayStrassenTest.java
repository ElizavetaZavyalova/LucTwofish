package org.example.Algoritmes.LUC.tests;

import org.example.Algoritmes.LUC.simbols.Symbol;

import java.math.BigInteger;
import java.util.Random;

public class SolovayStrassenTest extends SimplicityTest {
    public SolovayStrassenTest(){
        super(new Random(),0.5F);
    }
    @Override
     boolean isNumberSimple(BigInteger number){
        BigInteger exponent=number.subtract(BigInteger.ONE);
        BigInteger a=nextRandomBigInteger(1,exponent.subtract(BigInteger.ONE));
        if(!a.gcd(number).equals(BigInteger.ONE)){
            return false;
        }
        if(Symbol.J(a, number) != makeAModPow(a, exponent, number)){
            return false;
        }
        return true;
    }
    private int makeAModPow(BigInteger a,BigInteger exponent, BigInteger number){
        BigInteger aModPow=a.modPow(exponent.shiftRight(1),number);
        if(aModPow.equals(BigInteger.ONE)){
            return 1;
        }
        if(aModPow.equals(BigInteger.ZERO)){
            return 0;
        }
        if(number.subtract(aModPow).equals(BigInteger.ONE)){
            return -1;
        }
        return 3;
    }
    @Deprecated
    private BigInteger makeJPow(BigInteger a, BigInteger n){
        int j= Symbol.J(a,n);
        if(j==1){
            return BigInteger.ONE;
        }
        if(j==-1){
            return n.subtract(BigInteger.ONE);
        }
        return BigInteger.ZERO;
    }
}
