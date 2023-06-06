package org.example.server.information;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.sql.*;
@Slf4j
public class DateBase {
    String url="jdbc:mysql://localhost:3306/FILE_BD";
    String userName="root";
    String password="Qasxcdew19";
    Connection connection;
    public int addNewClient(int name){
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("insert into users (USER_ID,USER_NAME) values (?,?)");
            preparedStatement.setInt(1,name);
            preparedStatement.setString(2,String.valueOf(name));
            preparedStatement.execute();
            ResultSet resultSet=connection.createStatement().executeQuery("SELECT USER_ID FROM USERS;");
            resultSet.next();
            int id=resultSet.getInt("USER_ID");
            log.debug("BD_CREATE_USER:"+userName+"ID:"+id);
            return  id;
        } catch (SQLException e) {
            log.debug("-SQL_EXCEPTION_"+e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    private int getFileId(int chanelId,String fileName) throws SQLException {
        ResultSet resultSet=connection.createStatement()
                .executeQuery("SELECT MAX(FILE_ID) AS FILE_ID FROM FILES WHERE (CHANEL_ID="+chanelId+" and FILE_NAME=\'"+fileName+"\');");
        resultSet.next();
        return resultSet.getInt("FILE_ID");
        //SELECT MAX(id) FROM mytable
    }
    public void startBD() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try{
            this.connection=DriverManager.getConnection(url,userName,password);
            log.debug("-SQL_CONNECT | USER_NAME:"+userName+" | URL:"+url+"| PASSWORD:"+password+" |-");
        } catch (SQLException e) {
            log.debug("-SQL_EXCEPTION_"+e.getMessage());
            e.printStackTrace();
        }
    }
    int getChanel(int id1, int id2) throws SQLException {
        ResultSet resultSet=connection.createStatement().executeQuery("SELECT CHANEL_ID FROM CHANELS WHERE ((USER1_ID="+id1+")and(USER2_ID="+id2+"))OR  ((USER1_ID="+id2+")and(USER2_ID="+id1+"))");
        resultSet.next();
        return resultSet.getInt("CHANEL_ID");
    }
    public  synchronized void createNewChanel(int id1, int id2){
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("insert into chanels (USER1_ID, USER2_ID) values (?,?)");
            preparedStatement.setInt(1,id1);
            preparedStatement.setInt(2,id2);
            preparedStatement.execute();
            log.debug("BD_CREATE_CHANEL:"+id1+"_"+id2);
        } catch (SQLException e) {
            log.debug("-SQL_EXCEPTION_"+e.getMessage());
            e.printStackTrace();
        }
    }
    synchronized int setFile(Blob blob, String filename, int toId, int fromId){
        int fileId=-1;
        try {
            int chanelId=getChanel(toId, fromId);
            log.debug("ADD_BLOB_TO_BD_FILE_NAME:"+filename+"_CHANEL_ID:"+chanelId);
            PreparedStatement preparedStatement=connection.prepareStatement("insert into files (FILE_NAME, CHANEL_ID,INFORMATION) values (?,?,?)");
            preparedStatement.setString(1,filename);
            preparedStatement.setInt(2,chanelId);
            preparedStatement.setBlob(3,blob);
            preparedStatement.execute();
            fileId=getFileId(chanelId,filename);

        } catch (SQLException e) {
            log.debug("-SQL_EXCEPTION_"+e.getMessage());
            e.printStackTrace();
        }
        return  fileId;
    }
    public Blob getFile(int fileId) throws SQLException {
        log.debug("GET_BLOB_FROM_BD_FILE_NAME:"+fileId);
        ResultSet resultSet=connection.createStatement().executeQuery("SELECT INFORMATION FROM FILES WHERE FILE_ID="+fileId+";");
        resultSet.next();
        log.debug("GET_BLOB_FROM_BD_FILE_NAME:"+fileId);
        return resultSet.getBlob("INFORMATION");
    }
    Blob makeBlob() throws SQLException {
        return connection.createBlob();
    }
}
