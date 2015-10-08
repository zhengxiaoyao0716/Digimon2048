package com.zhengxiaoyao0716.sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import com.zhengxiaoyao0716.digimon2048.R;

import java.util.HashMap;
import java.util.Map;

public enum Sounds {
    INSTANCE;

    private SoundPool soundPool;
    private Map<String, Integer> soundIds;
    public void initSounds(Context context)
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
        soundIds.put("move", soundPool.load(context, R.raw.move, 1));
        soundIds.put("merge", soundPool.load(context, R.raw.merge, 1));
        soundIds.put("level_up", soundPool.load(context, R.raw.level_up, 1));
    }
    public boolean soundsSwitch;
    public void playSound(String name)
    {
        if (soundsSwitch && soundIds.containsKey(name))
            soundPool.play(soundIds.get(name), 1, 1, 0, 0, 1);
    }

    public  void releaseSounds() { soundPool.release(); }
}