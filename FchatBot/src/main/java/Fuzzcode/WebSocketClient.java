package Fuzzcode;

import okhttp3.*;
import okhttp3.WebSocket;
import okio.ByteString;
import org.json.JSONObject;

public class WebSocketClient {

    private static final String SOCKET_URL = "wss://chat.f-list.net/chat2";
    public static String Version = "1.0";
    public static String BotTestingStatus = "Bot is currently undergoing [color=yellow]testing[/color]. Performance may be impacted.";
    public static String BotOnlineStatus = String.format("[user]%s[/user] [color=yellow]v%s[/color] is Now Online!", BotMain.botCname,Version);
    private InputQueue inputQueue;
    private WebSocket webSocket;
    public WebSocketClient(InputQueue inputQueue){
        this.inputQueue = inputQueue;
    }
    public void openConnection() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SOCKET_URL).build();
        WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                handleWebSocketOpen(webSocket);
            }
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                processCommand(webSocket,text);
            }
            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if (BotMain.DebugMode){System.out.println("Received bytes: " + bytes.hex());}
            }
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                if (BotMain.DebugMode){System.out.println("WebSocket closed");}
            }
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                t.printStackTrace();
            }
        };
        webSocket = client.newWebSocket(request, webSocketListener);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> webSocket.close(1000, "Goodbye!")));
    }
    private void handleWebSocketOpen(WebSocket webSocket) {
        System.out.println("WebSocket opened");

        // IDN is the login request.
        String IDNCommand = fetchIDNString();
        webSocket.send(IDNCommand);
        System.out.println("Initial IDN request sent: " + IDNCommand);

        // JCH is the join channel request.
        String JCHCommand = fetchJCHString();
        webSocket.send(JCHCommand);
        System.out.println("Initial JCH request sent: " + JCHCommand);

        // STA is set status for the user.
        String STACommand = fetchSTAString();
        webSocket.send(STACommand);
        System.out.println("Initial STA request sent: " + STACommand);
    }
    public void sendOutputMessage(Message message) {
        if (this.webSocket != null && message.GetOutputContent() != null) {
            if (message.IsPrivate){
                webSocket.send(fetchPRIString(message.GetOutputContent(), message.Character));
            }
            else {
                webSocket.send(fetchMSGString(message.Channel,message.GetOutputContent()));
            }
        } else {
            System.out.println("Failed to send message: WebSocket or message string is null.");
        }
    }
    public void processCommand(WebSocket webSocket, String input) {
        // Convert input to uppercase for case-insensitive comparison
        String commandPrefix = input.length() >= 3 ? input.substring(0, 3).toUpperCase() : ""; // Get the first three letters of the input
        switch (commandPrefix) {
            case "MSG": //Message from Channel, same overall message structure as PRI.
                if (BotMain.DebugMode){System.out.println("Processing MSG command...");}
                inputQueue.addInMessage(NewInMessage(input, false));
                System.out.println("Processing MSG command..."+ input);
                break;
            case "PRI": //Private Messages do not have a channel, otherwise the same.
                if (BotMain.DebugMode){System.out.println("Processing PRI command...");}
                inputQueue.addInMessage(NewInMessage(input, true));
                System.out.println("Processing PRI command..."+ input);
                break;
            case "PIN":  //PIN is the server asking if the unit is still active, sent every 30 seconds. Returning PIN is a thumbs up.
                if (BotMain.DebugMode){System.out.println("Processing PIN command...");}
                webSocket.send("PIN");
                break;
            case "ERR":  //PIN is the server asking if the unit is still active, sent every 30 seconds. Returning PIN is a thumbs up.
                System.out.println(input);
                break;
            default:
                //System.out.println("Unknown command prefix: " + commandPrefix);
                //You receive a ton of messages so this isn't adviced.
                break;
        }
    }
    private static Message NewInMessage(String input, Boolean isPrivate) {
        String channel = null;
        String[] parts = input.split(" ", 2);
        JSONObject jsonObject = new JSONObject(parts[1]);
        String character = jsonObject.optString("character");
        String messageContent = jsonObject.optString("message");
        if (!isPrivate){
            channel = jsonObject.optString("channel");
        }
        if (character != null && messageContent != null) {
            if (channel != null) {
                return new Message(messageContent, channel, character); //Channel MSG
            } else {
                return new Message(messageContent, character); //PRI message
            }
        } else {
            System.out.println("Failure in NewInMessage: Invalid input");
            return null;
        }
    }
    private static String fetchSTAString(){
        String Status;
        if (BotMain.DebugMode){
            Status = String.format("STA {\"status\": \"A String\", \"statusmsg\": \"%s\"}",BotTestingStatus);
        }
        else{
            Status = String.format("STA {\"status\": \"A String\", \"statusmsg\": \"%s\"}",BotOnlineStatus);
        }
        return Status;
    }
    private static String fetchIDNString() {
        return String.format("IDN {\"method\":\"ticket\",\"account\":\"%s\",\"ticket\":\"%s\",\"character\":\"%s\",\"cname\":\"%s\",\"cversion\":\"x\"}", BotMain.accountName, BotMain.RequestNewTicket(), BotMain.botCharacterName, BotMain.botCname);
    }
    private static String fetchJCHString() {
        return String.format("JCH {\"channel\":\"%s\"}", BotMain.channelName);
    }
    private static String fetchPRIString(String message, String recipient) {
        return String.format("PRI {\"message\": \"%s\", \"recipient\": \"%s\"}", message, recipient);
    }
    private static String fetchMSGString(String channel, String message) {
        return String.format("MSG {\"channel\": \"%s\", \"message\": \"%s\"}", channel, message);
    }
}
