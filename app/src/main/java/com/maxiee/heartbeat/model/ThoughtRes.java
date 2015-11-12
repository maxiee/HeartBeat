package com.maxiee.heartbeat.model;

/**
 * Created by maxiee on 15/11/11.
 */
public class ThoughtRes {
    private long id;
    private int resType;
    private String path;
    private long thoughtId;

    public ThoughtRes(long id, int resType, String path, long thoughtId) {
        this.id = id;
        this.resType = resType;
        this.path = path;
        this.thoughtId = thoughtId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getThoughtId() {
        return thoughtId;
    }

    public void setThoughtId(long thoughtId) {
        this.thoughtId = thoughtId;
    }
}
