package org.example.massege;

public class CommandsLoad {
    public static final int LAST=-1;
    public static final int STOP=-2;
    public static String getCommandForDebug(int key){
        switch (key) {
            case ( CommandsLoad.LAST) -> {
                return "LAST(" + (key) + ")";
            }
            case (CommandsLoad.STOP) -> {
                return "STOP(" + (key) + ")";
            }
            default -> {
                return "NOT_CORRECT_COMMAND(" + key + ")";
            }
        }
    }
    public static boolean hasNext(int key){
        return (key!=STOP)&&(key!=LAST);
    }
}
