package com.example.myqq;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//自定义消息框
public class Dialog {
    private AlertDialog ad;
    private AlertDialog.Builder d;
    
    public interface OnClick{
        void click();
    }
    
    //context:上下文，con:消息，click:回调事件,可为null
    public Dialog(Context context, String con, final OnClick yclick, final OnClick nclick){
        //获取并准备组件
        d=new AlertDialog.Builder(context);
        final View view=LayoutInflater.from(context).inflate(R.layout.my_dialog,null);
        TextView textView=view.findViewById(R.id.con);
        Button no=view.findViewById(R.id.no);
        Button yes=view.findViewById(R.id.yes);
        //设置消息
        textView.setText(con);
        //确定按钮点击事件
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yclick!=null){
                    yclick.click();
                }
                ad.dismiss();
            }
        });
        //取消按钮回调事件
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nclick!=null){
                    nclick.click();
                }
                ad.dismiss();
            }
        });
        ad=d.setView(view).create();
        ad.show();
    }
}