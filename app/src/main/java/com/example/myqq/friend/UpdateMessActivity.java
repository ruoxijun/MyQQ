package com.example.myqq.friend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myqq.Dialog;
import com.example.myqq.R;
import com.example.myqq.RegisterActivity;
import com.example.myqq.sql.MySqlite;
import com.example.myqq.sql.SqlExec;

import java.util.Calendar;
import java.util.Date;

public class UpdateMessActivity extends AppCompatActivity {
    private String userNumber;
    private Intent intent;
    private EditText username;
    private RadioButton nan,nv;
    private CheckBox yy,lq,sj;
    private Button date;
    private Spinner jg;
    private Button update,not;
    private String name,sex,ah,userDate,userJg;
    private String newname,newsex,newah,newuserDate,newuserJg;
    private int year,month,day;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mess);
    
        //初始化
        intent=getIntent();
        userNumber=intent.getStringExtra("userNumber");
        init();
        
        //籍贯选择改变
        jg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] jgs=getResources().getStringArray(R.array.city);
                newuserJg=jgs[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        //生日
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateMessActivity.this);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date.setText(year+"年"+month+"月"+day+"日");
                    }
                });
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(UpdateMessActivity.this, R.layout.data_dialog, null);
                final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
                datePicker.setMaxDate(new Date().getTime());//设置时间上限
                dialog.setTitle("设置日期");
                dialog.setCancelable(false);
                dialog.setView(dialogView);
                dialog.show();
                //初始化日期监听事件
                final Calendar c=Calendar.getInstance();
                datePicker.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        UpdateMessActivity.this.year=year;
                        UpdateMessActivity.this.month=monthOfYear+1;
                        UpdateMessActivity.this.day=dayOfMonth;
                        newuserDate=year+"/"+month+"/"+day;
                    }
                });
            }
        });
        
        //更改按钮按下
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取值更新值
                newname=username.getText().toString();
                if (newname.equals("")){
                    username.setError("不能为空");
                    Toast.makeText(UpdateMessActivity.this, "更改失败",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (nan.isChecked()){
                    newsex=nan.getText().toString();
                }else{
                    newsex=nv.getText().toString();
                }
                StringBuilder s=new StringBuilder();
                if (yy.isChecked()){
                    s.append(yy.getText().toString());
                }
                if (lq.isChecked()){
                    if (s.toString()!="")
                        s.append(",");
                    s.append(lq.getText().toString());
                }
                if (sj.isChecked()){
                    if (s.toString()!="")
                        s.append(",");
                    s.append(sj.getText().toString());
                }
                if (s.toString().equals("")){
                    newah="你猜";
                }else{
                    newah=s.toString();
                }
                newuserDate=date.getText().toString();
                if (!(newname.equals(name)&&newsex.equals(sex)&&newah.equals(ah)
                &&newuserDate.equals(userDate)&&newuserJg.equals(userJg))){
                    //更新数据库数据
                    SqlExec se=SqlExec.getInstance(UpdateMessActivity.this);
                    se.create();
                    se.update(newname,newsex,newah,newuserDate,newuserJg,
                            MySqlite.USER_NUMBER,userNumber);
                    se.close();
                    Toast.makeText(UpdateMessActivity.this, "修改成功",
                            Toast.LENGTH_SHORT).show();
                }else {
                    new Dialog(UpdateMessActivity.this,"你未修改任何数据！",null,null);
                }
            }
        });
        //取消按钮按下
        not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    //初始化组件和显示
    private void init(){
        //获取组件
        username = findViewById(R.id.username);
        nan = findViewById(R.id.nan);
        nv = findViewById(R.id.nv);
        yy = findViewById(R.id.yy);
        lq = findViewById(R.id.lq);
        sj = findViewById(R.id.sj);
        date = findViewById(R.id.date);
        jg = findViewById(R.id.jg);
        update = findViewById(R.id.update);
        not = findViewById(R.id.not);
    
        SqlExec se=SqlExec.getInstance(this);
        se.create();
        Cursor cursor=se.getUserMess(userNumber);
        if(cursor.moveToNext()){
            name=cursor.getString(1);
            sex=cursor.getString(2);
            ah=cursor.getString(3);
            userDate=cursor.getString(4);
            userJg=cursor.getString(5);
            //显示现信息
            username.setText(name);//昵称
            if (nan.getText().toString().equals(sex)){//性别
                nan.setChecked(true);
            }else {
                nv.setChecked(true);
            }
            String[] ahs=ah.split(",");//爱好
            isCheck(ahs,yy);
            isCheck(ahs,lq);
            isCheck(ahs,sj);
            date.setText(userDate);//生日
            String[] jgs=getResources().getStringArray(R.array.city);
            for (int i=0;i<jgs.length;i++){
                if (jgs[i].equals(userJg))
                    jg.setId(i);
            }
        }
        cursor.close();
        se.close();
    }
    
    //初始化时爱好的选择状态
    public void isCheck(String[] a,CheckBox cb){
        for (String s : a) {
            if (cb.getText().toString().equals(s)) {
                cb.setChecked(true);
            }
        }
    }
}