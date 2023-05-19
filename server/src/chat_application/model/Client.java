/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chat_application.model;
import com.corundumstudio.socketio.SocketIOClient;
/**
 *
 * @author Mikołaj
 */
public class Client {
     public SocketIOClient getClient() {
        return client;
    }

    public void setClient(SocketIOClient client) {
        this.client = client;
    }

    public User_Account getUser() {
        return user;
    }

    public void setUser(User_Account user) {
        this.user = user;
    }

    public Client(SocketIOClient client, User_Account user) {
        this.client = client;
        this.user = user;
    }

    public Client() {
    }

    private SocketIOClient client;
    private User_Account user;
}
