package me.diax.dialect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Trainer {
    public static String readConsole() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

    public static String train2(String input) throws IOException {
        String output = API.ask(input);
        return output;
    }

    public static void createDependenciesAndIncrement(String response, String originalInput) {
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

    public static String train3(String input, String originalInput, String response) throws IOException {
        String result = "";
        if (input.equals("yes")) {
            createDependenciesAndIncrement(response, originalInput);
            result = "recorded";
        }
        else if (input.equals("no")) {
            result = "askResponse";
        }
        return result;
    }

    public static void train4(String proposedResponse, String originalInput) throws IOException {
        createDependenciesAndIncrement(proposedResponse, originalInput);
    }

    private static int databaseContains(String input) {
        int output = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diaxdialect", "diaxdialect", "diaxDialect");
            PreparedStatement stmt = con.prepareStatement("select id from messages where text=?");
            stmt.setString(1, input);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                output = rs.getInt(1);
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
                output = rs.getInt(1);
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
            PreparedStatement stmt = con.prepareStatement("insert into messages (text) values(?)");
            stmt.setString(1, input);
            stmt.executeUpdate();
            output = databaseContains(input);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return output;
    }
}
