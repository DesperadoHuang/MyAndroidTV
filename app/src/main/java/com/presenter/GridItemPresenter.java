package com.presenter;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.UI.R;

/**
 * Created by WilsonHuang on 2016/2/17.
 */
public class GridItemPresenter extends Presenter {
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setBackgroundColor(context.getResources().getColor(R.color.default_background));
        textView.setTextColor(context.getResources().getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((TextView) viewHolder.view).setText((String) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
