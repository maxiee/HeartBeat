package com.maxiee.heartbeat.common.cloudview;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by maxiee on 15-6-24.
 *
 * Modified from: luixal/android-tagcloud
 * https://github.com/luixal/android-tagcloud
 *
 */
public class Cloud {

    public enum Rounding {
        CEIL,
        FLOOR,
        ROUND
    }

    private Map<String, CloudTag> cloud = new HashMap<>();

    private double minWeight = 0.0;
    private double maxWeight = 4.0;

    private int maxTagToDisplay = 50;

    /* tags having score under the threshold are excluded. */
    private double threshold = 0.0;

    /* tags' timestamp older than tagLifetime are excluded. */
    private long tagLifetime = -1;

    private Rounding rounding = Rounding.CEIL;

    public Cloud() {}

    public void addTag(CloudTag tag) {
        if (tag == null) {
            return;
        }

        String key = tag.getName();
        if (cloud.containsKey(key)) {
            tag.addScore(cloud.get(key).getScore());
        }

        cloud.put(key, tag);
    }

    public void addTags(Collection<CloudTag> tags) {
        if (tags == null) {
            return;
        }

        Iterator<CloudTag> it = tags.iterator();
        while (it.hasNext()) {
            addTag(it.next());
        }
    }

    protected List<CloudTag> getOutputTags() {
        List<CloudTag> emptyList = new LinkedList<>();

        if (getCloud() == null) {
            return emptyList;
        }

        List<CloudTag> result = new LinkedList<>();
        Iterator<CloudTag> it = getCloud().values().iterator();
        CloudTag tag;

        double max = 0.0;

        while (it.hasNext()) {
            tag = it.next();

//            if (tag.getScore() < threshold ||
//                tag.getTimestamp() < tagLifetime) {
//                continue;
//            }

            result.add(tag);

            if (tag.getScore() > max) {
                max = tag.getScore();
            }
        }

        return result;
    }

    protected Map<String, CloudTag> getCloud() {
        return cloud;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }
}
