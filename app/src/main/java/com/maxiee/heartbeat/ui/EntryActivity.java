package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.api.AddEventApi;
import com.maxiee.heartbeat.database.api.AddEventLabelRelationApi;
import com.maxiee.heartbeat.database.api.AddLabelsApi;
import com.maxiee.heartbeat.database.api.AddThoughtApi;
import com.maxiee.heartbeat.database.api.GetAllEventApi;
import com.maxiee.heartbeat.model.Thoughts;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-29.
 */
public class EntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 首次使用添加引导教程
        boolean newUser = new GetAllEventApi(this).exec().isEmpty();
        if (newUser) {
            Log.d("maxiee", "捕捉到一只新用户!生成引导教程...");
            addTutorial();
        }

        // 手势解锁
        SharedPreferences sp = getSharedPreferences("hb", Context.MODE_PRIVATE);
        String pattern = sp.getString("pattern", "");
        if (pattern.isEmpty()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(this, PatternActivity.class);
            i.putExtra(PatternActivity.ACTION, PatternActivity.VERIFY);
            startActivity(i);
            finish();
        }
    }

    void addTutorial() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add(getString(R.string.app_name));
        int labelKey = new AddLabelsApi(this, labels).exec().get(0);
        int eventKey;

        // Tut4
        eventKey = (int) new AddEventApi(this, getString(R.string.tut_4_event)).exec();
        new AddEventLabelRelationApi(this, eventKey, labelKey).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_4_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_4_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_4_thought_3),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_4_thought_4),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();

        // Tut3
        eventKey = (int) new AddEventApi(this, getString(R.string.tut_3_event)).exec();
        new AddEventLabelRelationApi(this, eventKey, labelKey).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_3_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_3_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();

        // Tut2
        eventKey = (int) new AddEventApi(this, getString(R.string.tut_2_event)).exec();
        new AddEventLabelRelationApi(this, eventKey, labelKey).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_2_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_2_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_2_thought_3),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();

        // Tut1
        eventKey = (int) new AddEventApi(this, getString(R.string.tut_1_event)).exec();
        new AddEventLabelRelationApi(this, eventKey, labelKey).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_1_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_1_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();
        new AddThoughtApi(
                this, eventKey,
                getString(R.string.tut_1_thought_3),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        ).exec();
    }
}
