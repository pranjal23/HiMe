package com.prs.kw.httpclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pranjal on 6/6/15.
 */
public class Location {
    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
