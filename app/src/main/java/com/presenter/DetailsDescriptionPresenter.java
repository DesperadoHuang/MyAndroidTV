package com.presenter;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.model.Channel;

/**
 * Created by WilsonHuang on 2016/2/18.
 */
public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    @Override
    protected void onBindDescription(ViewHolder vh, Object item) {
        Channel channel = (Channel) item;
        if (channel != null) {
            vh.getTitle().setText(channel.getTitle());
            vh.getSubtitle().setText(channel.getStudio());
            vh.getBody().setText(channel.getDescription());
        }
    }
}
