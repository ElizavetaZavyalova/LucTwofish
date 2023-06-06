package org.example.massege;

public record AddRemoveCommands() {
    public static final int ADD=0;
    private static final int REMOVE=1;
    public static String getCommandForDebug(int key){
        switch (key) {
            case (AddRemoveCommands.ADD) -> {
                return "ADD(" + (key) + ")";
            }
            case (AddRemoveCommands.REMOVE) -> {
                return "REMOVE(" + (key) + ")";
            }
            default -> {
                return "NOT_CORRECT_COMMAND(" + key + ")";
            }
        }
    }
}
