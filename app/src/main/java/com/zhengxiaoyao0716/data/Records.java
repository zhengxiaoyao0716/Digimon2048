package com.zhengxiaoyao0716.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;

import java.text.SimpleDateFormat;

/**
 * @author 薛函
 */
public class Records extends SQLiteOpenHelper {
    private Context context;
    public Records(Context context) {
        super(context, "Records.db", null, 1);
        this.context = context;
    }

    private static final String DICTIONARY_TABLE_NAME = "records";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(
                "CREATE TABLE %s (level INT, score INT, time char(19));",
                DICTIONARY_TABLE_NAME));
    }

    public JSONArray getRecordsList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DICTIONARY_TABLE_NAME, new String[]{"level","score","time"},
                null,null,null,null,null);
        JSONArray recordListJA = new JSONArray();
        int number = 0;
        while(cursor.moveToNext()){
            JSONArray record = new JSONArray()
                    .put(context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
                            .getString("playerName", "Unknown"))
                    .put(String.format("No.%2d:", ++number))
                    .put(String.valueOf(cursor.getInt(cursor.getColumnIndex("score"))))
                    .put(String.format(
                            "Level:%02d", cursor.getInt(cursor.getColumnIndex("level"))))
                    .put(new SimpleDateFormat("MM/dd HH:mm").format(Long.valueOf(
                            cursor.getString(cursor.getColumnIndex("time")))));
            recordListJA.put(record);
        }
        db.close();
        return new JSONArray().put(recordListJA);
    }

    public void insert(int level,int score, long time){

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
        cv.put("time", String.valueOf(time));
        db.insert(DICTIONARY_TABLE_NAME, null, cv);
        //关闭数据库
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}