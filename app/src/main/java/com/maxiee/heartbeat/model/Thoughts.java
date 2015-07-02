package com.maxiee.heartbeat.model;

import java.util.ArrayList;

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

    public void addThought(String thought) {

        Thought thoughtObject = new Thought(
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


    public int length() {
        return mThoughts.size();
    }

    public static class Thought {
        private String mThought;
        private long mTimeStamp;

        public Thought(String mThought, long mTimeStamp) {
            this.mThought = mThought;
            this.mTimeStamp = mTimeStamp;
        }

        public String getThought() {
            return mThought;
        }

        public long getTimeStamp() {
            return mTimeStamp;
        }
    }
}
