package com.example.myqq.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.myqq.R;
import com.example.myqq.sql.MySqlite;
import com.example.myqq.sql.SqlExec;

import java.util.ArrayList;

public class FriendActivity extends AppCompatActivity {
    private TextView uName,uNumber;
    private ImageView fun;
    private EditText findFriend;
    private RecyclerView list_item;
    private String userNumber,userName;
    private PopupWindow pop;
    private static FriendAdapter friendAdapter;
    private static ArrayList<String> fname=new ArrayList<>();
    private static ArrayList<String> fnumber=new ArrayList<>();
    private static ArrayList<String> friendNames;
    private static ArrayList<String> friendNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        
        //获取登录跳转传来的参数，并对头部初始化
        final Intent intent=getIntent();
        userNumber=intent.getStringExtra("userNumber");
        
        //组件初始化
        uName=findViewById(R.id.uName);
        uNumber=findViewById(R.id.uNumber);
        uNumber.setText("QQ:"+userNumber);
        
        //好友界面右上角功能键
        fun=findViewById(R.id.fun);
        fun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view= LayoutInflater.from(FriendActivity.this).inflate(R.layout.fun_popup,null);
                Button addFriend=view.findViewById(R.id.addFriend);
                Button update=view.findViewById(R.id.upDate);
                Button uppass=view.findViewById(R.id.upPass);
                Button can=view.findViewById(R.id.can);
                
                //添加好友按钮
                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//跳转至添加好友页面
                        pop.dismiss();
                        Intent friendIntent=new Intent(FriendActivity.this
                                ,AddFriendActivity.class);
                        friendIntent.putExtra("userNumber",userNumber);
                        startActivity(friendIntent);
                    }
                });
                
                //更改自身信息
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent updatemessIntent=new Intent(FriendActivity.this
                                ,UpdateMessActivity.class);
                        updatemessIntent.putExtra("userNumber",userNumber);
                        startActivity(updatemessIntent);
                    }
                });
                
                //更改密码
                uppass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent updatepasssIntent=new Intent(FriendActivity.this,
                                UpdatePassActivity.class);
                        updatepasssIntent.putExtra("userNumber",userNumber);
                        startActivity(updatepasssIntent);
                    }
                });
                
                //取消按钮
                can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                    }
                });
                //弹出菜单设置
                pop=new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                //想PopupWindow点击外侧时消失需要设置一个背景，才能成功
                pop.setBackgroundDrawable(new BitmapDrawable());
                pop.setFocusable(true);//获取焦点
                pop.setOutsideTouchable(true);//点击外侧消失
                pop.showAsDropDown(fun,0,-30);//设置位置
            }
        });
        //拿到好友数据
        updateFriend();
        
        list_item=findViewById(R.id.list_item);
        //设置布局
        list_item.setLayoutManager(new LinearLayoutManager(this));
        //适配器对象
        friendAdapter=new FriendAdapter(this,friendNames,friendNumbers,userNumber);
        //设置适配器
        list_item.setAdapter(friendAdapter);
        
        //好友搜索
        findFriend=findViewById(R.id.findFriend);
        findFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String mgs=s.toString();
                System.out.println("改变后"+mgs.equals(""));
                if (mgs.equals("")){//搜索框为空时
                    friendAdapter.setData(friendNames,friendNumbers);
                } else {//有输入时
                    fname.clear();
                    fnumber.clear();
                    for (int i=0;i<friendNames.size();i++){
                        if (friendNames.get(i).contains(mgs)){
                            fname.add(friendNames.get(i));
                            fnumber.add(friendNumbers.get(i));
                        }else if (friendNumbers.get(i).contains(mgs)){
                            fname.add(friendNames.get(i));
                            fnumber.add(friendNumbers.get(i));
                        }
                    }
                    friendAdapter.setData(fname,fnumber);
                }
            }
        });
    }
    
    //当页面可见时
    @Override
    protected void onResume() {
        super.onResume();
        SqlExec se=SqlExec.getInstance(this);
        se.create();
        Cursor cursor=se.findTableTag(MySqlite.USER_TABLE,MySqlite.USER_NAME
                ,MySqlite.USER_NUMBER,userNumber);
        if (cursor.moveToNext()){//获取账号昵称
            userName=cursor.getString(0);
        }
        cursor.close();
        se.close();
        uName.setText(userName);//左上角昵称
        if(pop!=null) {
            pop.dismiss();//菜单消失
        }
    }
    
    //添加好友信息
    public static void updateFriend(String friendName,String friendNumber) {
        friendNames.add(friendName);
        friendNumbers.add(friendNumber);
        friendAdapter.notifyDataSetChanged();//刷新
    }
    
    //移除好友信息
    public static void deleteFriend(String name,String number) {
        fname.remove(name);
        fnumber.remove(number);
        friendNames.remove(name);
        friendNumbers.remove(number);
    }
    
    //拿到好友姓名和号码数据
    public void updateFriend(){
        //准备item数据
        SqlExec se=SqlExec.getInstance(this);
        se.create();
        //拿到该用户好友账号
        friendNumbers=se.findFriend(userNumber);
        //拿到该用户好友用户名
        friendNames=se.getTagList(friendNumbers,MySqlite.USER_NAME);
        se.close();
    }
    
}
