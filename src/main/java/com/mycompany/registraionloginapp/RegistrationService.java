/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registraionloginapp;

/**
 *
 * @author muano
 */
public class RegistrationService {

    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[!@#$%^&*()_+=<>?/{}~`|\\[\\]-].*");
    }

    public boolean checkCellPhoneNumber(String number) {
        // Based on GPT 4.0 suggested pattern for SA numbers
        return number.matches("^\\+27[-\\s]?[6-8][0-9]{1}[-\\s]?[0-9]{3}[-\\s]?[0-9]{4}$");
    }

    public String registerUser(String username, String password) {
        // Initialize flags
        boolean isUsernameValid = checkUserName(username);
        boolean isPasswordValid = checkPasswordComplexity(password);

        StringBuilder result = new StringBuilder();  // To build the result message

        // Check if username is valid
        if (!isUsernameValid) {
            result.append("The username is incorrectly formatted.\n");
        }

        // Check if password meets complexity requirements
        if (!isPasswordValid) {
            result.append("The password does not meet the complexity requirements.\n");
        }

        // If both validations are successful, append the success message
        if (isUsernameValid && isPasswordValid) {
            result.append("The two above conditions have been met, and the user has been registered successfully!");
        }

        // Return the final message
        return result.toString();
    }

}
