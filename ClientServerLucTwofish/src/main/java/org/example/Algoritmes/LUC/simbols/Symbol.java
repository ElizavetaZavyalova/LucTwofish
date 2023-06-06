package org.example.Algoritmes.LUC.simbols;

import java.math.BigInteger;

public class Symbol{
    static int makeL(BigInteger a, BigInteger p){
        if(a.equals(BigInteger.ONE)){
            return 1;
        }
        if(isEven(a)){
            BigInteger signNum=((p.multiply(p)).subtract(BigInteger.ONE)).shiftRight(3);
            return makeL(a.shiftRight(1),p)*(pow1(signNum));
        }
        BigInteger signNum=(a.subtract(BigInteger.ONE)).multiply(p.subtract(BigInteger.ONE)).shiftRight(2);
        return makeL(p.mod(a),a)*(pow1(signNum));
    }
    public static int L(BigInteger a, BigInteger p){
        return makeL(a,p);
    }
    static boolean isEven(BigInteger number){
        //number четное?
        return (modOfNumPow2(number,2))==0;
    }
    static int makeJ(BigInteger a, BigInteger n){
        a = a.mod(n);
        int result = 1;
        while (!a.equals(BigInteger.ZERO)) {
            while (isEven(a)) {
                a =a.shiftRight(1);
                if (modOfNumPow2(n,8)==3 ||modOfNumPow2(n,8)==5) {
                    result = -result;
                }
            }
            BigInteger swapVariable = n;
            n = a;
            a = swapVariable;
            if (modOfNumPow2(a,4)==3&&modOfNumPow2(n,4)==3){
                result= -result;
            }
            a = a.mod(n);
        }
        if (n.equals(BigInteger.ONE)) {
            return result;
        }
        return 0;
    }
    static int modOfNumPow2(BigInteger number, int num){
        return  (number.toByteArray()[number.toByteArray().length-1]&(num-1));
    }
    static int pow1(BigInteger number){
        //(-1)^number;
        if(!isEven(number)){
           return  -1;
        }
        return 1;
    }
    public static int J(BigInteger a, BigInteger n){
        return  makeJ(a,n);
    }
}
