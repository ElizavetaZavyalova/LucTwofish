package com.example.client.fxComponents;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.Algoritmes.Twofish.RoundKeysForTwofish;
import org.example.Algoritmes.Twofish.Twofish;
import org.example.Algoritmes.debug.DebugFunctions;
import org.example.client.client.ClientBD;

import java.sql.SQLException;

@Builder
@Slf4j
public class FileContentVbox extends VBox {
    @Getter
    int chanelId;
    @Getter
    private Twofish twofish;
    Label keyLabel;
    void setChanelId(int id, ClientBD clientBD){
        this.chanelId=id;
        twofish=new Twofish(new RoundKeysForTwofish());
        if(chanelId!=-1){
            try {
                byte[] password=clientBD.getChanelKey(chanelId);
                twofish.makeRoundKeys(password);
                keyLabel.setText("CHANEL"+chanelId+"KEY:"+DebugFunctions.byteArrayToHexString(password));
            } catch (SQLException e) {
                log.debug("SQL_EXEPTION_setChanelId Can't set key"+e.getMessage());
            }

        }
    }
}
