package org.example.server;

import org.example.massege.Ports;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {

        ServerLobby serverLobby=new ServerLobby();
        try {
            serverLobby.uploadDate();
            serverLobby.connectToBD();
            serverLobby.startInformationServer(Ports.informationServerPort);
            serverLobby.startUploadServer(Ports.uploadServerPort);
            serverLobby.startCommandServer();
        } catch (InterruptedException e) {
            log.debug("CANT_START_SERVER");
        } catch (ClassNotFoundException e) {
            log.debug("CANT_CONNECT_TO_BD"+e.getMessage());
        }
        //serverLobby.startUploadServer(uploadPort);
        //serverLobby.startDownloadServer(downloadPort);

    }
}