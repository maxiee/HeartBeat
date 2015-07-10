package com.maxiee.heartbeat.model;

/**
 * Created by maxiee on 15-7-11.
 */
public class Crash {
    public static final String LOG = "log";
    public static final String TS = "ts";

    public String log;
    public Long timeStamp;

    public Crash(String log, Long timeStamp) {
        this.log = log;
        this.timeStamp = timeStamp;
    }
}
