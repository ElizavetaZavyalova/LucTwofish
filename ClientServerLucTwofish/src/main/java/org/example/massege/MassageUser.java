package org.example.massege;

import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

@Setter
@Getter
public class MassageUser extends Massege {
    int action;
    int id;
    byte[] userName;
    public String getNameToDebug(){
        return new String(userName, StandardCharsets.UTF_8);
    }
}
