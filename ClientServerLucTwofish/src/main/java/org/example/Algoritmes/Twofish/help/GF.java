package org.example.Algoritmes.Twofish.help;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public record GF() {
    @Getter
    static List<Byte> mode8= GFFunctions.generateSimple();
    private static final int GF_SIZE=0x100;
    public static byte sum(byte value1,byte value2){
        return (byte)(value1^value2);
    }
    public static byte multiplication(final byte value1,final byte value2,final byte module) throws IllegalArgumentException{
        makeException(module);
        return GFFunctions.makeRemainderFromDivision(GFFunctions.multiplicationWithoutModule(value1, value2),(module&0xFF)|(GF_SIZE));
    }

    private static void makeException(final byte module) throws IllegalArgumentException{
        if(!isSimple(module)){
            throw new IllegalArgumentException("Value of"+(GF_SIZE|(module&0xFF))+"must be simple");
        }
    }
    public static boolean isSimple(byte value){
        return mode8.contains(value);
       // return GFFunctions.isSimpleToGenerate(0x100|(value&0xFF),0x1F,GFFunctions.generateSimple0x1F());
    }
    public static byte pow(byte number, int degree, byte module) throws IllegalArgumentException{
        makeException(module);
        return GFFunctions.makePow(number,degree,module);
    }
    public static byte getReverse(byte number,byte module)throws IllegalArgumentException{
        makeException(module);
        return GFFunctions.makePow(number, 254, module);
    }
    public static byte mod(int value,int  module){
       //makeException(module);
        return  GFFunctions.makeRemainderFromDivision(value, module);

    }
    private record GFFunctions() {
        public static List<Byte> generateSimple() {
            ArrayList<Byte> sNumbers = GFFunctions.generateSimple0x1F();
            List<Byte> result = new ArrayList<>();
            for (int p = 0x101; p <= 0x1FF; p += 2) {
                if (isSimpleToGenerate(p, 0x1F, sNumbers)) {
                    result.add((byte) p);
                }
            }
            return result;
        }

        protected static int countBits(int currentValue) {
            int maxNumber = 1;
            while (currentValue >> maxNumber != 0) {
                maxNumber++;
            }
            return maxNumber;
        }

        protected static boolean isSimpleToGenerate(int value, Integer max, ArrayList<Byte> simpleNumbers) {
            for (Byte number : simpleNumbers) {
                if ((number & 0xFF) > (max)) {
                    break;
                }
                if (makeRemainderFromDivision(value, number & 0xFF) == 0) {
                    return false;
                }
            }
            return true;
        }

        protected static byte makeRemainderFromDivision(int value, final int module) {
            int moduleSize = 0;
            while (module >> moduleSize != 0) {
                moduleSize++;
            }
            int result = value;
            while (result >= (GF_SIZE) || (result >= module)) {
                int shift = 1;
                while ((1 << (shift + moduleSize - 1)) <= result) {
                    shift++;
                }
                result = result ^ (module << (shift - 1));
            }
            return (byte) result;
        }

        protected static byte makePow(byte number, int degree, byte mode) throws IllegalArgumentException {
            if (degree == 0)
                return 1;
            if ((degree & 1) == 1)
                return multiplication(makePow(number, degree - 1, mode), number, mode);
            else {
                byte currentNumber = makePow(number, degree >> 1, mode);
                return multiplication(currentNumber, currentNumber, mode);
            }
        }

        protected static ArrayList<Byte> generateSimple0x1F() {
            ArrayList<Byte> result = new ArrayList<>();
            for (byte b = 0b11; b <= 0x1F; b += 2) {
                int shift = ((countBits(b & 0xff) >> 1) + 2);
                if (isSimpleToGenerate(b, shift, result)) {
                    result.add(b);
                }
            }
            return result;
        }
        //11111*11111=101010101 нихрена ни через корень. либо в gf корень ищется по другому.

        protected static int multiplicationWithoutModule(final byte value1, final byte value2) {
            int firstMultiplier = (value1 & (GF_SIZE - 1));
            int secondMultiplier = (value2 & (GF_SIZE - 1));
            int result = 0;
            while (firstMultiplier != 0) {
                result ^= (secondMultiplier * (firstMultiplier & 1));
                firstMultiplier = (firstMultiplier >> 1);
                secondMultiplier = (secondMultiplier << 1);
            }
            return result;
        }
    }

}
