package com.play4ubot;

import listeners.EventReader;
import listeners.MessageReader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;


public class Main {
    public static void main(String[] args) throws NullPointerException{
        try {
            JDA builder = JDABuilder.createDefault(System.getenv("TOKEN"))
                    .setActivity(Activity.playing("Irineu, você não sabe nem eu"))
                    .addEventListeners(new EventReader())
                    .addEventListeners(new MessageReader())
                    .setAutoReconnect(true)
                    .disableCache(CacheFlag.CLIENT_STATUS)
                    .disableCache(CacheFlag.ACTIVITY)
                    .build();
        } catch (LoginException le){
            le.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
