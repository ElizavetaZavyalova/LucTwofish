package org.example.massege;


public record Commands() {

    public static final int GET_FILE=0;//

   public static final int UPLOAD_FILE=2;

    public static final int DOWNLOAD_FILE=3;//

    public static final int PUBLIC_KEY=4;//

    public static final int CHANEL_KEY=5;//
    public static final int ACCOUNT=6;//

    public static final int STOP=7;

    public static final int NEW_FILE=8;//
    public static final int BLOCK=9;

    public static String getNameForDebug(int key){
        switch (key) {
            case (Commands.UPLOAD_FILE) -> {
                return "UPLOAD_FILE(" + key + ")";
            }
            case (Commands.PUBLIC_KEY) -> {
                return "PUBLIC_KEY(" + key + ")";
            }
            case (Commands.DOWNLOAD_FILE) -> {
                return "DELETE_CHANEL(" + key + ")";

            }
            case (Commands.BLOCK) -> {
                return "BLOCK(" + key + ")";

            }
            case (Commands.ACCOUNT) -> {
                return "ACCOUNT(" + key + ")";
            }
            case (Commands.CHANEL_KEY) -> {
                return "CHANEL_KEY(" + key + ")";

            }
            case (Commands.GET_FILE) -> {
                return "GET_FILE(" + key + ")";
            }
            case (Commands.STOP) -> {
                return "STOP(" + key + ")";

            }
            case (Commands.NEW_FILE) -> {
                return "ASK_FILES(" + key + ")";

            }
            default -> {
                return "NOT_CORRECT_COMMAND(" + key + ")";
            }
        }
    }
}
