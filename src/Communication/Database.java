package Communication;

import java.sql.*;

/**
 * @author MSmarsch
 */
public class Database {

    static final String URL = "jdbc:mysql://199.98.20.122:3306/SetGame";
    static final String DRIVER = "com.mysql.jdbc.Driver";
    static final String Username = "msmarsch";
    static final String Password = "software";

    public void createTable()
    {
        Connection conn;
        Statement stmt;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to the Game's Database...");
            conn = DriverManager.getConnection(URL, Username, Password);
            System.out.println("Connected Successfully!");
            System.out.println("Creating Player Table...");
            stmt = conn.createStatement();
            String command = "CREATE TABLE PLAYERS " + "(id INT NOT NULL, "
                    + " Username VARCHAR(40) UNIQUE NOT NULL, "
                    + " Password VARCHAR(40) NOT NULL, " + " PRIMARY KEY ( id ))";
            stmt.executeUpdate(command);
            System.out.println("Table created!");
            conn.close();
        }
        catch(SQLException | ClassNotFoundException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void deleteTable()
    {
        Connection conn;
        Statement stmt;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to the Game's Database...");
            conn = DriverManager.getConnection(URL, Username, Password);
            System.out.println("Connected Successfully!");
            System.out.println("Deleting Table...");
            stmt = conn.createStatement();
            String command = "DROP TABLE PLAYERS";
            stmt.executeUpdate(command);
            System.out.println("Table deleted!");
            conn.close();
        }
        catch(SQLException | ClassNotFoundException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public boolean addPlayer(String username, String password)
    {
        Connection conn;
        PreparedStatement stmt;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to the Game's Database...");
            conn = DriverManager.getConnection(URL, Username, Password);
            System.out.println("Connected Successfully!");
            System.out.println("Adding Player...");
            String command = "INSERT INTO PLAYERS(Username,Password) Values(?,?)";
            stmt = conn.prepareStatement(command);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.execute();
            conn.close();
        }
        catch(SQLException | ClassNotFoundException e)
        {
            System.err.println(e);
            return false;
        }
        return true;
    }

    public Client getPlayer(String username)
    {
        Connection conn;
        PreparedStatement stmt;
        Client player;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to the Game's Database...");
            conn = DriverManager.getConnection(URL, Username, Password);
            System.out.println("Connected Successfully!");
            System.out.println("Retrieving Player Info...");
            String command = "SELECT * FROM PLAYERS WHERE Username=?";
            stmt = conn.prepareStatement(command);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                player = new Client(rs.getInt("id"), rs.getString("Username"), rs.getString("Password"), -1);
            }
            else
            {
                player = new Client();
            }
            conn.close();
        }
        catch(SQLException | ClassNotFoundException e)
        {
            System.err.println(e.getMessage());
            player = new Client();
        }
        return player;
    }

    public boolean updatePlayer(Client player)
    {
        Connection conn;
        PreparedStatement stmt;
        boolean result;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to the Game's Database...");
            conn = DriverManager.getConnection(URL, Username, Password);
            System.out.println("Connected Successfully!");
            System.out.println("Updating Player Info...");
            String command = "UPDATE PLAYERS SET id=?,Username=?,Password=? WHERE id=?";
            stmt = conn.prepareStatement(command);
            stmt.setInt(1, player.userID);
            stmt.setString(2, player.username);
            stmt.setString(3, player.password);
            stmt.setInt(4, player.userID);
            result = stmt.execute();
            conn.close();
        }
        catch(SQLException | ClassNotFoundException e)
        {
            System.err.println(e.getMessage());
            result = false;
        }
        return result;
    }
}
