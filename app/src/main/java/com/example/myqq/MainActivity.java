package com.example.myqq;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.example.myqq.friend.FriendActivity;
import com.example.myqq.sql.MySqlite;
import com.example.myqq.sql.SqlExec;


public class MainActivity extends AppCompatActivity {
    private TextView register;
    private Button start;
    private EditText user,pass;
    private SharedPreferences sp;
    private SharedPreferences.Editor e;
    private CheckBox rem;
    private String userNumber,userPass,userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //账号输入框
        user=findViewById(R.id.user);
        //密码输入框
        pass=findViewById(R.id.pass);
        
        //注册按钮
        register=findViewById(R.id.register);
        register.setOnClickListener(new onclick());
        //登录按钮
        start=findViewById(R.id.start);
        start.setOnClickListener(new onclick());
        
        //记住密码设置
        rem=findViewById(R.id.rem);
        sp=getSharedPreferences("userdata",MODE_PRIVATE);
        e = sp.edit();
        if (sp.getBoolean("check",false)){
            rem.setChecked(true);
            user.setText(sp.getString("user",""));
            pass.setText(sp.getString("pass",""));
        }
        rem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    e.putBoolean("check",true);
                    e.apply();
                }else{
                    e.putBoolean("check",false);
                    e.putString("user","");
                    e.putString("pass","");
                    e.apply();
                }
            }
        });
    }
    //点击事件实现
    class onclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                //注册点击事件
                case R.id.register:
                    intent=new Intent(MainActivity.this,RegisterActivity.class);
                    startActivityForResult(intent,0);
                    break;
                    //登录点击事件
                case R.id.start:
                    //获取输入框数据
                    userNumber=user.getText().toString();
                    userPass=pass.getText().toString();
                    //判断某输入框为空时
                    if (userNumber.equals("")&&userPass.equals("")){
                        new Dialog(MainActivity.this,"账号和密码不能为空！",null,null);
                        user.setError("不能为空");
                        pass.setError("不能为空");
                        break;
                    } else if (userNumber.equals("")){
                        new Dialog(MainActivity.this,"账号不能为空！",null,null);
                        user.setError("不能为空");
                        break;
                    }else if (userPass.equals("")){
                        new Dialog(MainActivity.this,"密码不能为空！",null,null);
                        pass.setError("不能为空");
                        break;
                    }
                    //账号密码验证
                    SqlExec se=SqlExec.getInstance(MainActivity.this);
                    se.create();
                    Cursor c=se.query("select "+MySqlite.USER_NUMBER+","+MySqlite.USER_PASSWORD
                            +","+MySqlite.USER_NAME+" from "+MySqlite.USER_TABLE
                                    +" where "+MySqlite.USER_NUMBER+"="+userNumber+" and "+MySqlite.USER_PASSWORD+"="+userPass);
                    System.out.println("外");
                    if(!c.moveToNext()){//账号或密码有误
                        System.out.println("yes");
                        new Dialog(MainActivity.this, "账号或密码有误！"
                                ,null,new Dialog.OnClick() {
                            @Override
                            public void click() {
                                user.setText("");
                                pass.setText("");
                            }
                        });
                        break;
                    }else{//账号正确跳转
                        System.out.println("no");
                        //是否选择了记住密码
                        if (sp.getBoolean("check",false)) {
                            e.putString("user", user.getText().toString());
                            e.putString("pass", pass.getText().toString());
                            e.apply();
                        }
                        userName=c.getString(2);
                        intent=new Intent(MainActivity.this, FriendActivity.class);
                        intent.putExtra("userNumber",userNumber);//传值（账号）
                        intent.putExtra("userName",userName);//传值（账号名）
                    }
                    c.close();
                    se.close();
                    startActivity(intent);
                    break;
            }
        }
    }
    
    @Override//注册返回时，接受返回值
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            user.setText(data.getStringExtra("number"));
            pass.setText(data.getStringExtra("pass"));
        }
    }
}
