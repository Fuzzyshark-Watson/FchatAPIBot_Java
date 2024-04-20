package Fuzzcode;
public class Main {
    public static void main(String[] args) {
        BotMain Bot = new BotMain();
        Bot.Run();
        if (BotMain.DebugMode){System.out.println("Run Loop exited.");}
        System.exit(0);
    }
}
