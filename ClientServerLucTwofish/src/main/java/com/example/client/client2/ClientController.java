package com.example.client.client2;

import com.example.client.SettingsApplication;
import com.example.client.fxComponents.FileContentVbox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.Algoritmes.LUC.SimplicityMode;
import org.example.Algoritmes.modes.enums.Mode;

@Slf4j
public class ClientController {
    @Setter
    static int id;
    SettingsApplication settingsApplication;
    @FXML
    private ScrollPane channelsScrollPane;

    @FXML
    private Button createButton;

    @FXML
    private ScrollPane filesScrollPane;

    @FXML
    private TextField idField;

    @FXML
    private Label keyLabel;

    @FXML
    private Slider lengthNumId;

    @FXML
    private Button loadButton;

    @FXML
    private ProgressBar loadProgressBar;

    @FXML
    private ChoiceBox<Mode> modId;

    @FXML
    private Slider probabylityId;

    @FXML
    private ChoiceBox<SimplicityMode> testId;
    @FXML
    void initialize() {
        log.debug("PROJECT_STURT");
        this.makeAllAsset();
        initializeClass();
        createButton.setOnAction(event -> {
            settingsApplication.createChanel(Integer.parseInt(idField.getText()),idField.getText());
        });
       // this.createChoseBox();
    }
    void initializeClass(){
        VBox chanelContent=new VBox();
        FileContentVbox fileContent=FileContentVbox.builder().chanelId(-1).keyLabel(keyLabel).build();
        this.channelsScrollPane.setContent(chanelContent);
        this.filesScrollPane.setContent(fileContent);
        try {
            String url="jdbc:sqlite:src\\main\\resources\\com\\example\\client\\sql\\client"+id+".sqlite";
            /*this.settingsApplication=SettingsApplication.builder().id(id).stage(HelloApplication.stage).loadVbox(loadVbox)
                    .chanelContent(chanelContent).fileContent(fileContent).loadButton(loadButton).build();*/
            this.settingsApplication=new SettingsApplication(id, HelloApplication.stage,chanelContent,loadProgressBar, fileContent,loadButton,url);
            log.debug("ALL_CLASS_ARE_INICIALIZED");
        }catch (ClassNotFoundException e) {
            log.debug(e.getMessage());
        }
    }
    void createChoseBox(){
        ObservableList<Mode> modes = FXCollections.observableArrayList(
                Mode.ECB/**/, Mode.CBC/**/, Mode.CFB/**/, Mode.OFB/**/, Mode.CTR, Mode.RD, Mode.RDH);
        modId.setItems(modes);
        modId.setOnAction(event ->settingsApplication.getTwofishSettings().setMode(modId.getValue()));
                ObservableList<SimplicityMode> tests = FXCollections.observableArrayList(SimplicityMode.farm,SimplicityMode.millerRabin,SimplicityMode.solovayStrassen);
        testId.setItems(tests);
        testId.setOnAction(event ->settingsApplication.getLuc().setTestMode(testId.getValue()));
    }

    void makeAllAsset() {

    }

}
