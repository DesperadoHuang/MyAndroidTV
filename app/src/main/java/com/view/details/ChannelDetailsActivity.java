package com.view.details;

import android.app.Activity;
import android.os.Bundle;

import com.UI.R;

public class ChannelDetailsActivity extends Activity {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String CHANNEL = "Channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }
}
