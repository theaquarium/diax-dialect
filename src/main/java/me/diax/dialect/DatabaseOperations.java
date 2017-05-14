package me.diax.dialect;

import com.knockturnmc.api.util.sql.SqlDatasource;

import java.sql.*;
import java.util.LinkedList;

//Singleton Database Utility Class
class DatabaseOperations {

    private SqlDatasource datasource = null;

    private static DatabaseOperations instance = new DatabaseOperations();

    static DatabaseOperations getInstance() {
        return instance;
    }

    private DatabaseOperations() {
        datasource = Main.getConnectionPool();
    }

    int databaseContains(String input) {
        int output = -1;
        try (Connection con = datasource.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("select id from messages where text=?");
            stmt.setString(1, input);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                output = rs.getInt(1);
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return output;
    }

    int isLinked(int id1, int id2) {
        int output = -1;
        try (Connection con = datasource.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from links where input_id='" + id1 + "' and output_id='" + id2 + "'");
            if (rs.next()) {
                output = rs.getInt(1);
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return output;
    }

    void incrementWeight(int input) {
        try (Connection con = datasource.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("update links set weight = weight + 1 where id='" +  input + "'");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    void createLink(int id1, int id2) {
        try (Connection con = datasource.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("insert into links (input_id, output_id, weight) values(" + id1 +", " + id2 + ", 0)");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    int createMessageRow(String input) {
        int output = -1;
        try (Connection con = datasource.getConnection()) {
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

    int findBestMatch(String input) {
        int bestMatchId = 0;
        int bestMatchWordAmount = -1;
        try (Connection con = datasource.getConnection()) {
            String[] inputNoStopWords = QuestionLogic.removeStopWords(input);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select m.* from messages m join links l on m.id = l.input_id");
            while (rs.next()) {
                int id = rs.getInt(1);
                String text = rs.getString(2);
                LinkedList<Integer> output = QuestionLogic.findBestMatchLogic(text, id, bestMatchWordAmount, input, inputNoStopWords);
                if (output != null) {
                    if (output.size() == 1) {
                        bestMatchId = output.get(0);
                        break;
                    }
                    else if (output.size() == 2) {
                        bestMatchId = output.get(0);
                        bestMatchWordAmount = output.get(1);
                    }
                }
            }
            rs.close();
            stmt.close();
            con.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return bestMatchId;
    }

    String getOutputFromId(int input) {
        String output = "";
        int bestOutputId = 0;
        int bestOutputWeight = -1;
        try (Connection con = datasource.getConnection()){
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from links where input_id=" + input);
            while (rs.next()){
                int outputId = rs.getInt(3);
                int weight = rs.getInt(4);
                if (weight > bestOutputWeight) {
                    bestOutputId = outputId;
                    bestOutputWeight = weight;
                }
            }
            ResultSet rs2 = stmt.executeQuery("select * from messages where id=" + bestOutputId);
            if (rs2.next()){
                output = rs2.getString(2);
            }
            con.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return output;
    }
}
