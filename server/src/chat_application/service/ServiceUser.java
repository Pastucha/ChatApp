/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chat_application.service;

import chat_application.connection.dBConnection;
import chat_application.model.Client;
import chat_application.model.Login;

import chat_application.model.Message_Errors;
import chat_application.model.Register;
import chat_application.model.User_Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ServiceUser {

    public ServiceUser() {
        this.con = dBConnection.getInstance().getConnection();
    }

    public Message_Errors register(Register data) {
        //  Check user exit
        Message_Errors message = new Message_Errors();
        try {
            PreparedStatement p = con.prepareStatement(CHECK_USER);
            p.setString(1, data.getUserName());
            ResultSet r = p.executeQuery();
            if (r.first()) {
                message.setAction(false);
                message.setMessage("User Already Exit");
            } else {
                message.setAction(true);
            }
            r.close();
            p.close();
            if (message.isAction()) {
                //  Insert User Register
                con.setAutoCommit(false);
                p = con.prepareStatement(INSERT_USER,PreparedStatement.RETURN_GENERATED_KEYS);
                p.setString(1, data.getUserName());
                p.setString(2, data.getPassword());
                p.execute();
                r=p.getGeneratedKeys();
                r.first();
                int userID=r.getInt(1);
                r.close();
                p.close();
                // Create user account
                p=con.prepareStatement(INSERT_USER_ACCOUNT);
                p.setInt(1, userID);
                p.setString(2,data.getUserName());
                p.execute();
                p.close();
                con.commit();
                 con.setAutoCommit(true);
                message.setAction(true);
                message.setMessage("Ok");
                message.setData(new User_Account(userID,data.getUserName(),true));
            }
        } catch (SQLException e) {
            message.setAction(false);
            message.setMessage("Server Error");
             try {
                if (con.getAutoCommit() == false) {
                    con.rollback();
                    con.setAutoCommit(true);
                }
            } catch (SQLException e1) {
                
            }
        }
        return message;
    }
    
   public User_Account login(Login login) throws SQLException {
        User_Account data = null;
        PreparedStatement p = con.prepareStatement(LOGIN);
        p.setString(1, login.getUserName());
        p.setString(2, login.getPassword());
        ResultSet r = p.executeQuery();
        if (r.first()) {
            int userID = r.getInt(1);
            String userName = r.getString(2);
            data = new User_Account(userID, userName, true);
        }
        r.close();
        p.close();
        return data;
    }

    public List<User_Account> getUser(int exitUser) throws SQLException {
        List<User_Account> list = new ArrayList<>();
        PreparedStatement p = con.prepareStatement(SELECT_USER_ACCOUNT);
        p.setInt(1, exitUser);
        ResultSet r = p.executeQuery();
        while (r.next()) {
            int userID = r.getInt(1);
            String userName = r.getString(2);
            list.add(new User_Account(userID, userName, checkUserStatus(userID)));
        }
        r.close();
        p.close();
        return list;
    }

    private boolean checkUserStatus(int userID) {
        List<Client> clients = Service.getInstance(null).getListClient();
        for (Client c : clients) {
            if (c.getUser().getUserID() == userID) {
                return true;
            }
        }
        return false;
    }
    //  SQL
 
    private final String LOGIN = "select UserID, user_account.UserName from `user` join user_account using (UserID) where `user`.UserName=BINARY(?) and `user`.`Password`=BINARY(?) and user_account.`Status`='1'";
    private final String SELECT_USER_ACCOUNT = "select UserID, UserName from user_account where user_account.Status='1' and UserID<>?";
    private final String INSERT_USER = "insert into user (UserName, `Password`) values (?,?)";
    private final String INSERT_USER_ACCOUNT = "insert into user_account (UserID, UserName) values (?,?)";
    private final String CHECK_USER = "select UserID from user where UserName =? limit 1";
    //  Instance
    private final Connection con;
}

