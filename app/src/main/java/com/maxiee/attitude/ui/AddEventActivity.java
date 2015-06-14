package com.maxiee.attitude.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maxiee.attitude.R;
import com.maxiee.attitude.database.api.AddEventApi;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-11.
 */
public class AddEventActivity extends AppCompatActivity{
    private EditText mEditEvent;
    private EditText mEditFirstThought;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditEvent = (EditText) findViewById(R.id.edit_event);
        mEditFirstThought = (EditText) findViewById(R.id.first_thought);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String event = mEditEvent.getText().toString();
                String thought = mEditFirstThought.getText().toString();
                ArrayList<String> labels = new ArrayList<String>();

                if (event.isEmpty() || thought.isEmpty()) {
                    Toast.makeText(AddEventActivity.this,
                            getString(R.string.notempty),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                labels.add("label1");
                labels.add("label2");
                labels.add("label3");

                try {
                    new AddEventApi(
                            AddEventActivity.this,
                            event,
                            thought,
                            labels).exec();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
