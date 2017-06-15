package com.aquariumpain.dialect.database;

import com.knockturnmc.api.util.sql.SqlDatasource;
import com.aquariumpain.dialect.chatbot.*;
import com.aquariumpain.dialect.web.WebUtils;
import com.aquariumpain.dialect.web.daos.User;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.LinkedList;
import java.util.Random;

import javax.servlet.ServletContext;

import org.json.JSONObject;

//Database Utility Class
public class DatabaseOperations {

    private SqlDatasource datasource = null;

    
    public DatabaseOperations(ServletContext context) {
        InputStream input = context.getResourceAsStream("/WEB-INF/database.properties");
        DatabaseProperties properties = new DatabaseProperties();
        try {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        datasource = new DatabaseConnectionPool(properties);
    }
    //chatbot methods
    public int databaseContains(String input) {
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

    public int isLinked(long id1, long id2) {
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

    public void incrementWeight(long linkId) {
        try (Connection con = datasource.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("update links set weight = weight + 1 where id='" +  linkId + "'");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void createLink(long id1, long id2) {
        try (Connection con = datasource.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("insert into links (input_id, output_id, weight) values(" + id1 +", " + id2 + ", 0)");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public int createMessageRow(String input) {
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

    public long findBestMatch(String input) {
        long bestMatchId = 0;
        long bestMatchWordAmount = -1;
        try (Connection con = datasource.getConnection()) {
            String[] inputNoStopWords = QuestionLogic.removeStopWords(input.toLowerCase());
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select m.* from messages m join links l on m.id = l.input_id");
            while (rs.next()) {
                long id = rs.getInt(1);
                String text = rs.getString(2);
                LinkedList<Long> output = QuestionLogic.findBestMatchLogic(text, id, bestMatchWordAmount, input, inputNoStopWords);
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

    public String getOutputFromId(long input) {
        String output = "";
        if (input == 0) {
    		output = "I don't have any responses in my database. Please train me!";
    	}
        else {
        	LinkedList<String> outputs = new LinkedList<String>();
            try (Connection con = datasource.getConnection()){
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select o.text from links l join messages i on i.id = l.input_id join messages o on o.id = l.output_id where l.input_id=" + input + " and l.weight = (select max(weight) from links where input_id=" + input + ") order by l.weight desc");
                while (rs.next()){
                	String out = rs.getString(1);
                	outputs.add(out);
                }
                con.close();
            }
            catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            output = outputs.get(new Random().nextInt(outputs.size()));
        }
        return output;
    }
    //web methods
    public User getUserByGithubId(String githubId) {
		User user = null;
        try (Connection con = datasource.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("select * from users where github_id=?");
            stmt.setString(1, githubId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	user = new User();
                user.id = rs.getInt(1);
                user.username = rs.getString(2);
                user.email = rs.getString(3);
                user.githubId = rs.getString(4);
                user.token = rs.getString(5);
                user.isTrainer = rs.getBoolean(6);
                user.isAdmin = rs.getBoolean(7);
                user.isBanned = rs.getBoolean(8);
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return user;
	}
    
    public User getUserById(long id) {
		User user = null;
        try (Connection con = datasource.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("select * from users where id=?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	user = new User();
                user.id = rs.getInt(1);
                user.username = rs.getString(2);
                user.email = rs.getString(3);
                user.githubId = rs.getString(4);
                user.token = rs.getString(5);
                user.isTrainer = rs.getBoolean(6);
                user.isAdmin = rs.getBoolean(7);
                user.isBanned = rs.getBoolean(8);
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return user;
	}

	public User createUser(JSONObject userInput) {
		String username = userInput.getString("username");
		String email = userInput.getString("email");
		String githubId = userInput.getString("github_id");
		String token = WebUtils.generateToken();
		try (Connection con = datasource.getConnection()) {
			PreparedStatement stmt = con.prepareStatement("insert into users (username, email, github_id, token, is_trainer, is_admin, is_banned) values(?, ?, ?, ?, ?, ?, ?)");
			stmt.setString(1, username);
			stmt.setString(2, email);
			stmt.setString(3, githubId);
			stmt.setString(4, token);
			ResultSet rs = stmt.executeQuery("select * from users LIMIT 1");
			if (!rs.next()) {
				stmt.setBoolean(5, true);
				stmt.setBoolean(6, true);
			}
			else {
				stmt.setBoolean(5, false);
				stmt.setBoolean(6, false);
			}
			stmt.setBoolean(7, false);
            stmt.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
		return getUserByGithubId(githubId);
	}
	
	public User updateUserDetails(JSONObject userInput) {
		String username = userInput.getString("username");
		String email = userInput.getString("email");
		String githubId = userInput.getString("github_id");
		try (Connection con = datasource.getConnection()) {
			PreparedStatement stmt = con.prepareStatement("update users set username=?, email=? where github_id=?");
			stmt.setString(1, username);
			stmt.setString(2, email);
			stmt.setString(3, githubId);
            stmt.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
		return getUserByGithubId(githubId);
	}
	
	public LinkedList<User> getUsersByUsernameOrEmail(String input) {
		LinkedList<User> users = new LinkedList<User>();
        try (Connection con = datasource.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("select * from users where username=? or email=?");
            stmt.setString(1, input);
            stmt.setString(2, input);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	User user = new User();
                user.id = rs.getInt(1);
                user.username = rs.getString(2);
                user.email = rs.getString(3);
                user.githubId = rs.getString(4);
                user.token = rs.getString(5);
                user.isTrainer = rs.getBoolean(6);
                user.isAdmin = rs.getBoolean(7);
                user.isBanned = rs.getBoolean(8);
                users.add(user);
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return users;
	}
	
	public User updateUserPerms(JSONObject perms) {
		boolean isTrainer = perms.getBoolean("trainer");
		boolean isAdmin = perms.getBoolean("admin");
		boolean isBanned = perms.getBoolean("banned");
		long id = perms.getLong("id");
		try (Connection con = datasource.getConnection()) {
			PreparedStatement stmt = con.prepareStatement("update users set is_trainer=?, is_admin=?, is_banned=? where id=?");
			stmt.setBoolean(1, isTrainer);
			stmt.setBoolean(2, isAdmin);
			stmt.setBoolean(3, isBanned);
			stmt.setLong(4, id);
            stmt.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
		return getUserById(perms.getLong("id"));
	}
	
	public boolean checkToken(String token) {
		boolean isValid = false;
		try (Connection con = datasource.getConnection()) {
			PreparedStatement stmt = con.prepareStatement("select * from users where token=?");
			stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	isValid = true;
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
		return isValid;
	}
	
	public boolean isTokenTrainable(String token) {
		boolean isTrainer = false;
		try (Connection con = datasource.getConnection()) {
			PreparedStatement stmt = con.prepareStatement("select * from users where token=?");
			stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	boolean trainer = rs.getBoolean("is_trainer");
            	if (trainer) {
            		isTrainer = true;
            	}
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
		return isTrainer;
	}
	
	public boolean isTokenAdmin(String token) {
		boolean isAdmin = false;
		try (Connection con = datasource.getConnection()) {
			PreparedStatement stmt = con.prepareStatement("select * from users where token=?");
			stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	boolean admin = rs.getBoolean("is_admin");
            	if (admin) {
            		isAdmin = true;
            	}
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
		return isAdmin;
	}
	
	public boolean isTokenBanned(String token) {
		boolean isBanned = false;
		try (Connection con = datasource.getConnection()) {
			PreparedStatement stmt = con.prepareStatement("select * from users where token=?");
			stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	boolean banned = rs.getBoolean("is_banned");
            	if (banned) {
            		isBanned = true;
            	}
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
		return isBanned;
	}
	
	public String resetToken(String githubId) {
		String token = WebUtils.generateToken();
		try (Connection con = datasource.getConnection()) {
			PreparedStatement stmt = con.prepareStatement("update users set token=? where github_id=?");
			stmt.setString(1, token);
			stmt.setString(2, githubId);
			stmt.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
		return token;
	}
}
