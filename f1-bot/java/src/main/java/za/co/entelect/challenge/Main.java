package za.co.entelect.challenge;

import com.google.gson.Gson;
import za.co.entelect.challenge.entities.GameState;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {



    /**
     * Read the current state, feed it to the bot, get the output and print it to stdout
     *
     * @param args the args
     **/
    public static void main(String[] args) {
        Bot bot = new Bot();
        bot.run();
    }
}
