package org.example.Algoritmes.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record DebugFunctions(){
    private static final Logger log= LoggerFactory.getLogger(DebugFunctions.class);
    public static void  debugByteInBin(byte b){
        if (log.isDebugEnabled()) {
            log.debug(byteToBinarryString(b));
        }
    }
    public static String byteToBinarryString(byte b){
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
    public static String byteToHexString(byte b){
        return String.format("%2s", Integer.toHexString(b & 0xFF)).replace(' ', '0');
    }
    public static String intToHexString(int b){
        return String.format("%8s", Integer.toHexString(b & 0xFF_FF_FF_FF)).replace(' ', '0');
    }
    public static String intArrayToHexString(int[] bytes){
        StringBuilder byteString;
        byteString = new StringBuilder();
        for (int aByte : bytes) {
            byteString.append(intToHexString(aByte));
            byteString.append("_");
        }
        return byteString.toString();
    }
    public static void debugIntHex(int bytes){
        if (log.isDebugEnabled()) {
            log.debug(intToHexString(bytes));
        }
    }
    public static void debugIntArrayHex(int[] bytes){
        if (log.isDebugEnabled()) {
            log.debug(intArrayToHexString(bytes));
        }
    }
    public static void debugByteArray(byte[] bytes){
        if (log.isDebugEnabled()) {
            log.debug(byteArrayToBinarryString(bytes));
        }
    }
    public static void debugByteHexArray(byte[] bytes){
        if (log.isDebugEnabled()) {
            log.debug(byteArrayToHexString(bytes));
        }
    }
    public static String byteArrayToHexString(byte[] bytes){
        StringBuilder byteString;
        byteString = new StringBuilder();
        for (byte aByte : bytes) {
            byteString.append(byteToHexString(aByte));
            byteString.append("_");
        }
        return byteString.toString();
    }
    public static String byteArrayToBinarryString(byte[] bytes){
        StringBuilder byteString;
        byteString = new StringBuilder();
        for (byte aByte : bytes) {
            byteString.append(byteToBinarryString(aByte));
            byteString.append("_");
        }
        return byteString.toString();
    }
    public static void debugIndex(Integer index, String description){
        if (log.isDebugEnabled()) {
            log.debug(description,":",index.toString());
        }
    }
    public static void debugByBitsArrayNumber(byte[] bytes){
        StringBuilder biteIndexString;
        biteIndexString = new StringBuilder();
        for(int i=0; i<bytes.length; i++){
            biteIndexString.append(debugByBitsNumber(bytes[i], i));
        }
        if (log.isDebugEnabled()) {
            log.debug(biteIndexString.toString());
        }
    }
    public static void debugByBitsByteNumber(byte b){
        if (log.isDebugEnabled()) {
            log.debug(debugByBitsNumber(b, 0));
        }
    }
    public static void debugWrite(String s){
        if (log.isDebugEnabled()) {
            log.debug("------------------------"+s+"-------------------------------");
        }
    }
    public static String debugByBitsNumber(byte b, int digit){
        StringBuilder biteIndexString;
        biteIndexString = new StringBuilder();
         for(int i=1; i<=8; i++){
             String current = (digit + i) + ":" + (((b & 0xFF) >> (8 - i))&1) + " ";
             biteIndexString.append(current);
         }
         return biteIndexString.toString();
    }
}
