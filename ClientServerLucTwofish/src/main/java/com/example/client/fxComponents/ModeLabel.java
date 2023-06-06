package com.example.client.fxComponents;

import javafx.scene.control.Label;
import lombok.Getter;
import org.example.Algoritmes.modes.enums.Mode;


public class ModeLabel extends Label {
    @Getter
    Mode mode;
    public ModeLabel(Mode mode){
        super(mode.toString());
        this.mode=mode;
    }
}
