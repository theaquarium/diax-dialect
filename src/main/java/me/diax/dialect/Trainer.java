package me.diax.dialect;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;

class Trainer {
    static void createDependenciesAndIncrement(String response, String originalInput) {
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


    String train(String input, String originalInput, String response) {
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

    static int databaseContains(String input) {
        int output = -1;
        try {
            ResultSetBundle bundle = DatabaseOperations.query("select id from messages where text=?", new LinkedList<Object>(Arrays.asList(input)));
            ResultSet rs = bundle.getResultSet();
            if (rs.next()) {
                output = rs.getInt(1);
            }
            bundle.closeAll();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return output;
    }

    static int isLinked(int id1, int id2) {
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

    static void incrementWeight(int input) {
        try {
            DatabaseOperations.update("update links set weight = weight + 1 where id='" +  input + "'", null);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    static void createLink(int id1, int id2) {
        try {
            DatabaseOperations.update("insert into links (input_id, output_id, weight) values(" + id1 +", " + id2 + ", 0)", null);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    static int createMessageRow(String input) {
        int output = -1;
        try {
            DatabaseOperations.update("insert into messages (text) values(?)", new LinkedList<Object>(Arrays.asList(input)));
            output = databaseContains(input);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return output;
    }
}
