package com.UI;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.util.DisplayMetrics;

import com.presenter.CardPresenter;

import java.util.List;

import tools.MyTools;

/**
 * Created by WilsonHuang on 2016/2/13.
 */
public class MainFragment extends BrowseFragment {
    /**
     * 背景相關變數
     */
    private BackgroundManager mBackgroundManager;//背景管理者
    private DisplayMetrics mDisplayMetrics;//螢幕的尺寸資訊
    private Drawable mDefaultBackgroundDrawable;//預設的背景圖片

    private ArrayObjectAdapter mRowArrayObjectAdapter;
    private static final int NUM_ROWS = 5;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        MyTools.myLog("BrowseFragment : onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        List<Channel> list = ChannelList.setupChannels();
        mRowArrayObjectAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        int i;
        for (i = 0; i < NUM_ROWS; i++) {
        }

    }

    private void setupUIElements() {
        setTitle("My Android TV");

        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        setBrandColor(getResources().getColor(R.color.fastlane_background));
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    /**
     * 準備BackgroundManager(背景相關)
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
