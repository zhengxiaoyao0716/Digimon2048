package com.zhengxiaoyao0716.data;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 正逍遥0716
 */
public class Records {
    private Records() {}

    //获取记录
    public static List<? extends Map<String, ?>> getRecordList(Context context)
    {
        JSONArray records = null;
        try {
            //read file
            FileInputStream inputStream = context.openFileInput("gameRecord");
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            String recordsStr = new String(arrayOutputStream.toByteArray());
            records = new JSONArray(recordsStr);
        } catch (Exception e) {
            e.printStackTrace();
            records = null;
        }
        if (records == null) return null;

        List<Map<String, ?>> recordList = new ArrayList<>();
        for (int index = 0; index < records.length(); )
        {
            Map<String, Object> record = new HashMap<>();
            JSONObject recordJA = records.optJSONObject(index);
            record.put("number", String.format("No.%2d:", ++index));
            record.put("score", recordJA.optInt("score", -1));
            record.put("name", String.format("Level:%2d", recordJA.optInt("level", 0)));
            record.put("time", new SimpleDateFormat("MM/dd HH:mm")
                    .format(recordJA.optLong("time", 0)));
            recordList.add(record);
        }
        return recordList;
    }

    //添加记录
    private static Thread addRecordThread = new Thread();
    public static void add(final Context context, final int level, final int score)
    {
        if (addRecordThread.isAlive()) return;
        else addRecordThread = new Thread(){
            @Override
            public void run()
            {
                JSONArray records = null;
                try {
                    //read file
                    FileInputStream inputStream = context.openFileInput("gameRecord");
                    byte[] bytes = new byte[1024];
                    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                    while (inputStream.read(bytes) != -1) {
                        arrayOutputStream.write(bytes, 0, bytes.length);
                    }
                    inputStream.close();
                    arrayOutputStream.close();
                    String recordsStr = new String(arrayOutputStream.toByteArray());
                    records =  new JSONArray(recordsStr);
                    //只保留19个
                    if (records.length() >= 20) {
                        //Api19以上的方法
                        if (Build.VERSION.SDK_INT >= 19) records.remove(0);
                        else //noinspection deprecation
                            records = new JSONArray(
                                    new StringBuilder(recordsStr)
                                            .delete(1, recordsStr.indexOf("},") + 2)
                                            .toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    records = new JSONArray();
                }

                try {
                    //记录本次
                    records.put(
                            new JSONObject().put("level", level).put("score", score)
                            .put("time", System.currentTimeMillis()));

                    //write file
                    FileOutputStream outputStream = context.openFileOutput("gameRecord",
                            Activity.MODE_PRIVATE);
                    outputStream.write(records.toString().getBytes());
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        addRecordThread.start();
    }
}
