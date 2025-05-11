package org.example.foodrecipeplatform.Controller;

/**
 * SessionManager class -> use set/get methods for user id
 */
public class SessionManager {

    private static String userId;

    public static void setUserId(String uid) {
        userId = uid;
    }

    public static String getUserId() {
        return userId;
    }

} // End SessionManager class
