package org.example.foodrecipeplatform.Controller;

public class SessionManager {

    private static String userId;

    private static Object displayName;

    public static void setUserId(String uid) {
        userId = uid;
    }

    public static String getUserId() {
        return userId;
    }

    public static Object getUserDisplayName() { return displayName; }
}
