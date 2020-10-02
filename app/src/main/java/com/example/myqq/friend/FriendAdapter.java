package com.example.myqq.friend;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myqq.Dialog;
import com.example.myqq.R;
import com.example.myqq.sql.SqlExec;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {
    private PopupWindow pop;
    private ArrayList<String> friendNames;
    private ArrayList<String> friendNumbers;
    private Context context;
    private String userNumber;
    
    //初始化
    public FriendAdapter(Context context, ArrayList<String> friendNames,
                         ArrayList<String> friendNumbers,String userNumber) {
        this.context=context;
        this.friendNames=friendNames;
        this.friendNumbers=friendNumbers;
        this.userNumber=userNumber;
    }
    //单独设置数据
    public void setData(ArrayList<String> friendNames, ArrayList<String> friendNumbers){
        this.friendNames=friendNames;
        this.friendNumbers=friendNumbers;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override//将视图绑定
    public FriendAdapter.FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendHolder(LayoutInflater.from(context)
                .inflate(R.layout.friend,parent,false));
}

    @Override//对视图上的组件操作
    public void onBindViewHolder(@NonNull final FriendAdapter.FriendHolder holder, final int position) {
        holder.name.setText(friendNames.get(position));
        holder.number.setText(friendNumbers.get(position));
        //长按某好友时弹出菜单
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //组件准备
                View view=LayoutInflater.from(context).inflate(R.layout.my_popup,null);
                final Button in=view.findViewById(R.id.in);
                final Button de=view.findViewById(R.id.de);
                final Button can=view.findViewById(R.id.can);
                //查看好友详情信息
                in.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SqlExec se=SqlExec.getInstance(context);//准备操作数据库
                        se.create();
                        String fn=friendNumbers.get(position);
                        StringBuilder sb=se.getMess(se.getUserMess(fn));
                        new Dialog(context,sb.toString(),null,null);
                        se.close();//关闭数据库
                        pop.dismiss();
                    }
                });
                //删除好友
                de.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除数据库好友信息
                        SqlExec se=SqlExec.getInstance(context);
                        se.create();
                        boolean delete=se.deleteFriend(userNumber,friendNumbers.get(position));
                        se.close();
                        if (delete){
                            Toast.makeText(context, "删除好友  "+friendNames.get(position)+"  成功",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "删除好友失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //移除好友列表数据
                        FriendActivity.deleteFriend(friendNames.get(position),friendNumbers.get(position));
                        notifyDataSetChanged();//整个视图全部刷新
                        pop.dismiss();
                    }
                });
                //取消按钮
                can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyDataSetChanged();
                        pop.dismiss();
                    }
                });
                //长按菜单设置
                pop=new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                //想PopupWindow点击外侧时消失需要设置一个背景，才能成功
                pop.setBackgroundDrawable(new BitmapDrawable());
                pop.setFocusable(true);//获取焦点
                pop.setOutsideTouchable(true);//点击外侧消失
                pop.showAtLocation(view,Gravity.CENTER,0,0);//居中
                return true;
            }
        });
    }

    @Override//设置item的个数
    public int getItemCount() {
        return friendNames.size()<friendNumbers.size()?friendNames.size():friendNumbers.size();
    }
    
    //管理组件内部类
    class FriendHolder extends RecyclerView.ViewHolder{
        private ImageView hImg;
        private TextView name;
        private TextView number;
        private ViewGroup view;
        
        public FriendHolder(@NonNull View itemView) {
            super(itemView);
            hImg=itemView.findViewById(R.id.hImg);
            name=itemView.findViewById(R.id.name);
            number=itemView.findViewById(R.id.number);
           view=itemView.findViewById(R.id.item);
        }
    }
}
