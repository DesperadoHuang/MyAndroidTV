package com.view.playback_overlay;


import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.FastForwardAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RepeatAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RewindAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ShuffleAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipNextAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsDownAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsUpAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.model.Channel;
import com.model.ChannelList;
import com.presenter.CardPresenter;
import com.view.details.ChannelDetailsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tools.MyTools;

public class PlaybackOverlayFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment {
    private static final boolean SHOW_DETAIL = true;                //是否在播放控制列中顯示頻道描述
    private static final boolean HIDE_MORE_ACTION = false;
    private static final int CARD_WIDTH = 200;                      //
    private static final int CARD_HEIGHT = 240;                     //
    private static final int PRIMARY_CONTROLS = 5;                  //
    private static final boolean SHOW_IMAGE = PRIMARY_CONTROLS <= 5;//
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;
    private static final int SIMULATED_BUFFERED_TIME = 100000;
    private static final int BACKGROUND_TYPE = PlaybackOverlayFragment.BG_LIGHT;
    private static Context sContext;

    OnPlayPauseClickedListener mCallback;

    private ArrayObjectAdapter mRowAdapter;
    private ArrayObjectAdapter mPrimaryActionsAdapter;              //主要???
    private ArrayObjectAdapter mSecondaryActionsAdapter;            //次要

    private PlaybackControlsRow mPlaybackControlsRow;               //播放控制列
    private PlayPauseAction mPlayPauseAction;                       //暫停
    private RepeatAction mRepeatAction;                             //重複
    private ThumbsUpAction mThumbsUpAction;                         //點讚
    private ThumbsDownAction mThumbsDownAction;                     //點不讚
    private ShuffleAction mShuffleAction;                           //隨機撥放
    private FastForwardAction mFastForwardAction;                   //快轉
    private RewindAction mRewindAction;                             //倒帶
    private SkipNextAction mSkipNextAction;                         //直接播放下一個
    private SkipPreviousAction mSkipPreviousAction;                 //直接播放前一個

