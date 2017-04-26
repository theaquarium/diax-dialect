package me.diax.dialect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Trainer {
    private static String readConsole() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = br.readLine();
        return input;
    }

    public static void train1() throws IOException {
        System.out.println("Enter 'train' to begin training \n");
        String console = readConsole().toLowerCase();
        while (!console.equals("train")) {
            System.out.println("Enter 'train' to begin training \n");
            console = readConsole();
        }
        System.out.println("Enter a message: \n");
        String input = readConsole();
        train2(input);
    }

    private static void train2(String input) throws IOException {
        String output = API.ask(input);
        System.out.println(output);
        System.out.println("Is this a good response for your message (yes or no)?\n");
        String console = readConsole().toLowerCase();
        while (!console.equals("yes") && !console.equals("no")) {
            System.out.println("Please say 'yes' or 'no':\n");
            console = readConsole().toLowerCase();
        }
        train3(console, input, output);
    }

    private static void train3(String input, String originalInput, String response) throws IOException {
        if (input.equals("yes")) {
            int originalInputId = databaseContains(originalInput);
            if (originalInputId == -1) {
                originalInputId = createMessageRow(originalInput);
            }
            int responseId = databaseContains(response);
            if (responseId == -1) {
                responseId = createMessageRow(response);
            }
            int linkId = isLinked(originalInputId, responseId);
            if (linkId >= 0) {
                incrementWeight(linkId);
            }
            else {
                createLink(originalInputId, responseId);
            }
            System.out.println("Recorded!");
        }
        else if (input.equals("no")) {
            System.out.println("What is a good answer for your message?\n");
            String proposedResponse = readConsole();
            train4(proposedResponse, originalInput);
        }
    }

    private static void train4(String proposedResponse, String originalInput) throws IOException {
        int originalInputId = databaseContains(originalInput);
        if (originalInputId == -1) {
            originalInputId = createMessageRow(originalInput);
        }
        int responseId = databaseContains(proposedResponse);
        if (responseId == -1) {
            responseId = createMessageRow(proposedResponse);
        }
        int linkId = isLinked(originalInputId, responseId);
        if (linkId >= 0) {
            incrementWeight(linkId);
        }
        else {
            createLink(originalInputId, responseId);
        }
        System.out.println("Recorded!");
    }

    private static int databaseContains(String input) {
        int output = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diaxdialect", "diaxdialect", "diaxDialect");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select id from messages where text='" + input + "'");
            if (rs.next()) {
                int id = rs.getInt(1);
                output = id;
            }
            con.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return output;
    }

    private static int isLinked(int id1, int id2) {
        int output = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diaxdialect", "diaxdialect", "diaxDialect");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from links where input_id='" + id1 + "' and output_id='" + id2 + "'");
            if (rs.next()) {
                int id = rs.getInt(1);
                output = id;
            }
            con.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return output;
    }

    private static void incrementWeight(int input) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diaxdialect", "diaxdialect", "diaxDialect");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from links where id='" + input + "'");
            if (rs.next()) {
                stmt.executeUpdate("update links set weight = weight + 1 where id='" +  input + "'");
            }
            con.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static void createLink(int id1, int id2) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diaxdialect", "diaxdialect", "diaxDialect");
            Statement stmt = con.createStatement();
            stmt.executeUpdate("insert into links (input_id, output_id, weight) values(" + id1 +", " + id2 + ", 0)");
            con.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static int createMessageRow(String input) {
        int output = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diaxdialect", "diaxdialect", "diaxDialect");
            Statement stmt = con.createStatement();
            stmt.executeUpdate("insert into messages (text) values('" + input + "')");
            output = databaseContains(input);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return output;
    }
}
