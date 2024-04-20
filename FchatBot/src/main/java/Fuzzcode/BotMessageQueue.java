package Fuzzcode;

import java.util.ArrayDeque;
import java.util.Queue;

public class BotMessageQueue {
    Queue<Message> outMessages;

    public BotMessageQueue() {
        outMessages = new ArrayDeque<>();
    }
    public Message getNextOutMessage() {
        return outMessages.poll();
    }
    public void addOutMessage(Message message) {
        outMessages.offer(message);
    }
    public boolean hasMessages() {
        return !outMessages.isEmpty();
    }
}
