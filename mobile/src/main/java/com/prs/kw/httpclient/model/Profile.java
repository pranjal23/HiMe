package com.prs.kw.httpclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pranjal on 6/6/15.
 */
public class Profile {

    @SerializedName("id")
    String id;

    @SerializedName("birthday")
    String birthday;

    @SerializedName("first_name")
    String first_name;

    @SerializedName("gender")
    String gender;

    @SerializedName("last_name")
    String last_name;

    @SerializedName("link")
    String link;

    @SerializedName("location")
    Location location;

    @SerializedName("cover")
    Cover cover;

    @SerializedName("name")
    String name;

    @SerializedName("timezone")
    int timezone;

    @SerializedName("verified")
    boolean verified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
