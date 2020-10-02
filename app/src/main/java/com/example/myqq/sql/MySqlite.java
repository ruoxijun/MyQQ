package com.example.myqq.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MySqlite extends SQLiteOpenHelper {
    //数据库相关字段
    public static final String BASENAME="User.db";
    public static final int VERSION=1;
    //表相关字段
    public static final String USER_TABLE="user";
    public static final String FRIEND_TABLE="friend";
    //用户表字段名
    public static final int USER_SUMTAG=7;
    public static final String USER_NUMBER="number";
    public static final String USER_NAME="username";
    public static final String USER_PASSWORD="password";
    public static final ArrayList TAGS;
    //好友表字段名
    public static final String FRIEND_USERID="userid";
    public static final String FRIEND_FRIENDID="friendid";
    
    static {
        TAGS=new ArrayList();
        TAGS.add("账号：");
        TAGS.add("昵称：");
        TAGS.add("性别：");
        TAGS.add("爱好：");
        TAGS.add("生日：");
        TAGS.add("籍贯：");
    }
    
    //构造函数，保存数据库信息
    public MySqlite(@Nullable Context context) {
        super(context, BASENAME, null, VERSION);
    }
    
    @Override // 创建表
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+USER_TABLE+"(number char(50) primary key not null," +
                "username char(50) not null,password char(100) not null,gender char(2)," +
                "hobby char(100),birthday char(40),fromto char(40))");
        db.execSQL("create table "+FRIEND_TABLE+
                "(userid integer not null," +
                "friendid integer not null)");
    }
    
    @Override // 数据库表升级方法
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
