package com.yxing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.yxing.R;

import java.io.Closeable;
import java.io.IOException;

public class AudioUtil implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, Closeable {

    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;

    private final Activity activity;
    private MediaPlayer mediaPlayer;

    private int audioId;

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
     * 开启响铃和震动
     */
    public synchronized void playBeepSoundAndVibrate() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    /**
     * 创建MediaPlayer
     *
     * @param activity
     * @return
     */
    private MediaPlayer buildMediaPlayer(Context activity) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 监听是否播放完成
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        // 配置播放资源
        try {
            AssetFileDescriptor file = activity.getResources()
                    .openRawResourceFd(audioId == 0 ? R.raw.beep : audioId);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
            } finally {
                file.close();
            }
            // 设置音量
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException ioe) {
            mediaPlayer.release();
            return null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // When the beep has finished playing, rewind to queue up another one.
        mp.seekTo(0);
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
