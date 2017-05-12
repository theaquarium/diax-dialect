package me.diax.dialect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {
    public static String readConsole() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

    public static void main(String[] args) throws IOException {
        Trainer trainer = new Trainer();
        System.out.println("Enter 'train' to begin training \n");
        String train = readConsole().toLowerCase();
        while (!train.equals("train") && !train.equals("ask")) {
            System.out.println("Enter 'train' to begin training or 'ask' to ask \n");
            train = readConsole();
        }
        if (train.equals("train")) {
            System.out.println("Enter a message: \n");
            String originalInput = readConsole();
            String output = API.ask(originalInput);
            System.out.println(output);
            System.out.println("Is this a good response for your message (yes or no)?\n");
            String goodResponse = readConsole().toLowerCase();
            while (!goodResponse.equals("yes") && !goodResponse.equals("no")) {
                System.out.println("Please say 'yes' or 'no':\n");
                goodResponse = readConsole().toLowerCase();
            }
            String result = trainer.train(goodResponse, originalInput, output);
            if (result.equals("askResponse")) {
                System.out.println("What is a good answer for your message?\n");
                String proposedResponse = readConsole();
                trainer.createDependenciesAndIncrement(proposedResponse, originalInput);
            }
        }
        if (train.equals("ask")) {
            System.out.println("Enter a message: \n");
            String originalInput = readConsole();
            System.out.println(API.ask(originalInput));
        }
    }
}
