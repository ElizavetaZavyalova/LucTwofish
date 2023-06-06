package org.example.server;

import org.example.client.client.ClientOutputStream;
import org.example.massege.ActionFromClient;
import org.example.massege.Massege;
import org.example.server.information.*;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ServerLobby {
    ExecutorService executorService=null;
    Stop stopInformationServer;
    DateBase dateBase;
    ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Massege>> date;
    ServerLobby(){
        makeServer();
    }

    void uploadDate(){
        date=new ConcurrentHashMap<>();
    }
    void makeServer(){
        try {

            executorService=makeExecutorService(executorService);
            stopInformationServer=new Stop(false);

            dateBase=new DateBase();
        } catch (InterruptedException e) {
            log.debug("CANT_START_EXECUTOR"+e.getMessage());
        }
    }
    void connectToBD() throws ClassNotFoundException {
        dateBase.startBD();
    }
    ExecutorService makeExecutorService(ExecutorService executorService) throws InterruptedException {
        if(executorService!=null){
            executorService.shutdown();
        }
       return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
    }
    ServerSocket serverInformation;
    public void startInformationServer(int port) throws InterruptedException {
        executorService.submit(() -> {
            try {
                serverInformation = new ServerSocket(port);
                log.debug("-INFORMATION_SERVER_START-");
                while (!this.stopInformationServer.stop){
                    Socket socket = serverInformation.accept();
                    log.debug("-INFORMATION_CLIENT_ACCEPT-");
                    makeClient(socket);
                }
            } catch (IOException e) {
                this.stopInformationServer.stop=true;
                log.debug("-INFORMATION_SERVER_"+e.getMessage());
            }
            finally {
                executorService.shutdown();
            }
        });
    }

    ServerSocket serverDownload;
    ServerSocket serverUpload;

    public void startUploadServer(int port) throws InterruptedException {
        executorService.submit(() -> {
            try {
                serverUpload = new ServerSocket(port);
                log.debug("-UPLOAD_SERVER_START-");
                while (!this.stopInformationServer.stop){
                    Socket socket = serverUpload.accept();
                    log.debug("-UPLOAD_CLIENT_ACCEPT-");
                    makeClient(socket);
                }
            } catch (IOException e) {
                this.stopInformationServer.stop=true;
                log.debug("-UPLOAD_SERVER_"+e.getMessage());
            }
            finally {
                executorService.shutdown();
            }
        });
    }


    void setStopInformationServer(){
        stopInformationServer.stop=true;
        try {
            serverInformation.close();
        } catch (IOException e) {
          log.debug(e.getMessage());
        }
        log.debug("-INFORMATION_SERVER_WAS_STOP-");
    }
    void setStopUploadServer(){
        stopInformationServer.stop=true;
        log.debug("-UPLOAD_SERVER_WAS_STOP-");
    }
    void setStopDownloadServer(){
        stopInformationServer.stop=true;
        log.debug("-DOWNLOAD_SERVER_WAS_STOP-");
    }
    private record CommandsToServer(){
        public static final String StopInformation="STOP_INFORMATION";
        public static final String StopDownload="STOP_DOWNLOAD";
        public static final String StopALL="STOP_ALL";
        public static final String StopUpload="STOP_UPLOAD";

    }
    public void startCommandServer(){
        String commandServer="";
        Scanner scanner = new Scanner(System.in);
        while((!stopInformationServer.stop)){
            commandServer=scanner.nextLine().toUpperCase();
            switch (commandServer){
                case(CommandsToServer.StopInformation):{
                    setStopInformationServer();
                    continue;
                }
                case(CommandsToServer.StopDownload):{
                    setStopDownloadServer();
                    continue;
                }
                case(CommandsToServer.StopUpload):{
                    setStopUploadServer();
                    continue;
                }
                case(CommandsToServer.StopALL):{
                    setStopInformationServer();
                    setStopDownloadServer();
                    setStopUploadServer();
                    continue;
                }
                default:{
                    log.debug("-SERVER_LOBBY_NOT_CORRECT_COMMAND-");
                }
            }
        }

    }
    void makeClient(Socket socket) throws IOException {
        DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
        int action=dataInputStream.readInt();
        log.debug("CLIENT_WANT_"+ActionFromClient.getCommandForDebug(action));
        switch (action){
            case(ActionFromClient.Information):{
                makeInformationClient(socket,dataOutputStream,dataInputStream);
                return;
            }
            case(ActionFromClient.Upload):{
                makeUploadClient(socket,dataOutputStream,dataInputStream);
                return;
            }
            case(ActionFromClient.Download):{
                makeDownloadClient(socket,dataOutputStream,dataInputStream);
            }
        }
        log.debug("CLIENT_MAKE");

    }
    void makeInformationClient(Socket socket,DataOutputStream dataOutputStream,DataInputStream dataInputStream) throws IOException {
        int id=dataInputStream.readInt();
        log.debug("INFORMATION_READ:"+id);
        if(!date.contains(id)){
            dateBase.addNewClient(id);
            ConcurrentLinkedQueue<Massege> queue=new ConcurrentLinkedQueue<>();
            date.put(id,queue);
        }
        Stop exit=new Stop(false);
        Writer writer= Writer.builder().dataOutputStream(dataOutputStream).date(date.get(id)).id(id)
                .serverStop(this.stopInformationServer).exit(exit).build();
        Reader reader= Reader.builder().date(this.date).dateInputStream(dataInputStream).id(id)
                              .exit(exit).dateBase(dateBase).socket(socket).build();
        executorService.submit(reader);
        executorService.submit(writer);
        log.debug("INFORMATION_CONNECT:"+id);
    }
    @Deprecated
    int makeNewClient(DataOutputStream dataOutputStream,DataInputStream dataInputStream) throws IOException {
        log.debug("-NEW_CLIENT-");
        int nameLen=dataInputStream.readInt();
        byte[] nameArray=new byte[nameLen];
        dataInputStream.read(nameArray);
        String name=new String(nameArray, StandardCharsets.UTF_8);
        log.debug("-NEW_CLIENT_NAME_"+name+"-");
        int id=dateBase.addNewClient(1);
        dataOutputStream.writeInt(id);dataOutputStream.flush();
        ConcurrentLinkedQueue<Massege> queue=new ConcurrentLinkedQueue<>();
        date.put(id,queue);
        return id;
    }

    private void makeUploadClient(Socket socket,DataOutputStream dataOutputStream,DataInputStream dataInputStream) throws IOException {
        int id=dataInputStream.readInt();
        log.debug("New_Upload_Client_"+id);
        int toId=dataInputStream.readInt();
        log.debug("Upload_Clien_Read_USER_TO_ID:"+toId);
        int fileNameSize=dataInputStream.readInt();
        log.debug("Upload_Clien_Read_FILENAME_SIZE:"+fileNameSize);
        byte[] fileNameByte=new byte[fileNameSize];
        dataInputStream.readFully(fileNameByte);
        String fileName=new String(fileNameByte);
        log.debug("Upload_Clien_READ_FILE_NAME:"+fileName);
        Stop exit=new Stop(false);
        UploadFile uploadFile=UploadFile.builder().fileName(fileName).exit(exit).serverStop(this.stopInformationServer)
                .dateInputStream(dataInputStream).dateOutputStream(dataOutputStream).socket(socket).id(id).toId(toId)
                .dateBase(dateBase).date(date).build();
        executorService.submit(uploadFile);
        log.debug("USER"+id+"START_UPLOAD_FILE:"+fileName+"TO_CHANEL("+toId+"_"+id+")");

    }
    private void makeDownloadClient(Socket socket,DataOutputStream dataOutputStream,DataInputStream dataInputStream) throws IOException {
        int id=dataInputStream.readInt();
        log.debug("New_DOWNLOAD_Client_"+id);
        int fileId=dataInputStream.readInt();
        log.debug("Upload_Clien_Read_file_ID:"+fileId);
        Stop exit=new Stop(false);
        DownloadFile downloadFile=DownloadFile.builder().exit(exit).serverStop(this.stopInformationServer)
                .socket(socket).dateInputStream(dataInputStream).dateOutputStream(dataOutputStream)
                .fileid(fileId).id(id).dateBase(dateBase).build();
        executorService.submit(()->{
            downloadFile.runStop();
        });
        executorService.submit(downloadFile);
        log.debug("USER"+id+"START_DOWNLOAD_FILE_ID:"+fileId);
    }
}
