package com.play4ubot.utilities;

import com.play4ubot.audiopackage.MainPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {
    private Set<String> musics = new HashSet<>();
    private File dir;
    private final DriveManager cloud;
    private String[] symbols;
    public FileManager(){
        this.cloud = new DriveManager();
        this.dir = new File(System.getProperty("user.dir") + "/audiofiles");
        try {
            for (com.google.api.services.drive.model.File f : cloud.getDriver().files().list().setQ("not name contains 'audiofiles'").setFields("nextPageToken, files(name)").execute().getFiles()) {
                this.musics.add(f.getName());
            }
            this.musics.remove("ignore");
        }catch (NullPointerException e){
            System.out.println("Não há arquivos");
        } catch (IOException e){
            System.out.println("Acesso ao drive corrompido");
        }
        this.symbols = new String[]{"!", "(", ")", "?", "<", ">", "´", "'", "\"",
        "&", "_", "/", "[", "]"};
    }
    public boolean verifyExtension(String ext){
        String[] valid = {"MP3", "M4A", "OPUS", "MP4", "AA3", "FLAC", "WAV"};
        for (String e:valid){
            if (e.equals(ext)){
                return true;
            }
        }
        return false;
    }
    public String removeSymbols(String name){
        for (String s: this.getSymbols()){
            if (!s.equals("_") && !s.equals("'") & !s.equals("/") & !s.equals("!")) {
                name = name.replace(s, "");
            }else{
                name = name.replace(s, " ");
            }
        }
        return name;
    }
    public String removeSymbols(String name, String ext){
        for (String s: this.getSymbols()){
            if (!s.equals("_") && !s.equals("/") && !s.equals("'") & !s.equals("!")) {
                name = name.replace(s, "");
            }else{
                name = name.replace(s, " ");
            }
        }
        String replicant = name.replace("." + ext, "");
        replicant = replicant.trim();
        replicant = replicant.replace(ext.toUpperCase(), "");
        replicant = replicant.replaceAll("[1-9][0-9][0-9][K-k]", "").trim();
        replicant = replicant.replaceAll("HQ", "");
        replicant = replicant.replaceAll("HD", "");
        replicant = replicant.replaceAll("Hd", "");
        replicant = replicant.replaceAll("Hq", "");
        replicant = replicant.replaceAll("LEGENDADO", "");
        replicant = replicant.replaceAll("Legendado", "");
        replicant = replicant.replaceAll("legendado", "");
        replicant = replicant.replaceAll("LYRICS", "");
        replicant = replicant.replaceAll("Lyrics", "");
        replicant = replicant.replaceAll("lyrics", "");
        replicant = replicant.replaceAll("OFFICIAL", "");
        replicant = replicant.replaceAll("Official", "");
        replicant = replicant.replaceAll("official", "");
        replicant = replicant.replaceAll("VIDEO", "");
        replicant = replicant.replaceAll("Video", "");
        replicant = replicant.replaceAll("video", "");
        replicant = replicant.replaceAll("[T-t]radução", "");
        replicant = replicant.replaceAll("TRADUÇÃO", "");
        name = replicant.trim() + "." + ext;
        return name;
    }
    public String downloadFile(String name, Message.Attachment attachment, MessageChannel channel, Guild g, String user){
        this.setMusics();
        if (verifyExtension(attachment.getFileExtension().toUpperCase())) {
            name = this.removeSymbols(name, attachment.getFileExtension());
            if (this.getMusics().add(name)) {
                this.getMusics().remove(name);
                attachment.downloadToFile(this.getDir().getAbsolutePath() + "/" + name
                                .replaceAll(attachment.getFileExtension().toUpperCase(), ""))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            return null;
                        });
                try {
                    channel.sendMessage("Adicionando a música ao banco. Aguarde...").queue();
                    Thread.sleep(3000);
                    this.getCloud().uploadFile(name ,attachment.getFileExtension());
                } catch (IOException | InterruptedException e){
                    channel.sendMessage("Não consegui fazer upload :cry:").queue();
                }
                name = this.getURLFile(attachment);
                MainPlayer.getName_music().replace(g, this.removeSymbols(attachment.getFileName()));
                channel.sendMessage(user + ",**SUCESSO**, música **" + MainPlayer.getName_music().get(g) + "** adicionada ao banco de músicas").queue();
                return name;
            } else {
                MainPlayer.getName_music().replace(g, this.getDir() + "/" + name);
                channel.sendMessage(user +",A música já estava no banco").queue();
                String query = String.format("name = '%s'", name);
                try {
                        this.getCloud().downloadFile(name, this.getCloud().getDriver().files().list().setQ(query).execute().getFiles().get(0).getId());
                } catch (IOException e){
                        e.printStackTrace();
                }
                this.setMusics();
                return this.getDir() + "/" + name;
            }
        } else{
            throw new IllegalArgumentException(BotConstants.INVALID_FILE_FORMAT.getConstants());
        }
    }
    public String getURLFile(Message.Attachment attachment){
        return attachment.getUrl();
    }
    public List<String> enhancedSearch(String name, List<String> searchs){
        boolean found = false;
        char[] alpha =  {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' '};
        String[] search = name.split(" ");
        for (int c =0;c< search.length;c++) {
            String actual = search[c];
            final String copyActual = actual;
            Stream<String> corrects = this.getMusics().stream().filter(m -> m.toUpperCase().contains(copyActual));
            if (corrects.count() == 0) {
                for (char caracter : alpha) {
                    char[] array = actual.toCharArray();
                    int i = 0;
                    while (i < array.length && !found) {
                        char[] copy = array.clone();
                        copy[i] = caracter;
                        String one = new String(copy);
                        final String end = one.replaceAll(" ", "");
                        Stream<String> stream = this.getMusics().stream()
                                .filter(m -> m.toUpperCase().contains(end));
                        searchs.addAll(stream.collect(Collectors.toCollection(ArrayList::new)));
                        if (!searchs.isEmpty()) {
                            name = name.replaceAll(actual, end);
                            found = true;
                        }
                        i++;
                    }
                }
            }
        }
            if (searchs.isEmpty()){
                for (int c = 0; c< search.length;c++){
                    String actual = search[c];
                    final String annotherCopy = actual;
                    Stream<String> corrects =this.getMusics().stream().filter(m -> m.toUpperCase().contains(annotherCopy));
                    if (corrects.count() == 0) {
                        if (actual.length() > 3) {
                            for (char caracter : alpha) {
                                ArrayList<Character> chars = new ArrayList<>();
                                for (char e : actual.toCharArray()) {
                                    chars.add(e);
                                }
                                int i = 0;
                                while (i < chars.size() && !found) {
                                    ArrayList<Character> copy = new ArrayList<>(chars);
                                    copy.add(i, caracter);
                                    String one = "";
                                    for (char e : copy) {
                                        one += String.valueOf(e);
                                    }
                                    final String end = one.replaceAll(" ", "");
                                    Stream<String> stream = this.getMusics().stream().filter(m ->
                                            m.toUpperCase().contains(end));
                                    searchs = stream.collect(Collectors.toCollection(ArrayList::new));
                                    if (!searchs.isEmpty()) {
                                        name = name.replaceAll(actual, end);
                                        found = true;
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                }
            }
            if (searchs.isEmpty()) {
                for (int c = 0; c < search.length; c++) {
                    String actual = search[c];
                    final String copyActual = actual;
                    Stream<String> corrects = this.getMusics().stream().filter(m -> m.toUpperCase().contains(copyActual));
                    if (corrects.count() == 0) {
                        for (char caracter : alpha) {
                            for (char other : alpha) {
                                char[] array = actual.toCharArray();
                                int i = 0;
                                while (i < array.length && !found) {
                                    int i2 = 0;
                                    while (i2 < array.length && !found) {
                                        char[] copy = array.clone();
                                        copy[i] = caracter;
                                        if (i2 != i) {
                                            copy[i2] = other;
                                        }
                                        String one = new String(copy);
                                        final String end = one.replaceAll(" ", "");
                                        Stream<String> stream = this.getMusics().stream().filter(m -> m.toUpperCase().contains(end));
                                        searchs = stream.collect(Collectors.toCollection(ArrayList::new));
                                        if (!searchs.isEmpty()) {
                                            name = name.replaceAll(actual, end);
                                            found = true;
                                        }
                                        i2++;
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                }
            }
        search = name.split(" ");
        for (int c = 0; c< search.length; c++){
            final String end = search[c];
            Stream<String> stream = searchs.stream().filter(m -> m.toUpperCase().contains(end));
            searchs = stream.collect(Collectors.toCollection(ArrayList::new));
        }
        return searchs;
    }
    public String searchFile(String name) {
        this.setMusics();
        final String file_name = name;
        Stream<String> stream = this.getMusics().stream().filter(m -> m.replaceAll("-", " ").toUpperCase().contains(file_name));
        List<String> searchs = stream.collect(Collectors.toCollection(ArrayList::new));
       if (searchs.isEmpty()){
           final String[] split = name.split(" ");
           int c = 0;
           while (searchs.isEmpty() && c < split.length){
               final int e = c;
               stream = this.getMusics().stream().filter(m -> m.toUpperCase().contains(split[e]));
               searchs = stream.collect(Collectors.toCollection(ArrayList::new));
               c++;
           }
           for (c = 0;c < split.length;c++){
               final int e = c;
               stream = searchs.stream().filter(m ->
                       m.toUpperCase().contains(split[e]));
               searchs = stream.collect(Collectors.toCollection(ArrayList::new));
           }
       }
       if (searchs.isEmpty()){
           searchs = this.enhancedSearch(name, searchs);
       }
       Random n = new Random();
       if (!searchs.isEmpty()) {
           return String.format("%s",searchs.size() > 1 ? searchs.get(n.nextInt(searchs.size()
           )):searchs.get(0));
       }else {
           return null;
       }
    }
    public List<String> searchFiles(String correspondency){
        this.setMusics();
        List<String> searchs;
        Stream<String> stream = this.getMusics().stream().filter(m -> m.toUpperCase().contains(correspondency));
        searchs = stream.collect(Collectors.toCollection(ArrayList::new));
        if (searchs.isEmpty()){
            final String[] split = correspondency.split(" ");
            int c = 0;
            while (searchs.isEmpty() && c < split.length){
                final int e = c;
                stream =this.getMusics().stream().filter(
                        m -> m.toUpperCase().contains(split[e]));
                searchs = stream.collect(Collectors.toCollection(ArrayList::new));
                c++;
            }
            for (c=0;c < split.length;c++){
                final int e = c;
                stream = searchs.stream().filter(m -> m.toUpperCase().contains(split[e]));
                searchs = stream.collect(Collectors.toCollection(ArrayList::new));
            }
        }
        return searchs.stream().sorted().collect(Collectors.toList());
    }
    public void deleteFile(String name) throws IOException {
        String query = String.format("name='%s'", name);
        String id = this.getCloud().getDriver().files().list().setQ(query).setFields("nextPageToken, files(id)")
                .execute().getFiles().get(0).getId();
        boolean deleted = this.getCloud().deleteFile(id);
        if (deleted) {
            this.setMusics();
        }
    }
    public Set<String> getMusics() {
        return musics;
    }

    public void setMusics() {
        this.getCloud().setDriver();
        try {
            List<com.google.api.services.drive.model.File> list = this.getCloud().getDriver().files().list().setQ("not name contains 'audiofiles'").setFields("nextPageToken, files(name)").execute().getFiles();
            for (com.google.api.services.drive.model.File f : list) {
                this.musics.add(f.getName());
            }
        }catch (NullPointerException n){
            n.fillInStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public String[] getSymbols() {
        return symbols;
    }

    public void setSymbols(String[] symbols) {
        this.symbols = symbols;
    }

    public DriveManager getCloud() {
        return this.cloud;
    }
}
