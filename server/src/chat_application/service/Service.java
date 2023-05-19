/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chat_application.service;


import chat_application.model.Client;
import chat_application.model.Login;
import chat_application.model.Message_Errors;
import chat_application.model.Recive_Message;
import chat_application.model.Register;
import chat_application.model.Send_Message;
import chat_application.model.User_Account;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 * @author Mikołaj
 */
public class Service {
    private static Logger logger = LoggerFactory.getLogger(Service.class);
    private static Service instance;
    private SocketIOServer server;
    private ServiceUser serviceUser;
    private List<Client> listClient;
    private JTextArea textArea;
    private final int PORT_NUMBER = 9999;

   
    public static Service getInstance(JTextArea textArea) {
        if (instance == null) {
            instance = new Service(textArea);
        }
        return instance;
    }

    private Service(JTextArea textArea) {
        this.textArea = textArea;
        serviceUser = new ServiceUser();
        listClient = new ArrayList<>();
    }

    public void startServer() {
        Configuration config = new Configuration();
        config.setPort(PORT_NUMBER);
        server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient sioc) {
                
                logger.info("client connected");
            }
        });
        server.addEventListener("register", Register.class, new DataListener<Register>() {
            @Override
            public void onData(SocketIOClient sioc, Register t, AckRequest ar) throws Exception {
                Message_Errors message = serviceUser.register(t);
                ar.sendAckData(message.isAction(), message.getMessage(), message.getData());
                if (message.isAction()) {
                 
                    logger.info("User has Register");
                    server.getBroadcastOperations().sendEvent("list_user", (User_Account) message.getData());
                    addClient(sioc, (User_Account) message.getData());
                }
            }
        });
        server.addEventListener("login", Login.class, new DataListener<Login>() {
            @Override
            public void onData(SocketIOClient sioc, Login t, AckRequest ar) throws Exception {
                User_Account login = serviceUser.login(t);
                if (login != null) {
                    ar.sendAckData(true, login);
                    addClient(sioc, login);
                    userConnect(login.getUserID());
                } else {
                    ar.sendAckData(false);
                }
            }
        });
        server.addEventListener("list_user", Integer.class, new DataListener<Integer>() {
            @Override
            public void onData(SocketIOClient sioc, Integer userID, AckRequest ar) throws Exception {
                try {
                    List<User_Account> list = serviceUser.getUser(userID);
                    sioc.sendEvent("list_user", list.toArray());
                } catch (SQLException e) {
                   logger.error("error");
                }
            }
        });
         server.addEventListener("send_to_user", Send_Message.class, new DataListener<Send_Message>() {
            @Override
            public void onData(SocketIOClient sioc, Send_Message t, AckRequest ar) throws Exception {
                sendToClient(t);
            }
        });
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient sioc) {
                int userID = removeClient(sioc);
                if (userID != 0) {
                    //  removed
                    userDisconnect(userID);
                }
            }
        });
        server.start();
        logger.info("Server has Start on port : " + PORT_NUMBER );
    }

    private void userConnect(int userID) {
        server.getBroadcastOperations().sendEvent("user_status", userID, true);
    }

    private void userDisconnect(int userID) {
        server.getBroadcastOperations().sendEvent("user_status", userID, false);
    }

    private void addClient(SocketIOClient client, User_Account user) {
        listClient.add(new Client(client, user));
    }
    private void sendToClient(Send_Message data) {
        for (Client c : listClient) {
            if (c.getUser().getUserID() == data.getToUserID()) {
                c.getClient().sendEvent("receive_ms", new Recive_Message(data.getFromUserID(), data.getText()));
                break;
            }
        }
    }
    public int removeClient(SocketIOClient client) {
        for (Client d : listClient) {
            if (d.getClient() == client) {
                listClient.remove(d);
                return d.getUser().getUserID();
            }
        }
        return 0;
    }

    public List<Client> getListClient() {
        return listClient;
    }
}