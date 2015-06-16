package com.maxiee.attitude.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by maxiee on 15-6-16.
 */
public class Thoughts {

    public static final String THOUGHT = "thought";
    public static final String TIMESTAMP = "timestamp";

    private JSONArray mThoughts;

    public Thoughts() {
        mThoughts = new JSONArray();
    }

    public Thoughts(JSONArray thoughts) {
        mThoughts = thoughts;
    }

    public void addThought(String thought) throws JSONException{
        JSONObject thoughtObject = new JSONObject();
        thoughtObject.put(THOUGHT, thought);
        thoughtObject.put(TIMESTAMP, System.currentTimeMillis());
        mThoughts.put(thoughtObject);
    }

    public JSONArray getmThoughts() {
        return mThoughts;
    }

    public JSONObject get(int position) throws JSONException{
        return (JSONObject) mThoughts.get(position);
    }

    public String getThoughtAt(int position) throws JSONException{
        return get(position).getString(THOUGHT);
    }

    public String toString() {
        return mThoughts.toString();
    }

    public long getTimestampAt(int position) throws JSONException{
        return get(position).getLong(TIMESTAMP);
    }

    public int length() {
        return mThoughts.length();
    }
}
