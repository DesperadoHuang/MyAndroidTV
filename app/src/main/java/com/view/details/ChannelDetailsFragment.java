package com.view.details;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;

import com.UI.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.model.Channel;
import com.model.ChannelList;
import com.presenter.CardPresenter;
import com.presenter.DetailsDescriptionPresenter;
import com.view.main.MainActivity;
import com.view.playback_overlay.PlaybackOverlayActivity;

import java.util.List;

import tools.MyTools;

/**
 * 包裝著頻道的詳細介紹，也在詳細介紹下面顯示了相關頻道的選項
 */
public class ChannelDetailsFragment extends DetailsFragment {
    private static final int ACTION_WATCH = 1;

    private static final int DETAILS_THUMB_WIDTH = 274;     //詳細訊息的寬
    private static final int DETAILS_THUMB_HEIGHT = 274;    //詳細訊息的高

    private Channel mSelectedChannel;                       //被選取的頻道

    private ArrayObjectAdapter mArrayObjectAdapter;         //相關頻道的adapter
    private ClassPresenterSelector mClassPresenterSelector;

    private BackgroundManager mBackgroundManager;           //背景管理器
    private Drawable mDefaultBackground;                    //預設的背景圖片
    private DisplayMetrics mDisplayMetrics;                 //包裝著螢幕尺寸資訊

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MyTools.myLog("ChannelDetailsFragment : onCreate ");
        super.onCreate(savedInstanceState);

        prepareBackgroundManager();

        mSelectedChannel = (Channel) getActivity().getIntent().getSerializableExtra(ChannelDetailsActivity.CHANNEL);

        if (mSelectedChannel != null) {
            setupAdapter();
            setupDetailsOverviewRow();
            setupDetailsOverviewRowPresenter();
            setupChannelListRow();
            setupChannelListRowPresenter();
            updateBackground(mSelectedChannel.getBgImageUrl());
            setOnItemViewClickedListener(new ItemViewClickedListener());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        MyTools.myLog("ChannelDetailsFragment : onStop");
        super.onStop();
    }

    private void prepareBackgroundManager() {
        MyTools.myLog("ChannelDetailsFragment : prepareBackgroundManager");
        mBackgroundManager = BackgroundManager.getInstance(getActivity()); //取得BackgroundManager實體
        mBackgroundManager.attach(getActivity().getWindow());//將BackgroundManager與要顯示視窗連結
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);

        //取得螢幕解析度
        mDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    private void setupAdapter() {
        MyTools.myLog("ChannelDetailsFragment : setupAdapter");
        mClassPresenterSelector = new ClassPresenterSelector();
        mArrayObjectAdapter = new ArrayObjectAdapter(mClassPresenterSelector);
        setAdapter(mArrayObjectAdapter);
    }

    private void setupDetailsOverviewRow() {
        MyTools.myLog("ChannelDetailsFragment : setupDetailsOverviewRow");
        final DetailsOverviewRow detailsOverviewRow = new DetailsOverviewRow(mSelectedChannel);
        detailsOverviewRow.setImageDrawable(getResources().getDrawable(R.drawable.default_background));
        int width = MyTools.convertDpToPixel(getActivity().getApplicationContext(), DETAILS_THUMB_WIDTH);
        int height = MyTools.convertDpToPixel(getActivity().getApplicationContext(), DETAILS_THUMB_HEIGHT);
        Glide.with(getActivity())
                .load(mSelectedChannel.getCardImageUrl())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        MyTools.myLog("ChannelDetailsFragment : setupDetailsOverviewRow...onResourceReady");
                        detailsOverviewRow.setImageDrawable(resource);
                        mArrayObjectAdapter.notifyArrayItemRangeChanged(0, mArrayObjectAdapter.size());
                    }
                });
        detailsOverviewRow.addAction(new Action(ACTION_WATCH, "觀看", "頻道"));
        mArrayObjectAdapter.add(detailsOverviewRow);
    }

    private void setupDetailsOverviewRowPresenter() {
        MyTools.myLog("ChannelDetailsFragment : setupDetailsOverviewRowPresenter");
        //設定details的大小與背景顏色
        DetailsOverviewRowPresenter detailsOverviewRowPresenter = new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsOverviewRowPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));
        detailsOverviewRowPresenter.setStyleLarge(true);//設定 details overview 的大小(預設是true)

        //設定共享元素的進入動畫，主要是要讓畫面在切換時更為順暢
        //參考資料:http://blog.isming.me/2014/11/13/creating-app-with-material-design-five-animations/
        detailsOverviewRowPresenter.setSharedElementEnterTransition(getActivity(), ChannelDetailsActivity.SHARED_ELEMENT_NAME);

        detailsOverviewRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                MyTools.myLog("ChannelDetailsFragment : setupDetailsOverviewRowPresenter...onActionClicked");
                if (action.getId() == ACTION_WATCH) {
                    Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
                    intent.putExtra(ChannelDetailsActivity.CHANNEL, mSelectedChannel);
                    startActivity(intent);
                }
            }
        });

        mClassPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsOverviewRowPresenter);
    }

    private void setupChannelListRow() {
        MyTools.myLog("ChannelDetailsFragment : setupChannelListRow");
        List<Channel> channelList = ChannelList.list;
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (int i = 0; i < channelList.size(); i++) {
            listRowAdapter.add(channelList.get(i));
        }
        HeaderItem headerItem = new HeaderItem(0, "相關頻道");
        mArrayObjectAdapter.add(new ListRow(headerItem, listRowAdapter));
    }

    private void setupChannelListRowPresenter() {
        MyTools.myLog("ChannelDetailsFragment : setupChannelListRowPresenter");
        mClassPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }

    private void updateBackground(String uri) {
        MyTools.myLog("ChannelDetailsFragment : updateBackground");
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        MyTools.myLog("ChannelDetailsFragment : updateBackground...onResourceReady");
                        mBackgroundManager.setDrawable(resource);
                    }
                });
    }

    private class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Channel) {
                Channel channel = (Channel) item;
                MyTools.myLog("ChannelDetailsFragment : ItemViewClickedListener...onItemClicked\n" +
                        "Item : " + channel.toString());
                Intent intent = new Intent(getActivity(), ChannelDetailsActivity.class);
                intent.putExtra(ChannelDetailsActivity.CHANNEL, mSelectedChannel);
                intent.putExtra(ChannelDetailsActivity.SHARED_ELEMENT_NAME, true);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        ChannelDetailsActivity.SHARED_ELEMENT_NAME
                ).toBundle();

                getActivity().startActivity(intent, bundle);
            }
        }
    }
}
