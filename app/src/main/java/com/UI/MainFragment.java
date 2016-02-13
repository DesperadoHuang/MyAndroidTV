package com.UI;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.util.DisplayMetrics;

import tools.MyTools;

/**
 * Created by WilsonHuang on 2016/2/13.
 */
public class MainFragment extends BrowseFragment {
    private BackgroundManager mBackgroundManager;//背景管理者
    private DisplayMetrics mDisplayMetrics;
    private Drawable mDefaultBackgroundDrawable;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        MyTools.myLog("BrowseFragment : onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();


    }

    private void setupUIElements() {
        setTitle("My Android TV");

        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        setBrandColor(getResources().getColor(R.color.fastlane_background));
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    /**
     * 準備BackgroundManager
     */
    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackgroundDrawable = getResources().getDrawable(R.drawable.default_background);

        mDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
