package Fuzzcode;

public class Message {
    public String MessageContent;
    public String OutputContent;
    public String Channel = null;
    public String Character;
    public Boolean IsPrivate;

    //Constructor for Private Message
    public Message(String messageContent, String character) {
        this.MessageContent = messageContent;
        this.Character = character;
        this.IsPrivate = true;
    }
    //Constructor for Channel Message
    public Message(String messageContent, String channel, String character) {
        this.MessageContent = messageContent;
        this.Character = character;
        this.Channel = channel;
        this.IsPrivate = false;
    }
    public void AddOutputMessage(String string){
        OutputContent=string;
    }
    public String GetOutputContent(){
        return OutputContent;
    }
}

