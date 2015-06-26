package com.maxiee.attitude.common.cloudview;

/**
 * Created by maxiee on 15-6-24.
 */
public class CloudTag {

    private String name;
    private double score = 1.0;
    private double normScore = 0.0;
    private double weight = 10.0;
    private long timestamp;

    public CloudTag() {}

    public CloudTag(String name) {
        this.name = name;
    }

    public CloudTag(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public int getWeightInt() {
        return (int) Math.ceil(score * weight);
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void addScore(double val) {
        score += val;
    }
}
