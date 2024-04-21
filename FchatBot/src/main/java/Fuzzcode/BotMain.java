package Fuzzcode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Timer;
public class BotMain {

    public static boolean DebugMode = false;

    //todo ensure AccountSettings.txt has the correct login information.
    public String AccountSettingsFile = "C:\\BotData\\PowerBot\\AccountSettings.txt";
    static String accountName = "";
    static String accountPassword = "";
    static String botCharacterName = "";
    static String botCname = "";
    static String channelName = "";
    static String API_URL = "https://www.f-list.net/json/getApiTicket.php";
    public static String CurrentApiKey = "";

    BotCommandController controller = new BotCommandController();

    private WebSocketClient webSocketClient;

    public void Run() {
        LoadAccountSettings();
        LoadBotCommands();
        StartWebSocketClient();
    }
    private void StartWebSocketClient() {
        InputQueue inQueue = new InputQueue();
        BotMessageQueue outQueue = new BotMessageQueue();
        webSocketClient = new WebSocketClient(inQueue);
        webSocketClient.openConnection();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (inQueue.hasMessages()) {
                    Message incomingMessage = inQueue.getNextInMessage();
                    Message outgoingMessage = controller.processCommand(incomingMessage);
                    outQueue.addOutMessage(outgoingMessage);
                }

                if (outQueue.hasMessages()) {
                    webSocketClient.sendOutputMessage(outQueue.getNextOutMessage());
                }

            }
        }, 2000, 500);
    }
    private void LoadAccountSettings(){
        // Loading our local values by splitting ValueType:Value;
        System.out.println("Initializing variables...");
        String[] values = new String[5];
        System.out.println("Processing file: " +AccountSettingsFile);
        try (BufferedReader br = new BufferedReader(new FileReader(AccountSettingsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line by ;
                String[] parts = line.split(";");
                for (String part : parts) {
                    // Split each part by :
                    String[] keyValue = part.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        String value = keyValue[1].trim();
                        // Check for keys and assign values accordingly
                        switch (key) {
                            case "AccountName":
                                values[0] = value;
                                break;
                            case "AccountPassword":
                                values[1] = value;
                                break;
                            case "CharacterName":
                                values[2] = value;
                                break;
                            case "CName":
                                values[3] = value;
                                break;
                            case "Channel":
                                values[4] = value;
                                break;
                            default:
                                if (DebugMode){
                                System.out.println("Value: "+key+". Doesn't parse.");
                                }
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        accountName = values[0];
        accountPassword = values[1];
        botCharacterName = values[2];
        botCname = values[3];
        channelName = values[4];
        if (DebugMode){
            System.out.println("AccountSettings:");
            System.out.println("Account Name: "+accountName);
            System.out.println("Account Password: "+accountPassword);
            System.out.println("Bot Charactername: "+botCharacterName);
            System.out.println("Bot CName: "+botCname);
            System.out.println("Account Password: "+channelName);
        }
    }
    private void LoadBotCommands(){
        //todo add all permissible commands here.
        controller.registerCommand("!hello", new BotCommandController.HelloCommand());
        controller.registerCommand("!register", new BotCommandController.RegisterCommand());
        controller.registerCommand("!goodnight", new BotCommandController.GoodnightCommand());
        if (DebugMode){System.out.println("Loaded BotCommands");}
    }
    public static String RequestNewTicket() {
        // Get API ticket
        try {
            // Construct the URL with query parameters
            String noCharacters = "true";
            String noFriends = "true";
            String noBookmarks = "true";

            String urlStr = API_URL +
                    "?account=" + URLEncoder.encode(accountName, StandardCharsets.UTF_8) +
                    "&password=" + URLEncoder.encode(accountPassword, StandardCharsets.UTF_8) +
                    "&CharacterName=" + URLEncoder.encode(botCharacterName, StandardCharsets.UTF_8) +
                    "&no_characters=" + noCharacters +
                    "&no_friends=" + noFriends +
                    "&no_bookmarks=" + noBookmarks;

            // Create a URL object
            URL url = new URL(urlStr);

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();
            if (DebugMode){System.out.println("Response Code: " + responseCode);}

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response
            if (DebugMode){System.out.println("Response: " + response);}
            String regex = "\"ticket\":\\s*\"(\\w+)\"";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(response.toString());

            if (matcher.find()) {
                CurrentApiKey = matcher.group(1);
            } else {
                // Handle case where no match is found
                CurrentApiKey = ""; // Or any other appropriate value
            }
            // Close the connection
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CurrentApiKey;
    }
    public int CheckCharID(String account, String name) {
        //todo Method for finding the Character ID, Development for the future, if needed. Would make it easier to handle characters getting new names.
        try {
            // Construct the URL
            String urlStr = "https://www.f-list.net/json/api/character-memo-get2.php";

            // Encode form parameters
            String requestBody = "account=" + URLEncoder.encode(account, StandardCharsets.UTF_8) +
                    "&target=" + URLEncoder.encode(name, StandardCharsets.UTF_8) +
                    "&ticket=" + URLEncoder.encode(RequestNewTicket(), StandardCharsets.UTF_8);

            // Create an HttpRequest object with POST method and form parameters
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send the request and receive the response
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Get and print the response code
            int responseCode = response.statusCode();
            if (DebugMode){System.out.println("Response Code: " + responseCode);}

            // Get and print the response body
            String responseBody = response.body();
            if (DebugMode){System.out.println("Response: " + responseBody);}

            int id = extractIdFromJson(responseBody);
            if (DebugMode){System.out.println("Extracted id: " + id);}
            return id;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public int extractIdFromJson(String jsonInput) {
        // Use regex to find the id field and extract its value
        Pattern pattern = Pattern.compile("\"id\":(\\d+)");
        Matcher matcher = pattern.matcher(jsonInput);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException("No id found in the JSON input");
        }
    }

}
