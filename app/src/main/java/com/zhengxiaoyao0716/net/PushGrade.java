package com.zhengxiaoyao0716.net;

public class PushGrade implements Runnable{
    private int level, score;
    private String name;
    private long time;
    public PushGrade(int level, int score, String name, long time)
    {
        this.level = level;
        this.score = score;
        this.name = name;
        this.time = time;
    }
    @Override
    public void run() {
        try {
            HttpUtilities.postGrade(level, score, name, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
