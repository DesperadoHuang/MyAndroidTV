package com.view.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.UI.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.model.Channel;
import com.model.ChannelList;
import com.presenter.CardPresenter;
import com.presenter.GridItemPresenter;
import com.view.details.ChannelDetailsActivity;

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tools.MyTools;

/**
 * Created by WilsonHuang on 2016/2/13.
 */
public class MainFragment extends BrowseFragment {
    /**
     * 背景相關變數
     */
    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private BackgroundManager mBackgroundManager;//背景管理者
    private DisplayMetrics mDisplayMetrics;//螢幕的尺寸資訊
    private Drawable mDefaultBackgroundDrawable;//預設的背景圖片
    private Timer mBackgroundTimer;//更新背景的計時器
    private URI mBackgroundURI;//背景圖片的來源
    private final Handler mHandler = new Handler();

    /**
     * 選單選項相關變數
     */
    private ArrayObjectAdapter mRowArrayObjectAdapter;//主選單的adapter
    private static final int CHANNEL_TYPE_ROWS = 5;//主選單項目數量


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        MyTools.myLog("BrowseFragment : onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        initRow();

        setupEventListener();


    }


    /**
     * 準備BackgroundManager(背景相關)
     * <p/>
     * BackgroundManager是用來管理主畫面背景的類別
     */
    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackgroundDrawable = getResources().getDrawable(R.drawable.default_background);

        mDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    }


    private void setupUIElements() {
        //設定主畫面標題或圖片
        // setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.videos_by_google_banner));
        setTitle("My Taiwan TV");

        //設定主選單的顯示模式
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(false);

        //設定主選單背景顏色
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        //設定搜尋按鈕的顏色
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    /**
     * 初始化主選單選項及子選單的選項
     */
    private void initRow() {
        List<Channel> list = ChannelList.setupChannels();
        mRowArrayObjectAdapter = new ArrayObjectAdapter(new ListRowPresenter());//建立主選單adpater

        CardPresenter cardPresenter = new CardPresenter();//建立cardPresenter物件
        int i;
        for (i = 0; i < CHANNEL_TYPE_ROWS; i++) {

            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);//建立裡面包含著cardPresenter的子選單adapter
            for (int j = 0; j < list.size(); j++) {//取出list中的元素加入子選單中
                listRowAdapter.add(list.get(j));
            }

            HeaderItem headerItem = new HeaderItem(i, ChannelList.CHANNEL_CATEGORY[i]);//設定主選單選項名稱及子選單名稱

            mRowArrayObjectAdapter.add(new ListRow(headerItem, listRowAdapter));//將標題名稱與要加入的子選單綁在一起
        }

        GridItemPresenter gridItemPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridArrayAdapter = new ArrayObjectAdapter(gridItemPresenter);
        gridArrayAdapter.add("個人設定");
        HeaderItem gridHeaderItem = new HeaderItem(i, "喜好設定");
        mRowArrayObjectAdapter.add(new ListRow(gridHeaderItem, gridArrayAdapter));

        setAdapter(mRowArrayObjectAdapter);
    }

    /**
     * 註冊監聽事件
     */
    private void setupEventListener() {
        //設定Search按鈕的監聽事件並顯示按鈕
        //        setOnSearchClickedListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                Toast.makeText(getActivity(), "按下Search按鈕", Toast.LENGTH_SHORT).show();
        //            }
        //        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBackgroundTimer != null) {
            MyTools.myLog("MainFragment : onDestroy");
            mBackgroundTimer.cancel();
        }
    }

    /**
     * 選項被點選的監聽事件
     */
    private class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Channel) {
                Channel channel = (Channel) item;
                MyTools.myLog("Item : " + channel.toString());
                Intent intent = new Intent(getActivity(), com.view.details.ChannelDetailsActivity.class);
                intent.putExtra(ChannelDetailsActivity.CHANNEL, channel);

                //加入切換至DetailsActivity的畫面過場方式，主要是讓切換過程更無違和感
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        ChannelDetailsActivity.SHARED_ELEMENT_NAME).toBundle();

                getActivity().startActivity(intent, bundle);

            } else if (item instanceof String) {
                if (((String) item).indexOf("個人設定") >= 0) {
                    Toast.makeText(getActivity(), "進入個人設定頁面", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 選項被選取(只取得焦點，未點選)時的監聽事件
     * <p/>
     * 選項取得焦點時，延遲指定毫秒(BACKGROUND_UPDATE_DELAY)後開始下載圖片，然後更新選項背景
     */
    private class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Channel) {
                mBackgroundURI = ((Channel) item).getBackgroundImageURI();
                startBackgroundTimer();
            }
        }
    }

    /**
     * 開始執行計時任務
     */
    private void startBackgroundTimer() {
        if (mBackgroundTimer != null) {//若計時器不為null，則取消所有已安排的任務
            mBackgroundTimer.cancel();
        }

        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    /**
     * 更新背景的計時任務
     */
    private class UpdateBackgroundTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI.toString());
                    }
                }
            });
        }
    }

    /**
     * 下載圖片後完成後更新背景
     *
     * @param uri 圖片來源
     */
    private void updateBackground(String uri) {
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;

        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackgroundDrawable)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });

        mBackgroundTimer.cancel();
    }


}
