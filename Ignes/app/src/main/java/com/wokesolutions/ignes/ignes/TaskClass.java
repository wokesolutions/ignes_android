package com.wokesolutions.ignes.ignes;

import java.util.LinkedList;
import java.util.List;

public class TaskClass extends MarkerClass {

    private List<NoteClass> notes;
    private String indications;
    private String phoneNumber;

    public TaskClass(double lat, double lng, String status, String address,
                     String date, String username, String description, String gravity,
                     String title, String likes, String dislikes, String locality,
                     String marker_id, String indications, String category, String phoneNumber, boolean isPrivate) {
        super(lat, lng, status, address, date, username, description, gravity, title, likes, dislikes,
                locality, false,false,"", category, marker_id, isPrivate);

        notes = new LinkedList<>();
        this.indications = indications;
        this.phoneNumber = phoneNumber;
    }

    public String getIndications() {
        return indications;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<NoteClass> getNotes() {
        return notes;
    }
}