    private ArrayList<Channel> mItems = new ArrayList<Channel>();   //頻道的list
    private Channel mSelectedChannel;                               //被選取的Channel
    private int mCurrentItem;                                       //當前項目的索引(?)


    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnPlayPauseClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnPlayPauseClickedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sContext = getActivity();
        mItems = new ArrayList<Channel>();
        mSelectedChannel = (Channel) getActivity().getIntent().getSerializableExtra(ChannelDetailsActivity.CHANNEL);
        List<Channel> channelList = ChannelList.list;

        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());

        for (int i = 0; i < channelList.size(); i++) {
            mItems.add(channelList.get(i));
            if (mSelectedChannel.getTitle().contentEquals(mItems.get(i).getTitle())) {
                mCurrentItem = i;
            }
        }

        mHandler = new Handler();

        setBackgroundType(BACKGROUND_TYPE);
        setFadingEnabled(false);

        setupRows();

        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

            }
        });

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

            }
        });

    }

    private void setupRows() {
        ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();

        //建立播放控制列
        PlaybackControlsRowPresenter playbackControlsRowPresenter;
        if (SHOW_DETAIL) {//是否在撥放控制列中顯示頻道描述
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter(new DescriptionPresenter());
        } else {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter();
        }

        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                long actionID = action.getId();
                if (actionID == mPlayPauseAction.getId()) {                 //按下暫停
                    MyTools.myLog("暫停");
                    togglePlayback(mPlayPauseAction.getIndex() == PlayPauseAction.PLAY);
                } else if (actionID == mSkipNextAction.getId()) {           //按下播放下一個
                    MyTools.myLog("播放下一個");
                    next();
                } else if (actionID == mSkipPreviousAction.getId()) {       //按下播放上一個
                    MyTools.myLog("播放下一個");
                    prev();
                } else if (actionID == mFastForwardAction.getId()) {        //按下快轉
                    MyTools.myLog("快轉");
                } else if (actionID == mRewindAction.getId()) {             //按下倒帶
                    MyTools.myLog("倒帶");
                }
                if (action instanceof PlaybackControlsRow.MultiAction) {
                    ((PlaybackControlsRow.MultiAction) action).nextIndex();
                    notifyChanged(action);
                }
            }
        });
        playbackControlsRowPresenter.setSecondaryActionsHidden(HIDE_MORE_ACTION);

        classPresenterSelector.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

        mRowAdapter = new ArrayObjectAdapter(classPresenterSelector);

        addPlaybackContrlosRow();
        addOtherRows();

        setAdapter(mRowAdapter);
    }

    private void togglePlayback(boolean playPause) {
        if (playPause) {
            startProgressAutomation();
            setFadingEnabled(true);
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), mPlaybackControlsRow.getCurrentTime(), true);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PLAY));
        } else {
            stopProgressAutomation();
            setFadingEnabled(false);
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), mPlaybackControlsRow.getCurrentTime(), false);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PAUSE));
        }
        notifyChanged(mPlayPauseAction);
    }

    private void stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private void startProgressAutomation() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                int updatePeriod = getUpdatePeriod();
                int currentTime = mPlaybackControlsRow.getCurrentTime() + updatePeriod;
                int totalTime = mPlaybackControlsRow.getTotalTime();
                mPlaybackControlsRow.setCurrentTime(currentTime);
                mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
                if (totalTime > 0 && totalTime <= currentTime) {
                    next();
                }
                mHandler.postDelayed(this, updatePeriod);
            }
        };
        mHandler.postDelayed(mRunnable, getUpdatePeriod());
    }

    private void next() {
        if (++mCurrentItem >= mItems.size()) {
            mCurrentItem = 0;
        }
        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
    }

    private void prev() {
        if (--mCurrentItem > 0) {
            mCurrentItem = mItems.size() - 1;
        }
        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
    }

    private int getUpdatePeriod() {
        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0) {
            return DEFAULT_UPDATE_PERIOD;
        }
        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
    }

    /**
     * 通知更改主要及次要控制列
     *
     * @param action
     */
    private void notifyChanged(Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        adapter = mSecondaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
    }

    /**
     * 初始化控制列Row
     * 添加控制列中的播放控制按鈕
     */
    private void addPlaybackContrlosRow() {
        if (SHOW_DETAIL) {
            mPlaybackControlsRow = new PlaybackControlsRow(mSelectedChannel);
        } else {
            mPlaybackControlsRow = new PlaybackControlsRow();
        }
        mRowAdapter.add(mPlaybackControlsRow);

        updatePlaybackRow(mCurrentItem);

        ControlButtonPresenterSelector controlButtonPresenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(controlButtonPresenterSelector);
        mSecondaryActionsAdapter = new ArrayObjectAdapter(controlButtonPresenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
        mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

        //        mSkipPreviousAction = new SkipPreviousAction(sContext);//跳往上一個
        //        mRewindAction = new RewindAction(sContext);//倒帶
        mPlayPauseAction = new PlayPauseAction(sContext);//播放/暫停
        //        mFastForwardAction = new FastForwardAction(sContext);//快轉
        //        mSkipNextAction = new SkipNextAction(sContext);//跳往下一個

        //        mRepeatAction = new RepeatAction(sContext);//重複撥放
        //        mShuffleAction = new ShuffleAction(sContext);//隨機撥放
        mThumbsUpAction = new ThumbsUpAction(sContext);//喜歡
        //        mThumbsDownAction = new ThumbsDownAction(sContext);//不喜歡

        //        mPrimaryActionsAdapter.add(mSkipPreviousAction);
        //        mPrimaryActionsAdapter.add(mRewindAction);
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        //        mPrimaryActionsAdapter.add(mFastForwardAction);
        //        mPrimaryActionsAdapter.add(mSkipNextAction);
        mPrimaryActionsAdapter.add(mThumbsUpAction);

        //        mSecondaryActionsAdapter.add(mRepeatAction);
        //        mSecondaryActionsAdapter.add(mShuffleAction);
        //        mSecondaryActionsAdapter.add(mThumbsUpAction);
        //        mSecondaryActionsAdapter.add(mThumbsDownAction);
    }

    private void updatePlaybackRow(int mCurrentItem) {
        if (mPlaybackControlsRow.getItem() != null) {
            Channel channel = (Channel) mPlaybackControlsRow.getItem();
            channel.setTitle(mItems.get(mCurrentItem).getTitle());
            channel.setStudio(mItems.get(mCurrentItem).getStudio());
        }
        if (SHOW_IMAGE) {
            updateVideoImage(mItems.get(mCurrentItem).getCardImageURI().toString());
        }
        mRowAdapter.notifyArrayItemRangeChanged(0, 1);
        mPlaybackControlsRow.setTotalTime(getDuration());
        mPlaybackControlsRow.setCurrentTime(0);
        mPlaybackControlsRow.setBufferedProgress(0);
    }

    /**
     * 添加相關頻道Row
     */
    private void addOtherRows() {
        ArrayObjectAdapter relatedListRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (Channel channel : mItems) {
            relatedListRowAdapter.add(channel);
        }
        HeaderItem headerItem = new HeaderItem(0, "相關頻道");
        mRowAdapter.add(new ListRow(headerItem, relatedListRowAdapter));
    }

    /**
     * 取得媒體文件的長度
     *
     * @return
     */
    private int getDuration() {
        Channel channel = mItems.get(mCurrentItem);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//此類別的物件可取得媒體文件的相關訊息
        //Build.VERSION.SDK_INT : SDK版本
        //Build.VERSION_CODES.ICE_CREAM_SANDWICH : Android 4.0
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mmr.setDataSource(channel.getVideoUrl(), new HashMap<String, String>());
        } else {
            mmr.setDataSource(channel.getVideoUrl());
        }
        //從媒體文件中取出指定的訊息
        //MediaMetadataRetriever.METADATA_KEY_DURATION : 媒體文件的長度
        String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = Long.parseLong(time);
        return (int) duration;
    }

    @Override
    public void onStop() {
        stopProgressAutomation();
        super.onStop();
    }

    /**
     * 下載並更新頻道控制列的圖片
     *
     * @param uri 圖片的URI
     */
    private void updateVideoImage(String uri) {
        Glide.with(sContext)
                .load(uri)
                .centerCrop()
                .into(new SimpleTarget<GlideDrawable>(CARD_WIDTH, CARD_HEIGHT) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mPlaybackControlsRow.setImageDrawable(resource);
                        mRowAdapter.notifyArrayItemRangeChanged(0, mRowAdapter.size());
                    }
                });
    }

    public interface OnPlayPauseClickedListener {
        public void onFragmentPlayPause(Channel channel, int position, boolean playPause);
    }

    static class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {
        @Override
        protected void onBindDescription(ViewHolder vh, Object item) {
            vh.getTitle().setText(((Channel) item).getTitle());
            vh.getSubtitle().setText(((Channel) item).getStudio());
        }
    }
}
