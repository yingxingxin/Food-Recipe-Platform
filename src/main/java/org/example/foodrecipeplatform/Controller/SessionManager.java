package org.example.foodrecipeplatform.Controller;

/**
 * SessionManager class -> use set/get methods for user id
 */
public class SessionManager {

    private static String userId;

    private static Object displayName;

    public static void setUserId(String uid) {
        userId = uid;
    }

    public static String getUserId() {
        return userId;
    }

    public static Object getUserDisplayName() { return displayName;}

    public static void setUserDisplayName(String display) {displayName = display;
    }
} // End SessionManager class
