package com.maxiee.attitude.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxiee.attitude.R;
import com.maxiee.attitude.common.cloudview.CloudView;
import com.maxiee.attitude.database.api.GetLabelsAndFreqApi;
import com.maxiee.attitude.database.api.GetOneLabelApi;
import com.maxiee.attitude.ui.LabelDetailActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by maxiee on 15-6-24.
 */
public class LabelCloudFragment extends Fragment{

    private CloudView mCloudView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_label_cloud, container, false);

        Map<Integer,Integer> allLabels = new GetLabelsAndFreqApi(getActivity()).exec();
        List<Pair<String, Integer>> labels = new LinkedList<>();

        if (allLabels != null) {
            ArrayList<Map.Entry<Integer, Integer>> list = new ArrayList<>(allLabels.entrySet());
            for (Map.Entry<Integer, Integer> label: list) {
                labels.add(new Pair<String, Integer>(
                        new GetOneLabelApi(getActivity(), label.getKey()).exec(),
                        label.getValue()));
            }
        }

        mCloudView = (CloudView) v.findViewById(R.id.cloud_view);
        mCloudView.addLabels(labels);
        mCloudView.setOnLabelClickListener(new CloudView.OnLabelClickListener() {
            @Override
            public void onClick(String label) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.setClass(getActivity(), LabelDetailActivity.class);
                i.putExtra("tag_text", label);
                startActivity(i);
            }
        });
        return v;
    }
}
