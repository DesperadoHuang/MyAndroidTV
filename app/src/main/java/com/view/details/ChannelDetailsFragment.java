package com.view.details;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.util.DisplayMetrics;

import com.model.Channel;

import tools.MyTools;

/**
 *
 */
public class ChannelDetailsFragment extends DetailsFragment {
    private static final int DETAILS_THUMB_WIDTH = 274;//詳細訊息的寬
    private static final int DETAILS_THUMB_HEIGHT = 274;//詳細訊息的高

    private Channel mSelectedChannel;

    private ArrayObjectAdapter mArrayObjectAdapter;
    private ClassPresenterSelector mClassPresenterSelector;

    private BackgroundManager mbBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mdDisplayMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MyTools.myLog("ChannelDetailsFragment : onCreate ");
        super.onCreate(savedInstanceState);

    }
}
