package com.example.myqq.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SqlExec {
    private static SqlExec sqlExec;
    private MySqlite mySqlite;
    private SQLiteDatabase database;
    
    private SqlExec(Context context){
        mySqlite=new MySqlite(context);
    }
    //获取实例
    public static SqlExec getInstance(Context context){
        if (sqlExec==null){
            return sqlExec=new SqlExec(context);
        }else{
            return sqlExec;
        }
    }
    //获取数据库
    public boolean create(){
        if (database==null){
            database=mySqlite.getWritableDatabase();
            return true;
        }
        return false;
    }
    //关闭数据库
    public void close(){
        database.close();
        database=null;
    }
    
    //执行增删改方法
    public void exec(String sql){
        database.execSQL(sql);
    }
    
    //执行查找方法
    public Cursor query(String sql){
        Cursor cursor=database.rawQuery(sql,null);
        return cursor;
    }
    
    //添加新用户
    public boolean addUser(String number,String name,String pass,String sex,String ah,String date,String gj){
        if (findNumber(number)){ return false; }
        exec("insert into "+MySqlite.USER_TABLE+" values('"+number+
                "','"+name+"','"+pass+"','"+sex+"','"+ah+"','"+date+"','"+gj+"')");
        return true;
    }
    
    //修改用户表信息
    public void update(String name,String sex, String ah,String date,
                       String gj,String whereTag,String wherer){
        exec("update "+MySqlite.USER_TABLE+" set "+ "username='"+name+"',gender='"+
                sex+"',hobby='"+ah+"',birthday='"+date+"',fromto='"+gj+
                "' where " +whereTag+"='"+wherer+"'");
    }
    
    //查找某账号是否存在
    public boolean findNumber(String number){
        boolean n=false;
        Cursor cursor=findTableTag(MySqlite.USER_TABLE,MySqlite.USER_NUMBER,
                MySqlite.USER_NUMBER,number);
        if (cursor.moveToNext()){ n=true; }
        cursor.close();
        return n;
    }
    
    //查找某table表中 selectTag 字段符合whereTag=value数据
    public Cursor findTableTag(String table,String selectTag,String whereTag,String value){
        return query("select "+selectTag+" from "
                +table+" where "+whereTag+"="+value);
    }
    
    //查找某账号好友，并将所有好友的账号组成ArrayList返回
    public ArrayList<String> findFriend(String userNumber){
        ArrayList<String> al=new ArrayList<>();
        //在friend列的好友
        Cursor cursor=findTableTag(MySqlite.FRIEND_TABLE,MySqlite.FRIEND_FRIENDID,
                MySqlite.FRIEND_USERID,userNumber);
        while(cursor.moveToNext()){
            al.add(cursor.getString(0));
        }
        //在user列的好友
        cursor=findTableTag(MySqlite.FRIEND_TABLE,MySqlite.FRIEND_USERID,
                MySqlite.FRIEND_FRIENDID,userNumber);
        while(cursor.moveToNext()){
            al.add(cursor.getString(0));
        }
        cursor.close();
        return al;
    }
    
    //添加好友关系
    public boolean addFriend(String userNumber,String friendNumber){
        boolean isAdd = false;
        int uNI=Integer.parseInt(userNumber);
        int fNI=Integer.parseInt(friendNumber);
        Cursor cursor=query("select "+MySqlite.USER_NUMBER+" from "
                +MySqlite.USER_TABLE+" where "+MySqlite.USER_NUMBER+"="+friendNumber);
        if (cursor.moveToNext()) {//查看账号是否存在
            if (uNI < fNI) {//号码比较大小，按user<friend存储
                cursor = findTableTag(MySqlite.FRIEND_TABLE,MySqlite.FRIEND_USERID, MySqlite.FRIEND_FRIENDID
                        , friendNumber + " and " + MySqlite.FRIEND_USERID + "=" + userNumber);
                if (cursor.moveToNext()) {//是否已是好友
                    isAdd = false;
                } else {
                    exec("insert into " + MySqlite.FRIEND_TABLE + " values(" + userNumber + "," + friendNumber + ")");
                    isAdd = true;
                }
            } else {
                cursor = findTableTag(MySqlite.FRIEND_TABLE,MySqlite.FRIEND_USERID, MySqlite.FRIEND_FRIENDID,
                        userNumber + " and " + MySqlite.FRIEND_USERID + "=" + friendNumber);
                if (cursor.moveToNext()) {
                    isAdd = false;
                } else {
                    exec("insert into " + MySqlite.FRIEND_TABLE + " values(" + friendNumber + "," + userNumber + ")");
                    isAdd = true;
                }
            }
        }
        cursor.close();
        return isAdd;
    }
    
    //删除好友关系
    public boolean deleteFriend(String userNumber,String friendNumber){
        boolean isDelete = false;
        int uNI=Integer.parseInt(userNumber);
        int fNI=Integer.parseInt(friendNumber);
        if (uNI < fNI) {//号码比较大小按情况删除
            exec("delete from "+MySqlite.FRIEND_TABLE+" where "
                    +MySqlite.FRIEND_USERID+"='"+userNumber+"' and "
                    +MySqlite.FRIEND_FRIENDID+"='"+friendNumber+"'");
            isDelete=true;
        } else {
            exec("delete from "+MySqlite.FRIEND_TABLE+" where "
                    +MySqlite.FRIEND_USERID+"='"+friendNumber+"' and "
                    +MySqlite.FRIEND_FRIENDID+"='"+userNumber+"'");
            isDelete=true;
        }
        return isDelete;
    }
    
    //从数据库获取某账号信息
    public Cursor getUserMess(String number){
        Cursor cursor=null;
        if (findNumber(number)) {
            cursor = findTableTag(MySqlite.USER_TABLE
                    , "number,username,gender,hobby,birthday,fromto"
                    , MySqlite.USER_NUMBER, number);
        }
        return cursor;
    }
    
    //通过某账号Cursor获取信息，返回StringBuilder
    public StringBuilder getMess(Cursor cursor){
        StringBuilder sb = new StringBuilder();
        if (cursor.moveToNext()) {
            for (int i = 0; i < MySqlite.USER_SUMTAG - 1; i++) {
                if (i == 4) {
                    Date date = new Date(cursor.getString(i));
                    DateFormat df = DateFormat.getDateInstance();
                    sb.append(MySqlite.TAGS.get(i) + df.format(date) + "\n");
                    continue;
                }
                sb.append(MySqlite.TAGS.get(i) + cursor.getString(i) + "\n");
            }
        }
        cursor.close();
        return sb;
    }
    
    //根据ArrayList账号集合，拿到指定的字段集合(用户表)
    public ArrayList<String> getTagList(ArrayList<String> numbers,String tag){
        ArrayList<String> friendNames=new ArrayList<>();
        Cursor cursor;
        //将好友指定字段数据添加到集合中
        for (String friend : numbers){
            //获取当前账号字段数据
            cursor=findTableTag(MySqlite.USER_TABLE,tag,MySqlite.USER_NUMBER,friend);
            cursor.moveToNext();
            friendNames.add(cursor.getString(0));
            cursor.close();
        }
        return friendNames;
    }
    
    //获取此账号所有未添加的好友，返回ArrayList账号集合
    public ArrayList<String> getNotFriend(String userNumber){
        ArrayList<String> notFriends=new ArrayList<>();
        ArrayList<String> friends=findFriend(userNumber);
        StringBuilder sb=new StringBuilder();
        for (String fined : friends){
            sb.append(" and "+MySqlite.USER_NUMBER+"!="+fined);
        }
        Cursor cursor=query("select "+MySqlite.USER_NUMBER+" from "+MySqlite.USER_TABLE
                +" where "+MySqlite.USER_NUMBER+"!="+userNumber+sb.toString());
        while(cursor.moveToNext()){
            notFriends.add(cursor.getString(0));
        }
        return notFriends;
    }
}
