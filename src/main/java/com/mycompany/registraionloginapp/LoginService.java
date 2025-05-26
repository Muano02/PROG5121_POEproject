/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registraionloginapp;

/**
 *
 * @author muano
 */
public class LoginService {

    private User registeredUser;

    // Setter to store the user after registration
    public void setUser(User user) {
        this.registeredUser = user;
    }

    public boolean logUser(String username, String password) {
        return registeredUser != null
                && registeredUser.getUsername().equals(username)
                && registeredUser.getPassword().equals(password);
    }

    public String returnLoginStatus(boolean loginSuccess) {
        if (loginSuccess) {
            return "\n Welcome " + registeredUser.getFirstName() + " " + registeredUser.getLastName() + ", it is great to see you again";
        } else {
            return "Username or password are incorrect, please try again.";
        }
    }

}
