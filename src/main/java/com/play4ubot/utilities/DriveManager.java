package com.play4ubot.utilities;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class DriveManager {
    private String name;
    private Path path = Paths.get(System.getProperty("user.dir") + "/src/main/java/com/play4ubot/utilities");
    private JsonFactory json;
    private String tokens;
    private List<String> scopes;
    private String credentials;
    private Drive driver;
    private FileList dir;

    public DriveManager() {
        this.name = "play4ubot";
        this.tokens = "credentials/token";
        this.json = JacksonFactory.getDefaultInstance();
        this.credentials = System.getProperty("user.dir") + "/src/main/java/com/play4ubot/utilities/credentials/client_secret.json";
        this.scopes = Collections.singletonList(DriveScopes.DRIVE);
        this.setDriver();
        this.setDir();
    }

    public Credential connect(NetHttpTransport http) throws MalformedURLException {
        try {
            InputStream file = Paths.get(this.getCredentials()).toUri().toURL().openStream();
            if (file != null) {
                GoogleClientSecrets secret = GoogleClientSecrets.load(this.getJson(), new InputStreamReader(file));
                GoogleAuthorizationCodeFlow auth = new GoogleAuthorizationCodeFlow.Builder(http, this.getJson(), secret, scopes)
                        .setDataStoreFactory(new FileDataStoreFactory(new File(path + "/" + (this.getTokens()))))
                        .setAccessType("offline")
                        .build();
                LocalServerReceiver server = new LocalServerReceiver.Builder().setPort(8888).build();
                file.close();
                return new AuthorizationCodeInstalledApp(auth, server).authorize("user");
            }
        } catch (Exception e) {
            System.out.println("Arquivo inexistente");
            return null;
        }
        return null;
    }
    public void uploadFile(String name, String ext) throws IOException{
        java.io.File file = new File(System.getProperty("user.dir") + "/audiofiles/" + name);
        com.google.api.services.drive.model.File metaData = new com.google.api.services.drive.model.File();
        metaData.setParents(Collections.singletonList(this.getDir().getFiles().get(0).getId()));
        metaData.setName(file.getName());
        metaData.setMimeType("audio/" + ext);
        FileContent content = new FileContent(metaData.getMimeType(), file);
        com.google.api.services.drive.model.File fileUp = this.getDriver().files().create(metaData, content).execute();
    }
    public Path downloadFile(String name, String id) throws IOException{
        OutputStream downloader = new FileOutputStream(System.getProperty("user.dir") + "/audiofiles/" + name);
        this.getDriver().files().get(id).executeMediaAndDownloadTo(downloader);
        downloader.flush();
        downloader.close();
        return Paths.get(System.getProperty("user.dir")  + "/audiofiles/" + name);
    }
    public boolean deleteFile(String id){
        try {
            this.getDriver().files().delete(id).execute();
            return true;
        }catch (IOException e){
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonFactory getJson() {
        return json;
    }

    public void setJson(JsonFactory json) {
        this.json = json;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public Drive getDriver() {
        return driver;
    }

    public void setDriver() {
        try {
            NetHttpTransport http = GoogleNetHttpTransport.newTrustedTransport();
            this.driver = new Drive.Builder(http, this.getJson(), this.connect(http)).setApplicationName(this.getName()).build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    public FileList getDir() {
        return dir;
    }

    public void setDir() {
        try {
            this.dir = this.getDriver().files().list().setQ("name='audiofiles'").execute();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}