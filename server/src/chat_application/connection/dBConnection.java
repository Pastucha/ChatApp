/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chat_application.connection;


import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Mikołaj
 */
public class dBConnection {
    private static dBConnection instance;
    private Connection connection;
    public static dBConnection getInstance() {
        if (instance == null) {
            instance = new dBConnection();
        }
        return instance;
    }
      private dBConnection() {

    }
      
    public void connectToDatabase() throws SQLException {
       String server = "localhost";
        String port = "3305";
        String database = "chat_application";
        String userName = "pasti";
        String password = "1234";
        connection = java.sql.DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + database, userName, password);
       
    }
     public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
