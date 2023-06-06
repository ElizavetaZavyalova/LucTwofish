package com.example.client.client1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;


import java.io.IOException;

public class HelloApplication extends Application {
    @Getter
    static Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        HelloApplication.stage=stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ClientController.class.getResource("connect.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Клиент");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}