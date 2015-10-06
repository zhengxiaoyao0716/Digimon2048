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
public class SqlRecords extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DICTIONARY_TABLE_NAME = "records";
    private static final String DICTIONARY_TABLE_CREATE =
            new StringBuilder("CREATE TABLE ")
                    .append(DICTIONARY_TABLE_NAME)
                    .append(" (name").append(" TEXT, ")
                    .append("level").append(" INT, ")
                    .append("score").append(" INT, ")
                    .append("time").append(" char(19));")
                    .toString();

    public SqlRecords(Context context) {
        super(context, "game.db", null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    public List<? extends Map<String, ?>> list(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DICTIONARY_TABLE_NAME, new String[]{"name","level","score","time"},null,null,null,null,null);
        List<Map<String, ?>> recordList = new ArrayList<>();

        while(cursor.moveToNext()){
            Map<String, Object> map = new HashMap<>();
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("level", cursor.getInt(cursor.getColumnIndex("level")));
            map.put("score", cursor.getInt(cursor.getColumnIndex("score")));
            map.put("time",
                    new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(Long.valueOf(
                                    cursor.getString(cursor.getColumnIndex("time"))))
            );
            recordList.add(map);
        }
        db.close();
        return recordList;
    }

    public void insert(String name,int level,int score){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + DICTIONARY_TABLE_NAME, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count >= 20) {
            db.rawQuery(new StringBuilder("DELETE FROM ")
                    .append(DICTIONARY_TABLE_NAME)
                    .append(" where time in ( select time from records limit 1)")
                    .toString(),null);
        }

        ContentValues cv = new ContentValues();
        cv.put("name", name);
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