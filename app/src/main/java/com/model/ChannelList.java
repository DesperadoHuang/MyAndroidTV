package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WilsonHuang on 2016/2/14.
 */
public final class ChannelList {
    public static final String CHANNEL_CATEGORY[] = {
            "無線台",
            "綜合台",
            "體育台",
            "新聞台",
            "其他台"
    };

    public static List<Channel> list;

    public static List<Channel> setupChannels() {
        list = new ArrayList<Channel>();

        String title[] = {"新聞", "頻道二", "頻道三", "頻道四", "頻道五"};
        String description = "頻道簡介";
        String videoUrl[] = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose.mp4"};

        String bgImageUrl[] = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/bg.jpg",};

        String cardImageUrl[] = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/card.jpg"};

        for (int i = 0; i < 5; i++) {
            list.add(buildChannelInfo("新聞台", title[i], description, "頻道來源", videoUrl[i], cardImageUrl[i], bgImageUrl[i]));
        }

        return list;
    }

    private static Channel buildChannelInfo(String category, String title, String description, String studio,
                                            String videoUrl, String cardImageUrl, String bgImageUrl) {
        Channel channel = new Channel();
        channel.setId(Channel.getCount());
        Channel.increaseCount();
        channel.setCategory(category);
        channel.setTitle(title);
        channel.setDescription(description);
        channel.setStudio(studio);
        channel.setVideoUrl(videoUrl);
        channel.setCardImageUrl(cardImageUrl);
        channel.setBgImageUrl(bgImageUrl);

        return channel;
    }
}
