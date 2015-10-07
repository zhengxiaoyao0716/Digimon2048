package com.zhengxiaoyao0716.sound;

public class Music {
    private static final Music INSTANCE = new Music();
    public static Music getInstance() { return INSTANCE; }

    public boolean musicSwitch;
}
