package com.example.myqq.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Sqlitem {
    Opensql opensql;
    SQLiteDatabase sd;
    public Sqlitem(Context context) {
        opensql=new Opensql(context);
        sd=opensql.getWritableDatabase();
    }
    
    public void exec(String sql){
        sd.execSQL(sql);
    }
    
    public Cursor query(String sql){
        Cursor cursor=sd.rawQuery(sql,null);
        return cursor;
    }
    
    
    class Opensql extends SQLiteOpenHelper {
        
        public Opensql(@Nullable Context context) {
            super(context, "sjk", null, 2);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("");
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
        }
    }
}

