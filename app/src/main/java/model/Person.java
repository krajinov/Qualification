package model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Person {

    private String id;
    private String first_name;
    private String last_name;
    private String email;
    private String city;
    private String country;
    private long date;
    private Map<String, Float> map = new HashMap<>();
    private ArrayList<String> skills = new ArrayList<>();
    private String avatar;
    private Map<String, String> job = new HashMap<>();
    private String description;


    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getLast_name() {

        return last_name;
    }

    public void setLast_name(String last_name) {

        this.last_name = last_name;
    }

    public String getFirst_name() {

        return first_name;
    }

    public void setFirst_name(String first_name) {

        this.first_name = first_name;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getCity() {

        return city;
    }

    public void setCity(String city) {

        this.city = city;
    }

    public String getCountry() {

        return country;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Map<String, Float> getMap() {

        return map;
    }

    public void setMap(Map<String, Float> map) {

        this.map = map;
    }

    public ArrayList<String> getSkills() {

        return skills;
    }

    public void setSkills(ArrayList<String> skills) {

        this.skills = skills;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Map<String, String> getJob() {
        return job;
    }

    public void setJob(Map<String, String> job) {
        this.job = job;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
