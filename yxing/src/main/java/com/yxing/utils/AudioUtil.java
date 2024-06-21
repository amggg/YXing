package com.yxing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.yxing.R;
import com.yxing.Config;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author am
 */
public class AudioUtil implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, Closeable {

    private static final float BEEP_VOLUME = 0.10f;
    private static final long PLAY_INTERVAL_TIME = 800L;

    private final Activity activity;
    private MediaPlayer mediaPlayer;

    private final int audioId;
    private long lastPlayTime = 0;

    public AudioUtil(Activity activity, int audioId) {
        this.activity = activity;
        this.mediaPlayer = null;
        this.audioId = audioId;
        updatePrefs();
    }

    private synchronized void updatePrefs() {
        if (mediaPlayer == null) {
            // 设置activity音量控制键控制的音频流
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = buildMediaPlayer(activity);
        }
    }

    /**
     * 播放
     */
    public synchronized void playSound() {
        try {
            if (System.currentTimeMillis() - lastPlayTime < PLAY_INTERVAL_TIME) {
                return;
            }
            if (mediaPlayer != null) {
                mediaPlayer.start();
                lastPlayTime = System.currentTimeMillis();
            }
        } catch (IllegalStateException e) {
            Log.e(Config.TAG, "playSound error : " + e.getMessage());
        }
    }

    /**
     * 创建MediaPlayer
     */
    private MediaPlayer buildMediaPlayer(Context activity) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mediaPlayer.setAudioAttributes(audioAttributes);
        // 监听是否播放完成
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        // 配置播放资源
        try {
            try (AssetFileDescriptor file = activity.getResources()
                    .openRawResourceFd(audioId == 0 ? R.raw.beep : audioId)) {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
            }
            // 设置音量
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException ioe) {
            mediaPlayer.release();
            Log.e(Config.TAG, "buildMediaPlayer error : " + ioe.getMessage());
            return null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // When the beep has finished playing, rewind to queue up another one.
        try {
            mp.seekTo(0);
        } catch (IllegalStateException e) {
            Log.e(Config.TAG, "seek error : " + e.getMessage());
        }
    }

    @Override
    public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            // we are finished, so put up an appropriate error toast if required
            // and finish
            activity.finish();
        } else {
            // possibly media player error, so release and recreate
            mp.release();
            mediaPlayer = null;
            updatePrefs();
        }
        return true;
    }

    @Override
    public synchronized void close() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
