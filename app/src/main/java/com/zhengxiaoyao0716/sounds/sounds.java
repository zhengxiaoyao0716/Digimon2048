package com.zhengxiaoyao0716.sounds;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2015/10/7.
 */
public class Sounds {
    private SoundPool soundPool;
    private Map<String, Integer> soundIds;
    private Sounds()
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else //noinspection deprecation
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundIds = new HashMap<>();
    }
    private static final Sounds INSTANCE = new Sounds();
    public static Sounds getInstance() { return INSTANCE; }

    public void loadSound(Context context, final String name, int resourceId)
    { soundIds.put(name, soundPool.load(context, resourceId, 1)); }

    public void playSound(String name)
    {
        if (soundIds.containsKey(name))
            soundPool.play(soundIds.get(name), 1, 1, 0, 0, 1);
    }
}