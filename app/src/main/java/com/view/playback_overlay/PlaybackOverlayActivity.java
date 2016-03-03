package com.view.playback_overlay;

import android.app.Activity;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.widget.VideoView;

import com.UI.R;
import com.model.Channel;

public class PlaybackOverlayActivity extends Activity implements PlaybackOverlayFragment.OnPlayPauseClickedListener {

    private VideoView mVideoView;

    private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;
    private MediaSession mMediaSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playback_controls);
        loadViews();
        setupCallbacks();
        mMediaSession = new MediaSession(this, "LeanbckSampleApp");
        mMediaSession.setCallback(new MediaSessionCallback());
        mMediaSession.setFlags(
                MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setActive(true);
    }

    private void setupCallbacks() {

    }

    private void loadViews() {

    }

    @Override
    public void onFragmentPlayPause(Channel channel, int position, boolean playPause) {

    }

    public static enum LeanbackPlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE;
    }

    private class MediaSessionCallback extends MediaSession.Callback {

    }
}
