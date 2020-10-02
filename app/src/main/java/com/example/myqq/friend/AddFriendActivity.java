package com.example.myqq.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myqq.Dialog;
import com.example.myqq.R;
import com.example.myqq.sql.MySqlite;
import com.example.myqq.sql.SqlExec;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {
    private EditText friendNumber;
    private Button add,can;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
    
        Intent intent=getIntent();
        final String userNumber=intent.getStringExtra("userNumber");
        
        //添加好友
        friendNumber=findViewById(R.id.friendNumber);
        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Number=friendNumber.getText().toString();
                if(userNumber.equals(Number)){//是否是添加自己
                    new Dialog(AddFriendActivity.this, "不能添加自己为好友！",
                            new Dialog.OnClick() {
                                @Override
                                public void click() {
                                    friendNumber.setText("");
                                }
                            }, null);
                    return;
                }
                SqlExec se=SqlExec.getInstance(AddFriendActivity.this);
                se.create();
                boolean isAdd=se.addFriend(userNumber,Number);//向数据库添加数据
                if (isAdd){//添加情况
                    //获取该好友信息
                    Cursor cursor=se.query("select * from "+ MySqlite.USER_TABLE
                            +" where "+MySqlite.USER_NUMBER+"="+Number);
                    cursor.moveToNext();
                    String fName=String.valueOf(cursor.getString(1));
                    String fNumber=String.valueOf(cursor.getInt(0));
                    cursor.close();
                    //更新好友列表页面和数据
                    FriendActivity.updateFriend(fName,fNumber);
                    new Dialog(AddFriendActivity.this, "添加好友成功！",
                            new Dialog.OnClick() {
                                @Override
                                public void click() {
                                    friendNumber.setText("");
                                    finish();//关闭该页面,跳转回好友界面
                                }
                            },null);
                }else{
                    new Dialog(AddFriendActivity.this,
                            "添加好友失败！\n请检查账号是否正确！\n或是已存在该好友！",
                            null, new Dialog.OnClick() {
                                @Override
                                public void click() {
                                    friendNumber.setText("");
                                }
                            });
                }
                se.close();
            }
        });
        //取消键
        can=findViewById(R.id.can);
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendNumber.setText("");
                finish();
            }
        });
        
        //推荐好友
        SqlExec se=SqlExec.getInstance(this);
        se.create();
        ArrayList<String> userNumbers=se.getNotFriend(userNumber);
        ArrayList<String> userNames=se.getTagList(userNumbers,MySqlite.USER_NAME);
        se.close();
        RecyclerView possibleFriend =findViewById(R.id.possibleFriend);
        possibleFriend.setLayoutManager(new LinearLayoutManager(this));
        possibleFriend.setAdapter(new PossibleFriendAdapter(this,
                userNumbers,userNames,userNumber));
        
    }
}
