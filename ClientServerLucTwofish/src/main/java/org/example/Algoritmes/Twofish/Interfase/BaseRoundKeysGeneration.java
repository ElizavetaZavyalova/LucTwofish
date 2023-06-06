package org.example.Algoritmes.Twofish.Interfase;

import java.util.ArrayList;

public interface BaseRoundKeysGeneration<T>{
    ArrayList<T> generateRoundKeys(byte[] roundKey);
}
