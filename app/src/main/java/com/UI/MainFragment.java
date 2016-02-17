package com.UI;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.util.DisplayMetrics;

import com.presenter.CardPresenter;
import com.presenter.GridItemPresenter;

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

        initRow();
    }

    private void initRow() {
        List<Channel> list = ChannelList.setupChannels();
        mRowArrayObjectAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        CardPresenter cardPresenter = new CardPresenter();
        int i;
        for (i = 0; i < NUM_ROWS; i++) {

            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (int j = 0; j < list.size(); j++) {
                listRowAdapter.add(list.get(j));
            }

            HeaderItem headerItem = new HeaderItem(i, ChannelList.CHANNEL_CATEGORY[i]);

            mRowArrayObjectAdapter.add(new ListRow(headerItem, listRowAdapter));
        }

        GridItemPresenter gridItemPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridArrayAdapter = new ArrayObjectAdapter(gridItemPresenter);
        gridArrayAdapter.add("Grid View");
        gridArrayAdapter.add("Error Fragment");
        gridArrayAdapter.add("個人設定");
        HeaderItem gridHeaderItem = new HeaderItem(i, "喜好設定");
        mRowArrayObjectAdapter.add(new ListRow(gridHeaderItem, gridArrayAdapter));

        setAdapter(mRowArrayObjectAdapter);
    }

    private void setupUIElements() {
        //設定主畫面標題或圖片
        // setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.videos_by_google_banner));
        setTitle("My Android TV");

        //設定主選單的顯示模式
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(false);

        //設定主選單背景顏色
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        //設定搜尋按鈕的顏色
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
