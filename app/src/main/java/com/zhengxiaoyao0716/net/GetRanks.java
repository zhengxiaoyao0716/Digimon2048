package com.zhengxiaoyao0716.net;

import android.os.Handler;
import android.os.Message;

public class GetRanks implements Runnable{
    private Handler handler;
    private int days, number;
    private boolean isDESC;
    public GetRanks(Handler handler, int days, int number, boolean isDESC)
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
            msg.obj = HttpUtilities.getRanks(days, number, isDESC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.sendMessage(msg);
    }
}
