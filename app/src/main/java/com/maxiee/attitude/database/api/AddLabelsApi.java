package com.maxiee.attitude.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.attitude.database.tables.LabelsTable;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-16.
 */
public class AddLabelsApi extends BaseDBApi{

    private ArrayList<String> mLabels;

    public AddLabelsApi(Context context,
                        ArrayList<String> labels) {
        super(context);
        mLabels = labels;
    }

    public int insertLabel(String label) {
        ContentValues values = new ContentValues();
        values.put(LabelsTable.LABEL, label);
        add(LabelsTable.NAME, values);

        return new HasLabelApi(mContext, label).exec();
    }

    public ArrayList<Integer> exec() {
        ArrayList<Integer> ret = new ArrayList<>();
        for (String label: mLabels) {
            int id = new HasLabelApi(mContext, label).exec();
            if (id == HasLabelApi.NOT_FOUND) { // insert into DB
                int insertedId = insertLabel(label);
                if (insertedId != HasLabelApi.NOT_FOUND) {
                    ret.add(insertedId);
                }
            } else {
                ret.add(id);
            }
        }
        return ret;
    }
}
