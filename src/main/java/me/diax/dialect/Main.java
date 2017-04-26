package me.diax.dialect;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter 'train' to begin training \n");
        String train = Trainer.readConsole().toLowerCase();
        while (!train.equals("train")) {
            System.out.println("Enter 'train' to begin training \n");
            train = Trainer.readConsole();
        }
        System.out.println("Enter a message: \n");
        String originalInput = Trainer.readConsole();
        String output = Trainer.train2(originalInput);
        System.out.println(output);
        System.out.println("Is this a good response for your message (yes or no)?\n");
        String goodResponse = Trainer.readConsole().toLowerCase();
        while (!goodResponse.equals("yes") && !goodResponse.equals("no")) {
            System.out.println("Please say 'yes' or 'no':\n");
            goodResponse = Trainer.readConsole().toLowerCase();
        }
        String result = Trainer.train3(goodResponse, originalInput, output);
        if (result.equals("askResponse")){
            System.out.println("What is a good answer for your message?\n");
            String proposedResponse = Trainer.readConsole();
            Trainer.train4(proposedResponse, originalInput);
        }
    }
}
