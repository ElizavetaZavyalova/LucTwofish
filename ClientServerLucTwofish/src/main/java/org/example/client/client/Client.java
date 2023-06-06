package org.example.client.client;

import com.example.client.SettingsApplication;
import org.example.massege.ActionFromClient;
import org.example.massege.Ports;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.server.information.Stop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
@Builder
public class Client {
    int id;
    ClientBD clientBD;
    @Getter
    ExecutorService executorService;
    SettingsApplication settingsApplication;
    @Getter
    ClientInformation clientInformation;
    Stop stop;
    @Getter
    Stop stopUpload;
    @Getter
    Stop stopDownload;
    public void createInformationSocket(){
        try {
            stop.stop=false;
            Socket socketInformation=new Socket(Ports.host, Ports.informationServerPort);
            DataOutputStream dataOutputStream=new DataOutputStream(socketInformation.getOutputStream());
            DataInputStream dataInputStream=new DataInputStream(socketInformation.getInputStream());
            dataOutputStream.writeInt(ActionFromClient.Information);dataOutputStream.flush();
            dataOutputStream.writeInt(id);dataOutputStream.flush();
            clientInformation=ClientInformation.builder().id(id).dateBase(clientBD).stop(stop).socket(socketInformation)
                    .settingsApplication(settingsApplication).dateInputStream(dataInputStream).dateOutputStream(dataOutputStream).build();
            executorService.submit(clientInformation);
        } catch (IOException e) {
            log.debug("CANT_CREATE_INFORMATION_SOCKET"+e.getMessage());
        }
    }
    public ClientOutputStream makeClientOutputStream(int toId, String fileName) throws IOException {
        try {
            stopUpload.stop=false;
            Socket socket=new Socket(Ports.host, Ports.uploadServerPort);
            DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
            dataOutputStream.writeInt(ActionFromClient.Upload);dataOutputStream.flush();
            log.debug("MAKE_CLIENT_OUTPUT_STREAM_WRITE:"+ActionFromClient.getCommandForDebug(ActionFromClient.Upload));
            dataOutputStream.writeInt(id);dataOutputStream.flush();
            log.debug("MAKE_CLIENT_OUTPUT_STREAM_WRITE_USER_ID:"+id);
            dataOutputStream.writeInt(toId);dataOutputStream.flush();
            log.debug("MAKE_CLIENT_OUTPUT_STREAM_WRITE_USER_TO_ID:"+toId);
            byte[]name=fileName.getBytes();
            dataOutputStream.writeInt(name.length);dataOutputStream.flush();
            log.debug("MAKE_CLIENT_OUTPUT_STREAM_WRITE_LEN_FILE_NAME:"+name.length);
            dataOutputStream.write(name);dataOutputStream.flush();
            log.debug("MAKE_CLIENT_OUTPUT_STREAM_WRITE_FILE_NAME:"+fileName);
            ClientOutputStream clientOutputStream=ClientOutputStream.builder()
                    .dateInputStream(dataInputStream).dateOutputStream(dataOutputStream)
                     .toId(toId).fileName(fileName).stop(stop).stopUpload(stopUpload).build();
            executorService.submit(clientOutputStream);
            return  clientOutputStream;
        } catch (IOException e) {
            throw new IOException("CANT_CREATE_INFORMATION_SOCKET"+e.getMessage());
        }
    }
    public ClientInputStream makeClientInputStream(int fileId) throws IOException {
        try {
            stopUpload.stop=false;
            Socket socket=new Socket(Ports.host, Ports.uploadServerPort);
            DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
            dataOutputStream.writeInt(ActionFromClient.Download);dataOutputStream.flush();
            log.debug("MAKE_CLIENT_OUTPUT_STREAM_WRITE:"+ActionFromClient.getCommandForDebug(ActionFromClient.Upload));
            dataOutputStream.writeInt(id);dataOutputStream.flush();
            log.debug("MAKE_CLIENT_OUTPUT_STREAM_WRITE_ID:"+id);
            dataOutputStream.writeInt(fileId);dataOutputStream.flush();
            log.debug("MAKE_CLIENT_OUTPUT_STREAM_WRITE_FILE_ID:"+fileId);
            ClientInputStream clientInputStream=ClientInputStream.builder()
                    .dateInputStream(dataInputStream).dateOutputStream(dataOutputStream)
                    .stop(stop).upload(stopUpload).socket(socket).count(1).build();
            return  clientInputStream;
        } catch (IOException e) {
            throw new IOException("CANT_CREATE_INFORMATION_SOCKET"+e.getMessage());
        }
    }


    // Platform.runLater(() -> partialResults.get().add(r));

}
