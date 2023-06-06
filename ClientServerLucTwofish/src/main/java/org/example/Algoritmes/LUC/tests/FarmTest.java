package org.example.Algoritmes.LUC.tests;

import java.math.BigInteger;
import java.util.Random;

public class FarmTest extends SimplicityTest {
    public FarmTest(){
        super(new Random(),0.5F);
    }
    @Override
    boolean isNumberSimple(BigInteger number){
        BigInteger exponent=number.subtract(BigInteger.ONE);
        BigInteger a=nextRandomBigInteger(2,exponent.subtract(BigInteger.ONE));
        if(!a.gcd(number).equals(BigInteger.ONE)){
            return false;
        }
        if(!a.modPow(exponent,number).equals(BigInteger.ONE)){
            return false;
        }
        return true;
    }
}
