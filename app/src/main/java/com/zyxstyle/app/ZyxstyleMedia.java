package com.zyxstyle.app;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.VideoView;

public class ZyxstyleMedia {

    private final Context context;
    private final VideoView videoView;

    public ZyxstyleMedia(Context context, VideoView videoView) {
        this.context = context;
        this.videoView = videoView;
    }

    public void start() {
        Uri uri = Uri.parse(
            "android.resource://" +
            context.getPackageName() +
            "/" +
            R.raw.key_background
        );

        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(
            mp -> {
                mp.setLooping(true);
                mp.setVolume(1.0f, 1.0f);
                videoView.start();
            }
        );

        videoView.setOnCompletionListener(
            mp -> videoView.start()
        );
    }

    public void pause() {
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    public void resume() {
        if (videoView != null) {
            videoView.start();
        }
    }

    public void stop() {
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    public VideoView getVideoView() {
        return videoView;
    }
}
