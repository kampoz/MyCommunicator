package com.example.kp.mycommunicator;

//Singleton
public class UserInfo {

    protected static String login;

    private static UserInfo instance = null;
    protected UserInfo() {

    }
    public static UserInfo getInstance() {
        if(instance == null) {
            instance = new UserInfo();
        }
        return instance;
    }
}





