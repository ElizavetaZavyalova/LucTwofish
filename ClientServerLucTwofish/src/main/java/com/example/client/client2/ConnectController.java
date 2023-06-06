package com.example.client.client2;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ConnectController {
    @FXML
    private Button connectButton;
    @FXML
    private Label keyLabel;

    @FXML
    private Button revButton;

    @FXML
    private TextField userId;

    @FXML
    void initialize() {
        revButton.setOnAction(event -> {
            Platform.exit();
            log.debug("EXIT");
        });
        connectButton.setOnAction(event -> {
            try {
                ClientController.setId(Integer.parseInt(userId.getText()));
                FXMLLoader fxmlLoader = new FXMLLoader(ClientController.class.getResource("client.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 320, 240);
                HelloApplication.getStage().setTitle("Клиент"+userId.getText());
                HelloApplication.getStage().setScene(scene);
                HelloApplication.getStage().setOnCloseRequest(new EventHandler<>() {
                    @Override
                    public void handle(WindowEvent event) {
                        //TODO close
                        log.debug("EXIT");
                    }
                });
                HelloApplication.getStage().show();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }

        });
    }

}
