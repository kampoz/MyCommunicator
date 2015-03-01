package com.example.kp.mycommunicator;

public class Contact {
    public boolean online;
    public String contact;
    public boolean areThereMessages;

    public Contact(){
    }

    public Contact(boolean online, String contact, boolean areThereMessages) {
        super();
        this.online = online;
        this.contact = contact;
        this.areThereMessages = areThereMessages;
    }
}
