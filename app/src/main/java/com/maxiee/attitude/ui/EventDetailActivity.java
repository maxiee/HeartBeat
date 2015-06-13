package com.maxiee.attitude.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.maxiee.attitude.R;

/**
 * Created by maxiee on 15-6-13.
 */
public class EventDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
    }
}
