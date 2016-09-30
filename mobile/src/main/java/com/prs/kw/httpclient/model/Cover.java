package com.prs.kw.httpclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pranjal on 19/6/15.
 */
public class Cover {
    @SerializedName("source")
    String source;

    @SerializedName("id")
    String id;

    @SerializedName("offset_y")
    int offset_y;


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOffset_y() {
        return offset_y;
    }

    public void setOffset_y(int offset_y) {
        this.offset_y = offset_y;
    }
}
