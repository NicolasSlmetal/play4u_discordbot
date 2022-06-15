package com.play4ubot.listeners;

import java.io.IOException;
import java.util.HashMap;

import com.play4ubot.commands.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandChecker {
    private HashMap<String, CommandAction> cmdMap = new HashMap<>();
    public CommandChecker(){
        this.setCmdMap();
    }
    public HashMap<String, CommandAction> getCmdMap(){
        return this.cmdMap;
    }
    public void setCmdMap(){
        String[] names = {"OI", "PING", "HELP", "PLAY", "PAUSE", "SKIP", "STOP", "LOOP",
                "ADD", "BANCO", "ENTRAR", "SAIR", "DEL", "PREFIXO"};
        CommandAction[] cmd = {new CmdHello(), new CmdPing(), new CmdHelp(), new CmdPlay(), new CmdPause()
                , new CmdSkip(), new CmdStop(), new CmdLoop(), new CmdAdd(), new CmdBank(), new CmdJoin(), new CmdExit(),
                new CmdDel(), new CmdPrefix()};
        for (int c = 0; c < names.length; c++) {
            this.cmdMap.put(names[c], cmd[c]);
        }
    }
    public void loadCommand(String name, MessageReceivedEvent event, String user){
        String[] splitMsg = name.replaceFirst(MessageReader.getPrefix().get(event.getGuild()), "").trim().split(" ");
        String msg;
        for (String key : this.getCmdMap().keySet()){
            if (splitMsg[0].replaceFirst(MessageReader.getPrefix().get(event.getGuild()), "").trim().equals(key)){
                try{
                    this.getCmdMap().get(key).getCommand(name.substring(0, 1).trim() + name.substring(1), user, event);

                } catch (IllegalArgumentException illegal){
                    event.getChannel().sendMessage(user + "**,Comando inválido:no_entry_sign: **." + illegal.getMessage()
                    ).queue();
                } catch (UnsupportedOperationException wrong){
                    event.getChannel().sendMessage(user + "**,Comando inválido:no_entry_sign: **." + wrong.getMessage()
                    ).queue();
                } catch (Exception e){
                    event.getChannel().sendMessage(user +",ocorreu algum erro :cry:").queue();
                    e.printStackTrace();
                }
            }
        }
    }
    public void executeDangerousOperation(String user, MessageReceivedEvent event) throws IOException {
        this.getCmdMap().get("DEL").executeCommand("SIM", user, event);
    }
}
