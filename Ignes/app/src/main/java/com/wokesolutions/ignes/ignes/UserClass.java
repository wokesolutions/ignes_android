package com.wokesolutions.ignes.ignes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserClass {

   // private String role;
    private String username;

    private List<String> upReports;
    private List<String> downReports;
    private List<String> spamReports;

    public UserClass(String username) {

        // this.role = role;
        this.username = username;

        upReports = new LinkedList<>();
        downReports = new LinkedList<>();
        spamReports = new LinkedList<>();

    }


    public List<String> getSpamReports() {
        return spamReports;
    }

    public void addUpVote(String id) {
        upReports.add(id);
    }

    public void addDownVote(String id) {
        downReports.add(id);
    }

    public void addSpam(String id) {
        spamReports.add(id);
    }

    public List<String> getDownReports() {
        return downReports;
    }

    public List<String> getUpReports() {
        return upReports;
    }

    public String getUsername() {
        return username;
    }

   /* public String getRole() {
        return role;
    }*/
}
