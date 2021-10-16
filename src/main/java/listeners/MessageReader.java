package listeners;

import commands.CmdDel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;


public class MessageReader extends ListenerAdapter {
    private static String prefix = "=";
    public static boolean wait_answer = false;
    public CommandChecker loader = new CommandChecker();

    public CommandChecker getLoader() {
        return loader;
    }

    public void setLoader(CommandChecker loader) {
        this.loader = loader;
    }

    public static boolean isWait_answer() {
        return wait_answer;
    }

    public static void setWait_answer(boolean wait_answer) {
        MessageReader.wait_answer = wait_answer;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static void setPrefix(String prefix) {
        MessageReader.prefix = prefix;
    }

    public void onMessageReceived(MessageReceivedEvent message) {
        if (message.getAuthor().isBot() || message.isFromType(ChannelType.PRIVATE)) {
            return;
        }
        String user_mention = message.getAuthor().getAsMention();
        String msg = message.getMessage().getContentRaw();
        msg = msg.trim().toUpperCase();
        if (msg.startsWith(getPrefix()) && !wait_answer) {
            this.getLoader().loadCommand(msg, message, user_mention);
        } else if (isWait_answer() && message.getAuthor().getName().equals(CmdDel.getMember())) {
            if (msg.contains("SIM")) {
                this.getLoader().executeDangerousOperation(user_mention, message);
                message.getChannel().sendMessage("Música deletada com sucesso").queue();
                MessageReader.setWait_answer(false);
            } else if (msg.contains("NÃO")) {
                message.getChannel().sendMessage("Ok, não vou deletar").queue();
                MessageReader.setWait_answer(false);
            } else {
                message.getChannel().sendMessage("Digite uma opção válida [S/N]").queue();
            }
        }if (EventReader.isWait_prefix() && !message.getAuthor().isBot()) {
            String[] invalid = {"{", "}", "?", "#", "@", "*", ".", "$", ":", "|", "(", ")", "[", "]"};
            if (message.getMessage().getContentRaw().toUpperCase().startsWith("PREFIX")) {
                String symbol = message.getMessage().getContentRaw().toUpperCase().replace("PREFIX", "").trim();
                if (symbol.matches("[A-Z]") || symbol.matches("[0-9]") || symbol.length() > 1 || Arrays.stream(invalid)
                        .anyMatch(s -> s.equals(symbol))){
                    message.getChannel().sendMessage("Pré-fixo inválido, use símbolos válidos").queue();
                } else {
                    prefix = symbol;
                    message.getChannel().sendMessage("**SUCESSO**, o pré-fixo agora é \"" + prefix + "\". Se desejar" +
                            " alterar, use o comando **" + prefix + "prefixo**").queue();
                    EventReader.setWait_prefix(false);
                }
            }
        }
    }
}