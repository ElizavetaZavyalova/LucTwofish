package org.example.massege;

public record ActionFromClient(){
    public static final int Information=0;
    public static final int Upload=1;
    public static final int Download=2;
    public static String getCommandForDebug(int key){
        switch (key) {
            case (ActionFromClient.Download) -> {
                return "ActionFromClient_Download(" + (key) + ")";
            }
            case (ActionFromClient.Upload) -> {
                return "ActionFromClient_Upload(" + (key) + ")";
            }
            case (ActionFromClient.Information) -> {
                return "ActionFromClient_Information(" + (key) + ")";
            }
            default -> {
                return "NOT_CORRECT_COMMAND_ActionFromClient(" + key + ")";
            }
        }
    }

}
