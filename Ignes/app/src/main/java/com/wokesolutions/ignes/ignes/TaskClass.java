package com.wokesolutions.ignes.ignes;

import java.util.LinkedList;
import java.util.List;

public class TaskClass extends MarkerClass {

    private List<NoteClass> notes;
    private String indications;
    private String contacts;

    public TaskClass(double lat, double lng, String status, String address,
                     String date, String username, String description, String gravity,
                     String title, String likes, String dislikes, String locality,
                     String marker_id, String indications, String contacts) {
        super(lat, lng, status, address, date, username, description, gravity, title, likes, dislikes, locality, marker_id);

        notes = new LinkedList<>();
        this.indications =indications;
        this.contacts =contacts;
    }

    public String getIndications() {
        return indications;
    }

    public String getContacts() {
        return contacts;
    }

    public List<NoteClass> getNotes() {
        return notes;
    }
}
