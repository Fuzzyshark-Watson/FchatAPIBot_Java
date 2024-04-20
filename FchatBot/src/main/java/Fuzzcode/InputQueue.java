package Fuzzcode;

import java.util.ArrayDeque;
import java.util.Queue;

public class InputQueue {
    Queue<Message> messages;

    public InputQueue() {
        messages = new ArrayDeque<>();
    }
    public Message getNextInMessage() {
        return messages.poll();
    }
    public void addInMessage(Message message) {
        messages.offer(message);
    }
    public boolean hasMessages() {
        return !messages.isEmpty();
    }
}
