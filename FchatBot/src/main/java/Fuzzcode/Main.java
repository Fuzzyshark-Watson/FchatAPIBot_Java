package Fuzzcode;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BotMain Bot = new BotMain();

        Bot.Run();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter to exit the loop.");

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("")) {
                break; // Exit the loop if Enter key is pressed
            }
        }
        if (BotMain.DebugMode){System.out.println("Run Loop exited.");}
        System.exit(0);
    }
}
