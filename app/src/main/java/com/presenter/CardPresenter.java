package com.presenter;

import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.model.Channel;
import com.UI.R;
import com.bumptech.glide.Glide;

/**
 * Created by WilsonHuang on 2016/2/14.
 */
public class CardPresenter extends Presenter {

    private static int CARD_WIDTH = 313;            //卡片寬
    private static int CARD_HEIGHT = 176;           //卡片高
    private static int sSelectedBackgroundColor;    //被選取時的背景顏色
    private static int sDefaultBackgroundColor;     //預設背景顏色
    private Drawable mDefaultCardImage;             //預設卡片圖片

    //建立卡片要呈現的內容，卡片被點選時，預設動作為放大展開
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background);//卡片預設背景顏色
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background);//卡片被選擇時的背景顏色
        mDefaultCardImage = parent.getResources().getDrawable(R.drawable.channel_default_120_120);//卡片的預設圖片

        ImageCardView imageCardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {//設定卡片被點選時的動作
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        //接收來自D-pad的焦點控制
        imageCardView.setFocusable(true);
        imageCardView.setFocusableInTouchMode(true);

        updateCardBackgroundColor(imageCardView, false);

        return new ViewHolder(imageCardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Channel channel = (Channel) item;
        ImageCardView imageCardView = (ImageCardView) viewHolder.view;

        if (channel.getCardImageUrl() != null) {
            imageCardView.setTitleText(channel.getTitle());
            imageCardView.setContentText(channel.getStudio());
            imageCardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);

            //強大的ImageLoader
            Glide.with(viewHolder.view.getContext())
                    .load(channel.getCardImageUrl())
                    .fitCenter()                              //中心切圖，會填滿
                    .error(mDefaultCardImage)                  //load失敗時的Drawable
                    .into(imageCardView.getMainImageView());
        }
    }

    //消除圖像的引用，使垃圾收集器釋放內存
    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView imageCardView = (ImageCardView) viewHolder.view;
        imageCardView.setBadgeImage(null);
        imageCardView.setMainImage(null);
    }

    /**
     * 更新背景顏色
     *
     * @param imageCardView
     * @param selected
     */
    private static void updateCardBackgroundColor(ImageCardView imageCardView, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        imageCardView.setBackgroundColor(color);
        imageCardView.findViewById(R.id.info_field).setBackgroundColor(color);
    }
}
