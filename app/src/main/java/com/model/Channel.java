package com.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by WilsonHuang on 2016/2/14.
 * <p/>
 * 封裝頻道資訊的類別
 */
public class Channel implements Serializable {
    private static long count = 0;  //
    private long id;                //影片ID
    private String title;           //標題
    private String description;     //敘述
    private String bgImageUrl;      //背景圖片位置
    private String cardImageUrl;    //卡片圖片位置
    private String videoUrl;        //影片位置
    private String studio;          //作者or工作室
    private String category;        //分類

    public Channel() {
    }

    public static long getCount() {
        return count;
    }

    public static void increaseCount() {
        count++;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBgImageUrl() {
        return bgImageUrl;
    }

    public void setBgImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public URI getBackgroundImageURI() {
        try {
            return new URI(getBgImageUrl());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public URI getCardImageURI() {
        try {
            return new URI(getCardImageUrl());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", bgImageUrl='" + bgImageUrl + '\'' +
                ", backgroundImageURI='" + getBackgroundImageURI().toString() + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
