package com.view.playback_overlay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.VideoView;

import com.UI.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.model.Channel;

import tools.MyTools;

public class PlaybackOverlayActivity extends Activity implements PlaybackOverlayFragment.OnPlayPauseClickedListener {

    private VideoView mVideoView;

    private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;
    private MediaSession mMediaSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyTools.myLog("PlaybackOverlayActivity : onCreate(Bundle savedInstanceState)");
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

    @Override
    protected void onDestroy() {
        MyTools.myLog("PlaybackOverlayActivity : onDestroy()");
        super.onDestroy();
        mVideoView.suspend();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        MyTools.myLog("PlaybackOverlayActivity : onKeyUp(int keyCode, KeyEvent event)");
        PlaybackOverlayFragment playbackOverlayFragment =
                (PlaybackOverlayFragment) getFragmentManager().findFragmentById(R.id.playback_controls_fragment);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                playbackOverlayFragment.togglePlayback(false);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                playbackOverlayFragment.togglePlayback(false);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
                    playbackOverlayFragment.togglePlayback(false);
                } else {
                    playbackOverlayFragment.togglePlayback(true);
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onFragmentPlayPause(Channel channel, int position, boolean playPause) {
        MyTools.myLog("PlaybackOverlayActivity : onFragmentPlayPause(Channel channel, int position, boolean playPause)");
        mVideoView.setVideoPath(channel.getVideoUrl());
        if (position == 0 || mPlaybackState == LeanbackPlaybackState.IDLE) {
            setupCallbacks();
            mPlaybackState = LeanbackPlaybackState.IDLE;
        }
        if (playPause && mPlaybackState != LeanbackPlaybackState.PLAYING) {
            mPlaybackState = LeanbackPlaybackState.PLAYING;
            if (position > 0) {
                mVideoView.seekTo(position);
                mVideoView.start();
            }
        } else {
            mPlaybackState = LeanbackPlaybackState.PAUSED;
            mVideoView.pause();
        }
        updatePlaybackState(position);
        updateMetadata(channel);

    }

    private void updatePlaybackState(int position) {
        MyTools.myLog("PlaybackOverlayActivity : updatePlaybackState(int position)");
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder().setActions(getAvailableActions());
        int state = PlaybackState.STATE_PLAYING;
        if (mPlaybackState == LeanbackPlaybackState.PAUSED) {
            state = PlaybackState.STATE_PAUSED;
        }
        stateBuilder.setState(state, position, 1.0f);
        mMediaSession.setPlaybackState(stateBuilder.build());
    }

    private long getAvailableActions() {
        MyTools.myLog("PlaybackOverlayActivity : getAvailableActions()");
        long actions = PlaybackState.ACTION_PLAY |
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH;
        if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
            actions |= PlaybackState.ACTION_PAUSE;
        }
        return actions;
    }

    private void updateMetadata(Channel channel) {
        MyTools.myLog("PlaybackOverlayActivity : updateMetadata(Channel channel)");
        final MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();
        String title = channel.getTitle().replace("_", " -");

        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, channel.getDescription());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, channel.getCardImageUrl());

        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, channel.getStudio());

        Glide.with(this)
                .load(Uri.parse(channel.getCardImageUrl()))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(500, 500) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, resource);
                        mMediaSession.setMetadata(metadataBuilder.build());
                    }
                });
    }

    private void loadViews() {
        MyTools.myLog("PlaybackOverlayActivity : loadViews()");
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setFocusable(false);
        mVideoView.setFocusableInTouchMode(false);
    }

    private void setupCallbacks() {
        MyTools.myLog("PlaybackOverlayActivity : setupCallbacks()");
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String msg = "";
                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                    msg = "MediaPlayer.MEDIA_ERROR_TIMED_OUT";
                } else if (extra == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    msg = "MediaPlayer.MEDIA_ERROR_SERVER_DIED";
                } else {
                    msg = "Failed to load video";
                }
                mVideoView.stopPlayback();
                mPlaybackState = LeanbackPlaybackState.IDLE;
                return false;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
                    mVideoView.start();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        MyTools.myLog("PlaybackOverlayActivity : onResume()");
        super.onResume();
        mMediaSession.setActive(true);
    }

    @Override
    protected void onPause() {
        MyTools.myLog("PlaybackOverlayActivity : onPause()");
        super.onPause();
        if (mVideoView.isPlaying()) {
            if (!requestVisibleBehind(true)) {
                stopPlayback();
            }
        } else {
            requestVisibleBehind(false);
        }
    }

    @Override
    protected void onStop() {
        MyTools.myLog("PlaybackOverlayActivity : onStop()");
        super.onStop();
        mMediaSession.release();
    }

    @Override
    public void onVisibleBehindCanceled() {
        MyTools.myLog("PlaybackOverlayActivity : onVisibleBehindCanceled()");
        super.onVisibleBehindCanceled();
    }

    private void stopPlayback() {
        MyTools.myLog("PlaybackOverlayActivity : stopPlayback()");
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    public static enum LeanbackPlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE;
    }

    private class MediaSessionCallback extends MediaSession.Callback {

    }
}
