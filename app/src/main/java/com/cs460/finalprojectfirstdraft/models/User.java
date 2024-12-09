package com.cs460.finalprojectfirstdraft.models;

/**
 * This class represents a user in our code.
 */
public class User {
    private String firstName, lastName, email, password;

    /**
     * Constructor assigning all used fields
     * @param firstName String users first name
     * @param lastName String users last name
     * @param email String users email address
     * @param password String users password
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * Get users email address
     * @return String users email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get users password
     * @return String users password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get users first name
     * @return String users first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get users last name
     * @return String users last name
     */
    public String getLastName() {
        return lastName;
    }
}
