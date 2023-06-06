package org.example.Algoritmes.Twofish.help;

public record RO(){
    public static long rOR(long b, int count, int rO){
        long and=((1L<<rO)-1);
        return (((b&and) >> (count)) | ((b&and) << (rO - (count))))&and;
    }
    public static long rOL(long b, int count, int rO){
        long and=((1L<<rO)-1);
        return (((b&and) << (count)) | ((b&and) >> (rO - (count))))&and;
    }
}