 package com.example.myqq;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myqq.sql.MySqlite;
import com.example.myqq.sql.SqlExec;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

 public class RegisterActivity extends AppCompatActivity {
     private EditText username,pass,passY;
     private Button zc,date,reset;
     private RadioGroup sex;
     private RadioButton nan;
     private CheckBox yy,lq,sj;
     private Spinner jg;
     //输入框信息
     private String name,pass1,pass2;
     //日期信息
     private int year, month, day;
     private String uSex="男";
     private StringBuilder ah=new StringBuilder();
     private String ujg="重庆";//籍贯信息
    
     private String number;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        //输入框
        username=findViewById(R.id.username);
        pass=findViewById(R.id.pass);
        passY=findViewById(R.id.passY);
        
        //选择框
        sex=findViewById(R.id.sex);
        nan=findViewById(R.id.nan);
        yy=findViewById(R.id.yy);
        lq=findViewById(R.id.lq);
        sj=findViewById(R.id.sj);
    
        //性别选择
        
        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=group.findViewById(checkedId);
                uSex=rb.getText().toString();
                System.out.println(uSex);
            }
        });
    
        //时间选择
        date=findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date.setText(year+"年"+month+"月"+day+"日");
                    }
                });
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(RegisterActivity.this, R.layout.data_dialog, null);
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
                        RegisterActivity.this.year=year;
                        RegisterActivity.this.month=monthOfYear+1;
                        RegisterActivity.this.day=dayOfMonth;
                    }
                });
            }
        });
        
        //籍贯
        jg=findViewById(R.id.jg);
        jg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ujg=getResources().getStringArray(R.array.city)[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        //注册按钮
        zc=findViewById(R.id.zc);
        zc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //输入框部分
                name=username.getText().toString();
                if (name.trim().equals("")){
                    username.setError("不能为空");
                    return;
                }
                pass1=pass.getText().toString();
                pass2=passY.getText().toString();
                if (pass1.equals("")){//密码的判断
                    pass.setError("不能为空");
                    return;
                }else if (pass2.equals("")){
                    passY.setError("不能为空");
                    return;
                }else if (!pass1.equals(pass2)){
                    new Dialog(RegisterActivity.this,"两次密码不同，请确密码！",null,null);
                    return;
                }
                System.out.println(ah.toString());
                //爱好选择
                if (yy.isChecked()){
                    ah.append(yy.getText().toString());
                }
                if (lq.isChecked()){
                    if (!("".equals(ah.toString())))
                        ah.append(",");
                    ah.append(lq.getText().toString());
                }
                if (sj.isChecked()){
                    if (!("".equals(ah.toString())))
                        ah.append(",");
                    ah.append(sj.getText().toString());
                }
                System.out.println(ah.toString());
                //出生日期部分
                if (year==0&&month==0&&day==0){
                    ah.delete(0,ah.length());
                    new Dialog(RegisterActivity.this,"请选择出生日期！",
                            null,null);
                    return;
                }
                
                //添加数据(注册)
                SqlExec se=SqlExec.getInstance(RegisterActivity.this);
                se.create();
                //随机生成一个号码
                number=ran(se);
                boolean add=se.addUser(number,name,pass1,uSex,ah.toString(),
                        year+"/"+month+"/"+day,ujg);
                if (add){//账号注册成功
                    new Dialog(RegisterActivity.this,
                            "注册成功\n" + se.getMess(se.getUserMess(number)).toString(),
                            new Dialog.OnClick() {
                                @Override
                                public void click() {//点击确定，跳转回登录页面
                                    Intent intent = getIntent();
                                    intent.putExtra("number", number);
                                    intent.putExtra("pass", pass1);
                                    setResult(1, intent);
                                    finish();
                                }
                            }, new Dialog.OnClick() {//点击取消清空表单
                        @Override
                        public void click() {
                            reset();
                        }
                    });
                }else{
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println(se.getMess(se.getUserMess(number)).toString());
                se.close();
            }
        });
        
        //重置按钮
        reset=findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Dialog(RegisterActivity.this, "确定清空表单吗？",
                        new Dialog.OnClick() {
                            @Override
                            public void click() {
                                reset();
                            }
                        }, new Dialog.OnClick() {
                    @Override
                    public void click() {
                        Toast.makeText(RegisterActivity.this, "请继续注册！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    
    private void reset(){
        username.setText("");
        pass.setText("");
        passY.setText("");
        nan.setChecked(true);
        yy.setChecked(true);
        lq.setChecked(false);
        sj.setChecked(false);
        jg.setSelection(0,true);
        year=0;
        month=0;
        day=0;
        date.setText("点击选择出生日期");
    }
    
    //为用户生成一个随机账号
    public String ran (SqlExec se){
        String number;
        //随机生成一个号码
        number=String.valueOf((int) (Math.random()*2000000000));
        if (se.findNumber(number)) {return ran(se);}
        return number;
    }
}
