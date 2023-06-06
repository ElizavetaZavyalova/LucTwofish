package com.example.client.fxComponents;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class FileVbox extends VBox {
    @Getter
    Hyperlink hyperlink;
    @Getter
    int fileId;

    @Getter
    String name;
    int chanelId;

    FileVbox(String name, int fileId, int chanelId){
        this.fileId=fileId;
        this.chanelId=chanelId;
        hyperlink=new Hyperlink(name);
        this.name=name;
        this.getChildren().addAll(hyperlink);
    }

}
