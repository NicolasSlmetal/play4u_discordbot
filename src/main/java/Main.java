
import listeners.EventReader;
import listeners.MessageReader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;


public class Main {
    private static  String token;
    public static String getEnv(){
        token = System.getenv("TOKEN");
        return token;
    }
    public static void main(String[] args){
        try {
            JDA builder = JDABuilder.createDefault(getEnv())
                    .setActivity(Activity.playing("Irineu, você não sabe nem eu"))
                    .addEventListeners(new EventReader())
                    .addEventListeners(new MessageReader())
                    .setAutoReconnect(true)
                    .disableCache(CacheFlag.CLIENT_STATUS)
                    .disableCache(CacheFlag.ACTIVITY)
                    .build();
        } catch (LoginException le){
            le.printStackTrace();
        }
    }
}
