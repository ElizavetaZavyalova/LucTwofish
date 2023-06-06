package org.example.Algoritmes.modes.enums;

public enum PaddingType {
    ANSI_X923,ISO_10126,PCS7;
    public static int getInt(PaddingType type){
        switch (type){
            case ISO_10126 -> {
                return 1;
            }
            case ANSI_X923 -> {
                return 2;
            }
            default->{
                return 3;
            }
        }
    }
    public static PaddingType getPadding(int num){
        switch (num){
            case 1 -> {
                return ISO_10126;
            }
            case 2 -> {
                return ANSI_X923;
            }
            default -> {
                return PCS7;
            }

        }
    }
}
