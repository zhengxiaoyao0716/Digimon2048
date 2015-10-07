package com.zhengxiaoyao0716.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 薛函
 */
public class Records extends SQLiteOpenHelper {
    private static final String DICTIONARY_TABLE_NAME = "records";

    public Records(Context context) {
        super(context, "Records.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(
                "CREATE TABLE %s (level INT, score INT, time char(19));",
                DICTIONARY_TABLE_NAME));
    }

    public List<? extends Map<String, ?>> list(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DICTIONARY_TABLE_NAME, new String[]{"level","score","time"},
                null,null,null,null,null);
        List<Map<String, ?>> recordList = new ArrayList<>();
        int number = 0;
        while(cursor.moveToNext()){
            Map<String, Object> map = new HashMap<>();
            map.put("number", String.format("No.%2d:", ++number));
            map.put("name", String.format("Level:%2d", cursor.getInt(cursor.getColumnIndex("level"))));
            map.put("score", cursor.getInt(cursor.getColumnIndex("score")));
            map.put("time",
                    new SimpleDateFormat("MM/dd HH:mm").format(Long.valueOf(
                                    cursor.getString(cursor.getColumnIndex("time"))))
            );
            recordList.add(map);
        }
        db.close();
        return recordList;
    }

    public void insert(int level,int score){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + DICTIONARY_TABLE_NAME, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        if (count >= 20) {
            db.execSQL(String.format(
                    "DELETE FROM %s where time in ( select time from records limit 1)",
                    DICTIONARY_TABLE_NAME));
        }

        ContentValues cv = new ContentValues();
        cv.put("level", level);
        cv.put("score", score);
        cv.put("time", String.valueOf(System.currentTimeMillis()));
        db.insert(DICTIONARY_TABLE_NAME, null, cv);
        //关闭数据库
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}