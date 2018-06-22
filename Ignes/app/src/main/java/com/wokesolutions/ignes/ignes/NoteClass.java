package com.wokesolutions.ignes.ignes;

public class NoteClass {

    private String user, text, date;

    public NoteClass(String user, String text, String date){
        this.user=user;
        this.text =text;
        this.date =date;
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }
}
