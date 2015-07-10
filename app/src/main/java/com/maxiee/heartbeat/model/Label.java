package com.maxiee.heartbeat.model;

/**
 * Created by maxiee on 15-7-10.
 */
public class Label {
    private int mKey;
    private String mLabel;

    public Label(int key, String label) {
        this.mKey = key;
        this.mLabel = label;
    }

    public int getKey() {return mKey;}

    public String getLabel() {return mLabel;}
}
