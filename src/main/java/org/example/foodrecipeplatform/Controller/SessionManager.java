package org.example.foodrecipeplatform.Controller;

public class SessionManager {
    private static String userId;

    public static void setUserId(String uid) {
        userId = uid;
    }

    public static String getUserId() {
        return userId;
    }
}
