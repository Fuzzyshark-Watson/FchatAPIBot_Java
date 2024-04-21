Important Links:
F-list API for any other command-strings you need: https://toys.in.newtsin.space/api-docs/#f-list-api 
F-List Rules:
https://wiki.f-list.net/F-Chat_Protocol#Bots 
### ENSURE YOU HAVE EXPLICIT CONSENT TO USE F-LIST, and that your bot profile explicitly states you as its owner and that it is a bot.

1: DEPENDENCIES
To make this work you need to verify you have all necessary sources, my gradle includes the following dependencies:
dependencies {
    testImplementation platform('org.junit:junit-bom:5.9.1')
    testImplementation 'org.junit.jupiter:junit-jupiter'
    //Websocket Implementations
    implementation 'com.squareup.okhttp3:okhttp:5.0.0-alpha.9'
    //JSON
    implementation 'org.json:json:20210307'
}

2: ACCOUNT SETTINGS

In BotMain you can define where the Local AccountSettings is located.
Example: public String AccountSettingsFile = "C:\\BotData\\PowerBot\\AccountSettings.txt";

!!! If you pull this program, make sure the AccountSettingsFile string points to your file !!!

***This file should be kept unavailable by anyone if you upload your program.***

3: ABOUT THE API

The F-chat API works in the following way:
1. You open a request to get a new ticket. This lasts for 30 minutes each.
2. You then contact their websocket with an IDN request using that ticket to log in.
3. Every 30 seconds their server sends a PIN, you then reply PIN or get booted off after 3 failed attempts.

4: DEBUGMODE

Your Botmain class has a Debugmode boolean in your webSocketClient. 
If toggled it will post more information to the console.

5: HOW_TO

In order to run the program, look at your //todo's and add them accordingly.
1. Setup your accountsettings.
2. Add your commands both in the BotMain<LoadBotCommands() method. Draw inspiration from what is already there.
3. Add your commands implementation in the BotCommandController. Draw inspiration from what is already there.

6: STATUS

Your WebSocketClient has 2 statuses one for debug mode and one for normal running. Editable from the WebClient.

X: CHAR_ID

It is possible to get not a character name but a character ID, your BotMain class has this method, which could be used to get a users' ID which wouldn't change when characters are renamed.

Q&A: 

Q:1 I get the following ticket, what do I do?

Response Code: 200

Response: {"ticket":"fct_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx","error":""}

A:1 Good news. Every ticket looks like that, "error" is a field in the response ticket, which is empty. Which means it works! Code 200 means succes.

Q:2 Newlines in output messages breaks the message I'm trying to send!
A:2 /n doesn't work, try using 'your string first line+ Bot.nl +your string next line'

Q:3 What to do when I receive: ERR {"number":1,"message":"Syntax error."}
A:3 ERR is an API response from F-chat that the message was not valid, in this case, go through the message you sent them, bit by bit to locate the problem. It is highly likely that either your newlines or some of the characters you've used for indentation breaks the message.
