package com.zhengxiaoyao0716.net;

import android.os.Handler;
import android.os.Message;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

public class GetRankList implements Runnable{
    private Handler handler;
    private int days, number;
    private boolean isDESC;
    public GetRankList(Handler handler, int days, int number, boolean isDESC)
    {
        this.handler = handler;
        this.days = days;
        this.number = number;
        this.isDESC = isDESC;
    }
    @Override
    public void run() {
        Message msg = handler.obtainMessage();
        try {
            msg.obj = HttpUtilities.getRankList(days, number, isDESC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.sendMessage(msg);
    }
}
