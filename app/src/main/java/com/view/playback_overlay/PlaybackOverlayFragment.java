package com.view.playback_overlay;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.model.Channel;
import com.model.ChannelList;
import com.presenter.CardPresenter;
import com.view.details.ChannelDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import tools.MyTools;

public class PlaybackOverlayFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment {
    private static final boolean SHOW_DETAIL = true;                //是否在播放控制列中顯示頻道描述
    private static final boolean HIDE_MORE_ACTION = false;
    private static final int CARD_WIDTH = 200;                      //
    private static final int CARD_HEIGHT = 240;                     //
    private static Context sContext;

    private ArrayObjectAdapter mRowAdapter;

    private PlaybackControlsRow mPlaybackControlsRow;               //播放控制列
    private PlayPauseAction mPlayPauseAction;                       //暫停
    private RepeatAction mRepeatAction;                             //重複
    private ThumbsUpAction mThumbsUpAction;                         //點讚
    private ThumbsDownAction mThumbsDownAction;                     //點不讚
    private ShuffleAction mShuffleAction;                           //播放進度的拖移鈕
    private FastForwardAction mFastForwardAction;                   //快轉
    private RewindAction mRewindAction;                             //倒帶
    private SkipNextAction mSkipNextAction;                         //直接播放下一個
    private SkipPreviousAction mSkipPreviousAction;                 //直接播放前一個

    private ArrayList<Channel> mItems = new ArrayList<Channel>();   //頻道的list
    private Channel mSelectedChannel;                               //被選取的Channel
    private int mCurrentItem;                                       //當前項目的索引(?)

    private Handler mHandler;

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

        setBackgroundType(BG_LIGHT);
        setFadingEnabled(false);


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
                } else if (actionID == mSkipPreviousAction.getId()) {       //按下播放上一個
                    MyTools.myLog("播放下一個");
                } else if (actionID == mFastForwardAction.getId()) {        //按下快轉
                    MyTools.myLog("快轉");
                } else if (actionID == mRewindAction.getId()) {             //按下倒帶
                    MyTools.myLog("倒帶");
                }
                if (action instanceof PlaybackControlsRow.MultiAction) {
                    ((PlaybackControlsRow.MultiAction) action).nextIndex();

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

    }

    private void addOtherRows() {

    }

    /**
     * 初始化控制列
     */
    private void addPlaybackContrlosRow() {
        if (SHOW_DETAIL) {
            mPlaybackControlsRow = new PlaybackControlsRow(mSelectedChannel);
        } else {
            mPlaybackControlsRow = new PlaybackControlsRow();
        }
        mRowAdapter.add(mPlaybackControlsRow);

        updatePlaybackRow(mCurrentItem);
    }

    private void updatePlaybackRow(int mCurrentItem) {
        if (mPlaybackControlsRow.getItem() != null) {
            Channel channel = (Channel) mPlaybackControlsRow.getItem();
            channel.setTitle(mItems.get(mCurrentItem).getTitle());
            channel.setStudio(mItems.get(mCurrentItem).getStudio());
        }
        if (SHOW_DETAIL) {
            updateVideoImage(mItems.get(mCurrentItem).getCardImageURI().toString());
        }
        mRowAdapter.notifyArrayItemRangeChanged(0,1);

    }

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

    static class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {
        @Override
        protected void onBindDescription(ViewHolder vh, Object item) {
            vh.getTitle().setText(((Channel) item).getTitle());
            vh.getSubtitle().setText(((Channel) item).getStudio());
        }
    }
}
