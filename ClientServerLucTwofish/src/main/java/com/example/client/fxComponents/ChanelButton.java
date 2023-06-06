package com.example.client.fxComponents;

import com.example.client.SettingsApplication;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.example.client.client.ClientBD;

import java.sql.ResultSet;
import java.sql.SQLException;
@Slf4j
public class ChanelButton extends Button {
    int chanelId;
    ClientBD clientBD;
    private  FileContentVbox content;
    SettingsApplication settingsApplication;
    public ChanelButton(int id, String name, ClientBD clientBD, FileContentVbox content,SettingsApplication settingsApplication){
        this.chanelId=id;
        this.clientBD=clientBD;
        this.content=content;
        this.setText(name);
        this.settingsApplication=settingsApplication;
        setClick();
    }
    void setClick(){
        this.setOnAction(event -> {
            try {
                content.getChildren().removeAll(content.getChildren());
                addItemsOnScrollPane(clientBD.selectFilesOfChanelId(chanelId));
                content.setChanelId(chanelId,clientBD);
            }catch (SQLException e) {
                log.debug("SQLEXEPTION_CHANEL_ID:"+chanelId+"_"+e.getMessage());
            }
        });
    }
    void addItemsOnScrollPane(ResultSet files) throws SQLException {
         while(files.next()){
             String fileName=files.getString("file_Name");
             int fileId=files.getInt("file_id");
             FileVbox fileVbox=new FileVbox(fileName,fileId,chanelId);
             Hyperlink hyperlink=fileVbox.hyperlink;
             setClick(hyperlink,fileVbox.getFileId(),fileVbox.getName());
             content.getChildren().addAll(fileVbox);
         }
    }
    void setClick(Hyperlink hyperlink, int id, String name){
        hyperlink.setOnAction(e->{
            settingsApplication.download(id,name);
        });
    }

}
