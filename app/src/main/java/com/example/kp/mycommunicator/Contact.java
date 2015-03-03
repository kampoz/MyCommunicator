package com.example.kp.mycommunicator;

public class Contact {
    public Boolean online;
    public String name = "hkjhkj ";
    public Boolean areThereMessages;

    public Contact(){
        this.online = false;
        this.name = "Default";
        this.areThereMessages = false;
    }

    public Contact(Boolean online, String name, Boolean areThereMessages) {
        super();
        this.online = online;
        this.name = name;
        this.areThereMessages = areThereMessages;
    }


    @Override
    public String toString() {
        return ("["+online.toString()+" "+name+" "+areThereMessages.toString()+"}");
    }
}
