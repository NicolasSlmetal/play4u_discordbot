package utilities;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class URLFinder {
    private URL url;
    private HttpURLConnection connecter;
    public boolean isUrl(String text){
        try{
            this.setUrl(new URL(text));
            return true;
        } catch (MalformedURLException e){
            return false;
        }
    }
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public HttpURLConnection getConnecter() {
        return connecter;
    }

    public void setConnecter(HttpURLConnection connecter) {
        this.connecter = connecter;
    }
}
