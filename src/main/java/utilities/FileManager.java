package utilities;

import audiopackage.MainPlayer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {
    private Set<String> musics = new HashSet<>();
    private File dir;
    private List<String> symbols;
    public FileManager(){
        this.dir = new File(System.getProperty("user.dir") + "\\audiofiles");
        for (File f : this.dir.listFiles()){
            this.musics.add(f.getName().replace("_", " "));
        }
        String[] s = {"!", "(", ")", "?", "<", ">", "´", "'", "\"",
        "&", "_", "/", "[", "]"};
        this.symbols = Arrays.stream(s).toList();
    }
    public boolean verifyExtension(String ext){
        String[] valid = {"MP3", "M4A", "OPUS", "MP4", "AA3", "FLAC", "WAV"};
        return Arrays.stream(valid).toList().contains(ext);
    }
    public String removeSymbols(String name){
        for (String s: this.getSymbols()){
            if (!s.equals("_") && !s.equals("'") & !s.equals("/")) {
                name = name.replace(s, "");
            }else{
                name = name.replace(s, " ");
            }
        }
        return name;
    }
    public String removeSymbols(String name, String ext){
        for (String s: this.getSymbols()){
            if (!s.equals("_") && !s.equals("/") && !s.equals("'")) {
                name = name.replace(s, "");
            }else{
                name = name.replace(s, " ");
            }
        }
        String replicant = name.replace("." + ext, "");
        replicant = replicant.trim();
        replicant = replicant.replace(ext.toUpperCase(), "");
        replicant = replicant.replaceAll("[1-9][1-9][1-9][K-k]", "").trim();
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
    public String downloadFile(String name, Message.Attachment attachment, MessageChannel channel, String user){
        this.setMusics();
        if (verifyExtension(attachment.getFileExtension().toUpperCase())) {
            name = this.removeSymbols(name, attachment.getFileExtension());
            if (this.getMusics().add(name)) {
                this.getMusics().remove(name);
                attachment.downloadToFile(this.getDir().getAbsolutePath() + "\\" + name
                                .replaceAll(attachment.getFileExtension().toUpperCase(), ""))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            return null;
                        });
                name = this.getURLFile(attachment);
                MainPlayer.setName_music(this.removeSymbols(attachment.getFileName(),
                        attachment.getFileExtension()).trim());
                channel.sendMessage(user + ",**SUCESSO**, música **" + MainPlayer.getName_music() + "** adicionada ao banco de músicas").queue();
                return name;
            } else {
                MainPlayer.setName_music(this.getDir() + "\\" + name);
                channel.sendMessage(user +",A música já estava no banco").queue();
                return this.getDir() + "\\" + name;
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
            if (this.getMusics().stream().filter(m -> m
                    .substring(0, m.lastIndexOf(".")).toUpperCase().contains(copyActual)).toList().isEmpty()) {
                for (char caracter : alpha) {
                    char[] array = actual.toCharArray();
                    int i = 0;
                    while (i < array.length && !found) {
                        char[] copy = array.clone();
                        copy[i] = caracter;
                        String one = new String(copy);
                        final String end = one.replaceAll(" ", "");
                        searchs = this.getMusics().stream()
                                .filter(m -> m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(end)).collect(Collectors.toList());
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
                    if (this.getMusics().stream().filter(m -> m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(annotherCopy)).collect(Collectors.toList()).isEmpty()) {
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
                                            m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(end));
                                    searchs = stream.toList();
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
                    if (this.getMusics().stream().filter(m -> m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(copyActual)).collect(Collectors.toList()).isEmpty()) {
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
                                        Stream<String> stream = this.getMusics().stream().filter(m -> m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(end));
                                        searchs = stream.toList();
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
            Stream<String> stream = searchs.stream().filter(m -> m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(end));
            searchs = stream.toList();
        }
        return searchs;
    }
    public String searchFile(String name) {
        this.setMusics();
        final String file_name = name;
        Stream<String> stream = this.getMusics().stream().filter(m -> m.substring(0, m.lastIndexOf(".")).replaceAll("-", " ").toUpperCase().contains(file_name));
        List<String> searchs = stream.toList();
       if (searchs.isEmpty()){
           final String[] split = name.split(" ");
           int c = 0;
           while (searchs.isEmpty() && c < split.length){
               final int e = c;
               stream = this.getMusics().stream().filter(m -> m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(split[e]));
               searchs = stream.toList();
               c++;
           }
           for (c = 0;c < split.length;c++){
               final int e = c;
               stream = searchs.stream().filter(m ->
                       m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(split[e]));
               searchs = stream.toList();
           }
       }
       if (searchs.isEmpty()){
           searchs = this.enhancedSearch(name, searchs);
       }
       Random n = new Random();
       if (!searchs.isEmpty()) {
           return String.format("%s\\%s", this.getDir(), searchs.size() > 1 ? searchs.get(n.nextInt(searchs.size()
           )):searchs.get(0));
       }else {
           return null;
       }
    }
    public List<String> searchFiles(String correspondency){
        this.setMusics();
        List<String> searchs;
        Stream<String> stream = this.getMusics().stream().filter(m -> m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(correspondency));
        searchs = stream.toList();
        if (searchs.isEmpty()){
            final String[] split = correspondency.split(" ");
            int c = 0;
            while (searchs.isEmpty() && c < split.length){
                final int e = c;
                stream =this.getMusics().stream().filter(
                        m -> m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(split[e]));
                searchs = stream.toList();
                c++;
            }
            for (c=0;c < split.length;c++){
                final int e = c;
                stream = searchs.stream().filter(m -> m.substring(0, m.lastIndexOf(".")).toUpperCase().contains(split[e]));
                searchs = stream.toList();
            }
        }
        return searchs.stream().sorted().collect(Collectors.toList());
    }
    public void deleteFile(String name){
        File[] files = this.getDir().listFiles();
        for(File f: files){
            if (f.getName().equals(name)){
                f.delete();
                break;
            }
        }
    }
    public Set<String> getMusics() {
        return musics;
    }

    public void setMusics() {
        for (File f : this.dir.listFiles()) {
            this.musics.add(f.getName().replace("_", " "));
        }
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }
}
