package org.example.foodrecipeplatform.Controller;

/**
 * SessionManager class -> use set/get methods for user id
 */
public class SessionManager {

    private static String userId;

    private static String userDisplayName;

    public static void setUserId(String uid) {
        userId = uid;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getUserDisplayName() {
        return userDisplayName;
    }
    public static void setUserDisplayName(String displayName) {
        userDisplayName = displayName;
    }

} // End SessionManager class
