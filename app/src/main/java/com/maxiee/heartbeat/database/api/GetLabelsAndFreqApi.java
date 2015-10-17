package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventLabelRelationTable;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by maxiee on 15-6-18.
 */
public class GetLabelsAndFreqApi extends BaseDBApi {

    public GetLabelsAndFreqApi(Context context) {
        super(context);
    }

    public Map<Integer, Integer> exec() {
        ArrayList<Integer> labelIds = new GetAllLabelApi(mContext).exec();

        if (labelIds == null) {
            return  null;
        }

        Map<Integer, Integer> ret = new TreeMap<>();
        for (int labelId : labelIds) {
            Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                    EventLabelRelationTable.NAME,
                    new String[] {
                            EventLabelRelationTable.LABEL_ID
                    },
                    EventLabelRelationTable.LABEL_ID + "=?",
                    new String[] {String.valueOf(labelId)},
                    null, null, null
            );
            ret.put(labelId,cursor.getCount());
            cursor.close();
        }
//        Log.d("map_put", ret.toString());
        return ret;
    }

}
