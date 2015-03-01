package com.example.kp.mycommunicator;

public class Contact {
    public boolean online;
    public String name;
    public boolean areThereMessages;

    public Contact(){
    }

    public Contact(boolean online, String name, boolean areThereMessages) {
        super();
        this.online = online;
        this.name = name;
        this.areThereMessages = areThereMessages;
    }
}
