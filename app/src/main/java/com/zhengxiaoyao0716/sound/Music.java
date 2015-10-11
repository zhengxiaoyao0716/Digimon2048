package com.zhengxiaoyao0716.sound;

import android.content.Context;
import android.media.MediaPlayer;
import com.zhengxiaoyao0716.digimon2048.R;

public enum Music {
    INSTANCE;

    private MediaPlayer mediaPlayer;

    public boolean musicSwitch;
    public void playMusic(final Context context)
    {
        if (musicSwitch)
            new Thread() {
                @Override
                public void run() {
                    synchronized (INSTANCE) {
                        mediaPlayer = MediaPlayer.create(context, R.raw.butterfly);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    }
                }
            }.start();
    }
    public synchronized void stopMusic()
    {
        if (mediaPlayer == null) return;
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}