package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.data.DataManager;
import com.maxiee.heartbeat.database.utils.EventUtils;
import com.maxiee.heartbeat.database.utils.LabelUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.Label;
import com.maxiee.heartbeat.model.Thoughts;

/**
 * Created by maxiee on 15-7-29.
 */
public class EntryActivity extends Activity {

    public static final String IS_FIRST_USE = "is_first";

    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataManager = DataManager.getInstance(this);
        // 首次使用添加引导教程
        if (mDataManager.isEventEmpty() && isFirstUse()) {
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

    private boolean isFirstUse() {
        SharedPreferences sp = getSharedPreferences("hb", MODE_PRIVATE);
        boolean isFirst = sp.getBoolean(IS_FIRST_USE, true);
        if (isFirst) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(IS_FIRST_USE, false);
            editor.apply();
            return true;
        }
        return false;
    }

    private void addTutorial() {

        Label label = LabelUtils.addLabel(this, getString(R.string.app_name));
        long eventKey;

        // Tut4
        eventKey = EventUtils.addEvent(this, getString(R.string.tut_4_event)).getId();
        LabelUtils.addRelation(this, eventKey, label.getId());
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_4_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_4_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_4_thought_3),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_4_thought_4),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );

        // Tut3
        eventKey = EventUtils.addEvent(this, getString(R.string.tut_3_event)).getId();
        LabelUtils.addRelation(this, eventKey, label.getId());
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_3_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_3_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );

        // Tut2
        eventKey = EventUtils.addEvent(this, getString(R.string.tut_2_event)).getId();
        LabelUtils.addRelation(this, eventKey, label.getId());
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_2_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_2_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_2_thought_3),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );

        // Tut1
        eventKey = EventUtils.addEvent(this, getString(R.string.tut_1_event)).getId();
        LabelUtils.addRelation(this, eventKey, label.getId());
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_1_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_1_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_1_thought_3),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        mDataManager.reload();
        mDataManager.logInfo();
    }
}
