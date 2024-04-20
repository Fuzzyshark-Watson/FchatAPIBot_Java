package Fuzzcode;

import java.util.HashMap;
import java.util.Map;

public class BotCommandController {
    private final Map<String, BotCommand> commands;

    public BotCommandController() {
        commands = new HashMap<>();
    }
    public void registerCommand(String commandName, BotCommand command) {
        commands.put(commandName, command);
    }
    public Message processCommand(Message message) {
        String inputcommand = getFirstWord(message.MessageContent);
        BotCommand command = commands.get(inputcommand);
        if (command != null) {
            command.execute(message);
        }
        else{
            message.AddOutputMessage("Unknown command: " + message.MessageContent);
        }
        return message;
    }
    public interface BotCommand {
        void execute(Message message);
    }
    //todo Example commands You should implement your own here.
    public static class HelloCommand implements BotCommand {
        @Override
        public void execute(Message message) {
            String[]process=splitMessage(message.MessageContent, 2);
            if(process.length<2){
                message.AddOutputMessage("That is not a valid command");
                message.IsPrivate=true;
            } else
            {
                message.AddOutputMessage(String.format("Greetings %s, and welcome to the bot!", process[1]));
            }
        }
    }
    public static class RegisterCommand implements BotCommand {
        @Override
        public void execute(Message message) {
            //execute the Register code here.
        }
    }
    public static class GoodnightCommand implements BotCommand {
        @Override
        public void execute(Message message) {
            //execute the shutdown code here
        }
    }
    public String getFirstWord(String input) {
        String[] words = input.split("\\s+");
        if (words.length > 0) {
            return words[0];
        } else {
            return "";
        }
    }
    public static String[] splitMessage(String message, int numberOfVariables) {
        String[] result = new String[numberOfVariables];
        String[] words = message.split("\\s+");
        for (int i = 0; i < numberOfVariables - 1; i++) {
            if (i < words.length) {
                result[i] = words[i];
            } else {
                result[i] = "";
            }
        }
        StringBuilder remainder = new StringBuilder();
        for (int i = numberOfVariables - 1; i < words.length; i++) {
            remainder.append(words[i]).append(" ");
        }
        result[numberOfVariables - 1] = remainder.toString().trim();
        return result;
    }
}

