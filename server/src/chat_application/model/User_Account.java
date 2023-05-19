/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chat_application.model;

/**
 *
 * @author Mikołaj
 */
public class User_Account {
     public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public User_Account(int userID, String userName, boolean status) {
        this.userID = userID;
        this.userName = userName;
        this.status = status;
    }

    public User_Account() {
    }

    private int userID;
    private String userName;
    private boolean status;
}
