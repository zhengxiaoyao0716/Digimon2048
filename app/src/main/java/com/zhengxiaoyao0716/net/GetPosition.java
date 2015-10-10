package com.zhengxiaoyao0716.net;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;

public class GetPosition implements Runnable{
    private Handler handler;
    private int level, score;
    public GetPosition(Handler handler, int level, int score)
    {
        this.handler = handler;
        this.level = level;
        this.score = score;
    }
    @Override
    public void run() {
        Message msg = handler.obtainMessage();
        try {
            msg.arg1 = HttpUtilities.findPosition(level, score);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
