package com.maxiee.heartbeat.provider;

/**
 * Created by maxiee on 16/1/10.
 */
public class Constant {
    public static final String BASE_URI = "com.maxiee.heartbeat.provider";
    public static final String CATEGORY_EVNET = "event/";
    public static final String CATEGORY_THOUGHT = "thought/";
    public static final String CATEGORY_THOUGHT_RES = "thought_res/";

    public static final String API_EVENT_RANDOM = CATEGORY_EVNET + "random";
    public static final String API_EVENT_ID     = CATEGORY_EVNET + "id/#";
    public static final String API_THOUGHT_EVENT_ID = CATEGORY_THOUGHT + "event_id/#";
    public static final String API_THOUGHT_RES_THOUGHT_ID = CATEGORY_THOUGHT_RES + "thought_id/#";

    public static final int API_EVENT_RANDOM_CODE = 0;
    public static final int API_EVENT_ID_CODE = 1;
    public static final int API_THOUGHT_EVENT_ID_CODE = 2;
    public static final int API_THOUGHT_RES_THOUGHT_ID_CODE = 3;

}
