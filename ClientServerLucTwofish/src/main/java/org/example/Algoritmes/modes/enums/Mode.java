package org.example.Algoritmes.modes.enums;

public enum Mode {
    ECB/**/, CBC/**/, CFB/**/, OFB/**/, CTR, RD, RDH;
    public static int getInt(Mode mode){
        switch (mode){
            case RD -> {
                return 1;
            }
            case RDH -> {
                return 2;
            }
            case CTR -> {
                return 3;
            }
            case OFB -> {
                return 4;
            }
            case CFB->{
                return 5;
            }
            case CBC ->{
                return 6;
            }
            default -> {
                return 7;
            }
        }
    }
    public static Mode getMode(int num){
        switch (num){
            case 1 -> {
                return RD;
            }
            case 2 -> {
                return RDH;
            }
            case 3 -> {
                return CTR;
            }
            case 4 -> {
                return OFB;
            }
            case 5->{
                return CFB;
            }
            case 6 ->{
                return CBC;
            }
            default -> {
                return ECB;
            }

        }
    }
}
