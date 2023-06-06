package org.example.massege;

public class CommandsSocket {
    public static final int NEW_CLIENT=-1;
    public static String getCommandForDebug(int key){
        switch (key) {
            case ( CommandsSocket.NEW_CLIENT) -> {
                return "NEW_CLIENT(" + (key) + ")";
            }
            default -> {
                return "USER_ID:(" + key + ")";
            }
        }
    }
}
