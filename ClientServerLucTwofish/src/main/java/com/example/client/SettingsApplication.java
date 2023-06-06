package com.example.client;

import com.example.client.fxComponents.ChanelButton;
import com.example.client.fxComponents.FileContentVbox;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.Algoritmes.Twofish.Twofish;
import org.example.TwofishSettings;
import org.example.Algoritmes.LUC.LUC;
import org.example.Algoritmes.LUC.LUCKey;
import org.example.Algoritmes.LUC.SimplicityMode;
import org.example.Algoritmes.modes.BaseModeEncryption;
import org.example.Algoritmes.modes.enums.Mode;
import org.example.Algoritmes.modes.enums.PaddingType;
import org.example.client.client.Client;
import org.example.client.client.ClientBD;
import org.example.client.client.ClientInputStream;
import org.example.client.client.ClientOutputStream;
import org.example.server.information.Stop;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;

import static com.example.client.SettingsApplication.ButtonLoad.STOP_DOWNLOAD;
import static com.example.client.SettingsApplication.ButtonLoad.STOP_UPLOAD;

@Getter
@Setter
@Slf4j
public class SettingsApplication {
    int id;
    private TwofishSettings twofishSettings;
    private LUC luc;
    private Client client;
    FileContentVbox fileContent;
    VBox chanelContent;
    Button loadButton;
    private ClientBD clientBD;
    Stage stage;
    ProgressBar loadProgressBar;
    public SettingsApplication(int id, Stage stage, VBox chanelContent, ProgressBar loadProgressBar, FileContentVbox fileContent, Button loadButton, String url) throws ClassNotFoundException{
        this.id=id;
        this.stage=stage;
        this.loadProgressBar=loadProgressBar;
        this.chanelContent=chanelContent;
        this.fileContent=fileContent;
        this.loadButton=loadButton;
        clientBD=new ClientBD();
        clientBD.startBD(url);
        TwofishSettings.loadProgressBar=loadProgressBar;
        this.luc=new LUC(SimplicityMode.solovayStrassen,0.99F,64);
        this.twofishSettings=new TwofishSettings(Mode.ECB, PaddingType.PCS7, fileContent.getTwofish());
        makeChanelContent();
        createClients();
        setClickLoadButton();
    }
    ClientOutputStream clientOutputStream;
    ClientInputStream clientInputStream;
    void setClickLoadButton(){
            loadButton.setOnAction(event -> {
                try {
                    loadButtonClick();
                } catch (IOException e) {
                    log.debug("LOAD_BUTTON_EXEPTION"+e.getMessage());
                }
            });
    }
    public  enum ButtonLoad{
        STOP_UPLOAD,STOP_DOWNLOAD,UPLOAD
    }
    ButtonLoad buttonLoad=ButtonLoad.UPLOAD;
    void loadButtonClick() throws IOException {
        switch (buttonLoad){
            case UPLOAD: {
                 upload();
                return;
            }
            case STOP_DOWNLOAD:{
                stopDownload();
                return;
            }
            case STOP_UPLOAD:{
                stopUpload();
                return;
            }
        }
    }
    void endLoad(){
        buttonLoad=ButtonLoad.UPLOAD;
        loadButton.setText("Загрузить");
        loadProgressBar.setProgress(0);
        client.getStopUpload().stop=true;
    }
    void stopUpload() throws IOException {
        clientOutputStream.setStop();
        endLoad();
    }
    void stopDownload() throws IOException {
        clientInputStream.setStop();
        endLoad();
    }
    void upload(){
        if(client.getStopUpload().stop) {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                if (fileContent.getTwofish() != null) {
                    loadFileToServer(selectedFile);
                }
            }
        }
    }
    public void download(int fromId,String fileName){
        if(client.getStopUpload().stop) {
            downloadFromServer(fromId,fileName);
        }
    }
    void downloadFromServer(int fromId, String fileName){
        buttonLoad=STOP_DOWNLOAD;
        loadButton.setText("Cтоп");
        client.getExecutorService().submit(()->{
            try {
                clientInputStream = client.makeClientInputStream(fromId);
                log.debug("TWOFISH_SETTINGS_MADE");
                Twofish twofish=fileContent.getTwofish();
                twofishSettings.setEncryption(fileContent.getTwofish());//Twofish
               String loadPath="C:\\Users\\eliza\\Downloads\\"+fileName;
                BaseModeEncryption.decrypt(clientInputStream,loadPath,twofish);
                Platform.runLater(()->endLoad());
            } catch (IOException e) {
                log.debug("START_LOAD_EXEPTION:"+e.getMessage());
            }
        });

    }
   void loadFileToServer(File selectedFile){
       buttonLoad=STOP_UPLOAD;
       loadButton.setText("Cтоп");
        client.getExecutorService().submit(()->{
            try {
                clientOutputStream = client.makeClientOutputStream(fileContent.getChanelId(), selectedFile.getName());
                log.debug("TWOFISH_SETTINGS_MADE");
                twofishSettings.setEncryption(fileContent.getTwofish());//Twofish
                TwofishSettings currentSettings=new TwofishSettings(twofishSettings);
                //currentSettings.setLoadProgressVbox(loadProgressVbox);
               // loadProgressVbox.setClickButtonStop(clientOutputStream);//Cтоп
                BaseModeEncryption.encrypt(selectedFile,clientOutputStream,currentSettings);
                Platform.runLater(()->endLoad());
            } catch (IOException e) {
                log.debug("START_LOAD_EXEPTION:"+e.getMessage());
            }
        });
    }

    void createClients(){
        client=Client.builder().id(id).clientBD(clientBD).settingsApplication(this)
                .stop(new Stop(true)).stopUpload(new Stop(true)).stopDownload(new Stop(true))
                .executorService(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)).build();
        client.createInformationSocket();
    }
    void makeChanelContent(){
        try {
            ResultSet chanel=this.clientBD.getAllChanel();
            while (chanel.next()){
                String name=chanel.getString("user_name");
                int id=chanel.getInt("chanel_id");
                addChanel(id,name);
            }
        } catch (SQLException e) {
            log.debug("SQL_EXCEPTION_MAKE_CHANEL_"+e.getMessage());
        }
    }
    public void addChanel(int id, String name){
        ChanelButton chanelButton=new ChanelButton(id,name,clientBD,fileContent,this);
        this.chanelContent.getChildren().addAll(chanelButton);
    }

   public void createChanel(int id, String name){
        client.getExecutorService().submit(()-> {
            LUCKey lucKey=luc.generateKey();
            try {
                client.getClientInformation().sendPublicKey(id,name,lucKey);
            } catch (IOException e) {
                log.debug("CANT_SEND_PUBLIC_KEY"+e.getMessage());
            }
        });

    }
   }
