package com.view.playback_overlay;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.model.Channel;
import com.model.ChannelList;
import com.presenter.CardPresenter;
import com.view.details.ChannelDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class PlaybackOverlayFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment {
    private static Context sContext;

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
}
