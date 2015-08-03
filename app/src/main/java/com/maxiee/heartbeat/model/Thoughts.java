package com.maxiee.heartbeat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by maxiee on 15-6-16.
 */
public class Thoughts {

    public static final String THOUGHT = "thought";
    public static final String TIMESTAMP = "timestamp";

    private ArrayList<Thought> mThoughts;

    public Thoughts() {
        mThoughts = new ArrayList<>();
    }

    public void addThought(int key, String thought) {

        Thought thoughtObject = new Thought(
                key,
                thought,
                System.currentTimeMillis());

        mThoughts.add(thoughtObject);
    }

    public ArrayList<Thought> getmThoughts() {
        return mThoughts;
    }

    public Thought get(int position) {
        return mThoughts.get(position);
    }

    public void add(Thought thought) {
        mThoughts.add(thought);
    }

    public void remove(int position) {
        mThoughts.remove(position);
    }

    public void reverse() {
        Collections.reverse(mThoughts);
    }

    public int length() {
        return mThoughts.size();
    }

    public static class Thought {

        private int key;
        private String mThought;
        private long mTimeStamp;

        public Thought(int key, String mThought, long mTimeStamp) {
            this.key = key;
            this.mThought = mThought;
            this.mTimeStamp = mTimeStamp;
        }

        public int getKey() {
            return key;
        }

        public String getThought() {
            return mThought;
        }

        public long getTimeStamp() {
            return mTimeStamp;
        }

        public void setThought(String mThought) {
            this.mThought = mThought;
        }
    }
}
