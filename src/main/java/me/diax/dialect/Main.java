package me.diax.dialect;

import com.knockturnmc.api.util.ConfigurationUtils;
import com.knockturnmc.api.util.sql.SqlDatasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {
    private static SqlDatasource datasource = null;

    static SqlDatasource getConnectionPool() {
        return datasource;
    }

    private static String readConsole() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

    public static void main(String[] args) throws IOException {
        ClassLoader cl = Main.class.getClassLoader();
        DatabaseProperties properties = ConfigurationUtils.loadConfiguration(cl, "database.properties", ConfigurationUtils.getDataFolder(), DatabaseProperties.class);
        datasource = new DatabaseConnectionPool(properties);
        System.out.println("Enter 'train' to begin training or 'ask' to ask \n");
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
            String isGoodResponse = readConsole().toLowerCase();
            while (!isGoodResponse.equals("yes") && !isGoodResponse.equals("no")) {
                System.out.println("Please say 'yes' or 'no':\n");
                isGoodResponse = readConsole().toLowerCase();
            }
            if (isGoodResponse.equals("yes")) {
                API.createDependenciesAndIncrement(originalInput, output);
            }
            else {
                System.out.println("What is a good answer for your message?\n");
                String proposedResponse = readConsole();
                API.createDependenciesAndIncrement(proposedResponse, originalInput);
            }
        }
        if (train.equals("ask")) {
            System.out.println("Enter a message: \n");
            String originalInput = readConsole();
            System.out.println(API.ask(originalInput));
        }
    }
}
