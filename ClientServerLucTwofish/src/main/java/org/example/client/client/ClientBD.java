package org.example.client.client;
import org.example.Algoritmes.LUC.LUC;
import org.example.Algoritmes.LUC.LUCKey;
import org.example.Algoritmes.debug.DebugFunctions;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.sql.*;


@Slf4j
@NoArgsConstructor
public class ClientBD {
    Connection connection;
    public void startBD(String url) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try{
            this.connection=DriverManager.getConnection(url);
            log.debug("-SQL_LITE_CONNECT| URL:"+url+"|-");
        } catch (SQLException e) {
                log.debug("-SQL_EXCEPTION_"+e.getMessage());
                e.printStackTrace();
        }
    }

    @Getter
   @Builder
   static class ChanelKey{
        int chanelId;
        String name;
        byte[] key;
    }
    private ChanelKey makeChanelKey(int chanelId, byte[] key) throws SQLException {
        ResultSet resultSet=connection.createStatement().executeQuery("SELECT* FROM privateKeys where chanel_id="+chanelId+";");
        resultSet.next();
        BigInteger[] d=new BigInteger[4];
        d[0]=new BigInteger(resultSet.getString("d0"));
        d[1]=new BigInteger(resultSet.getString("d1"));
        d[2]=new BigInteger(resultSet.getString("d2"));
        d[3]=new BigInteger(resultSet.getString("d3"));
        BigInteger p=new BigInteger(resultSet.getString("p"));
        BigInteger q=new BigInteger(resultSet.getString("q"));
        LUCKey.D privateKey=new LUCKey.D(p,q,d);
        log.debug(privateKey.toString());
        key= LUC.decrypt(key,privateKey);
        String name=resultSet.getString("user_name");
        deletePrivateKey(chanelId);
        return ChanelKey.builder().key(key).name(name).chanelId(chanelId).build();
    }
    public void deletePrivateKey(int chanelId) throws SQLException {
        PreparedStatement st = connection.prepareStatement("DELETE FROM privateKeys WHERE chanel_id = ?");
        st.setInt(1,chanelId);
        st.executeUpdate();
    }
    public void setAndDecryptChanel(int chanelId, byte[] decryptKey){
        try {
            ChanelKey chanelKey=makeChanelKey(chanelId,decryptKey);
            setChanel(chanelId,chanelKey.getName(),chanelKey.getKey());
            log.debug("ADD_NEW_CHANEL_BD_CHANEL_ID:"+chanelId+"_CHANEL_NAME:"+chanelKey.getName()+
                    "DECRYPT_KEY:"+ DebugFunctions.byteArrayToHexString(chanelKey.getKey()));
        } catch (SQLException e) {
            log.debug("-SQL_EXCEPTION_SET_CHANEL"+e.getMessage());
            e.printStackTrace();
        }
    }
    private Blob makeBlobFromByteArray(byte[] array) throws SQLException {
        log.debug("MAKE_BLOB:");
        Blob blob=connection.createBlob();
        blob.setBytes(1,array);
        log.debug("REV_BLOB:");
        return blob;
    }
    public void setChanel(int chanelId,String chanelName, byte[] chanelKey){
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("insert into chanels (chanel_id, user_name,password) values (?,?,?)");
            preparedStatement.setInt(1,chanelId);
            preparedStatement.setString(2, chanelName);
            preparedStatement.setBytes(3,chanelKey);
           // preparedStatement.setBlob(3,makeBlobFromByteArray(chanelKey));//
            preparedStatement.execute();
            log.debug("ADD_NEW_CHANEL_BD_CHANEL_ID:"+chanelId+"_CHANEL_NAME:"+chanelName+
                    "DECRYPT_KEY:"+ DebugFunctions.byteArrayToHexString(chanelKey));
        } catch (SQLException e) {
            log.debug("-SQL_EXCEPTION_SET_CHANEL"+e.getMessage());
            e.printStackTrace();
        }
    }
    public byte[] getChanelKey(int chanelId) throws SQLException {
        ResultSet resultSet=connection.createStatement().executeQuery("SELECT password FROM chanels where chanel_id="+chanelId+";");
        resultSet.next();
        return resultSet.getBytes("password");
    }
    public  ResultSet selectFilesOfChanelId(int chanelId) throws SQLException {
        return connection.createStatement().executeQuery("SELECT* FROM files where chanel_id="+chanelId+";");
    }

    public void setFile(int chanelId, String fileName,int fileId){
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("insert into files ( file_id,file_name, chanel_id ) values (?,?,?)");
            preparedStatement.setInt(1,fileId);
            preparedStatement.setString(2, fileName);
            preparedStatement.setInt(3,chanelId);
            preparedStatement.execute();
            log.debug("ADD_NEW_CHANEL_BD_FILE_CHANEL_ID:"+chanelId+"_FILE_NAME:"+fileName+
                    "_FILE_ID:"+ fileId);
        } catch (SQLException e) {
            log.debug("-SQL_EXCEPTION_SET_FILE"+e.getMessage());
            e.printStackTrace();
        }
    }
    public int getChanel(int fileId) throws SQLException {
        ResultSet resultSet=connection.createStatement().executeQuery("SELECT chanel_id FROM files where file_id="+fileId+";");
        resultSet.next();
        return resultSet.getInt("chanel_id");
    }
    public ResultSet getAllChanel() throws SQLException {
        return connection.createStatement().executeQuery("SELECT chanel_id,user_name FROM chanels "+";");
    }

    public void setPrivateKey(LUCKey.D privateKey, int chanelId, String name){
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("insert into privateKeys (chanel_id,user_name,d0,d1,d2,d3,p,q) values (?,?,?,?,?,?,?,?)");
            preparedStatement.setInt(1,chanelId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3,(privateKey.getD()[0]).toString());//d0
            preparedStatement.setString(4,(privateKey.getD()[1]).toString());//d1
            preparedStatement.setString(5,(privateKey.getD()[2]).toString());//d2
            preparedStatement.setString(6,(privateKey.getD()[3]).toString());///d3
            preparedStatement.setString(7,(privateKey.getP()).toString());//p
            preparedStatement.setString(8,(privateKey.getQ()).toString());//q
            preparedStatement.execute();
            log.debug("ADD_NEW_PRIVATE_KEY_BD_CHANEL_ID:"+chanelId+"_CHANEL_NAME:"+name+
                    "PRIVATE_KEY:"+privateKey.toString());
        } catch (SQLException e) {
            log.debug("-SQL_EXCEPTION_SET_PRIVATE_KEY"+e.getMessage());
        }
    }

}
