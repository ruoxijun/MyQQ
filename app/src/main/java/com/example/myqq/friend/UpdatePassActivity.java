package com.example.myqq.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.EventBridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myqq.Dialog;
import com.example.myqq.R;
import com.example.myqq.sql.MySqlite;
import com.example.myqq.sql.SqlExec;

public class UpdatePassActivity extends AppCompatActivity {
    private String userNumber;
    private String thisUserPass;
    private EditText userPass,pass,passY;
    private Button update,not;
    private String userPasss,passs,passYs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass);
        
        Intent intent=getIntent();
        userNumber=intent.getStringExtra("userNumber");
        
        init();
        //更改按钮被按下
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从数据库获取此用户密码
                SqlExec se=SqlExec.getInstance(UpdatePassActivity.this);
                se.create();
                Cursor cursor=se.findTableTag(MySqlite.USER_TABLE,MySqlite.USER_PASSWORD
                        ,MySqlite.USER_NUMBER,userNumber);
                if (cursor.moveToNext()){
                    thisUserPass=cursor.getString(0);
                }
                cursor.close();
                se.close();
                
                //拿到输入数据
                userPasss=userPass.getText().toString();
                passs=pass.getText().toString();
                passYs=passY.getText().toString();
                //判空
                if (userPasss.equals("")){
                    userPass.setError("不能为空");
                    return;
                }
                if (passs.equals("")){
                    pass.setError("不能为空");
                    return;
                }
                if (passYs.equals("")){
                    passY.setError("不能为空");
                    return;
                }
                //两次密码判断是否一致
                if (!passs.equals(passYs)){
                    passY.setError("两次密码不一致");
                    return;
                }
                //原密码是否正确
                if (!thisUserPass.equals(userPasss)){
                    userPass.setError("原密码不正确");
                    return;
                }
                //新密码与原密码相同
                if (thisUserPass.equals(passYs)){
                    pass.setError("新密码不能与原密码相同");
                    return;
                }
                SqlExec sec=SqlExec.getInstance(UpdatePassActivity.this);
                sec.create();
                sec.exec("update "+MySqlite.USER_TABLE+" set "+ "password='"+passYs+
                        "' where " +MySqlite.USER_NUMBER+"='"+userNumber+"'");
                sec.close();
                Toast.makeText(UpdatePassActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                new Dialog(UpdatePassActivity.this, "是否需要记住密码,\n方便您的下一次登录。",
                        new Dialog.OnClick() {
                            @Override
                            public void click() {
                                SharedPreferences sp=getSharedPreferences("userdata",MODE_PRIVATE);
                                SharedPreferences.Editor e = sp.edit();
                                e.putString("pass",passYs);
                                e.apply();
                            }
                        }, new Dialog.OnClick() {
                    @Override
                    public void click() {
                        finish();
                    }
                });
            }
        });
        
        //点击取消时返回上页面
        not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    //初始化
    private void init(){
        userPass=findViewById(R.id.userPass);
        pass=findViewById(R.id.pass);
        passY=findViewById(R.id.passY);
        update=findViewById(R.id.update);
        not=findViewById(R.id.not);
    }
}