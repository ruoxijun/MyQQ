package com.example.myqq.friend;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myqq.R;
import com.example.myqq.sql.SqlExec;

import java.util.ArrayList;

public class PossibleFriendAdapter extends RecyclerView.Adapter<PossibleFriendAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> userNames;
    private ArrayList<String> userNumbers;
    private String userNumber;
    public PossibleFriendAdapter (Context context,ArrayList<String> userNumbers,
                                  ArrayList<String> userNames, String userNumber){
        this.context=context;
        this.userNumbers=userNumbers;
        this.userNames=userNames;
        this.userNumber=userNumber;
    }
    @NonNull
    @Override
    public PossibleFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.possible_friend,parent,false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull final PossibleFriendAdapter.ViewHolder holder, final int position) {
        holder.name.setText(userNames.get(position));
        holder.number.setText(userNumbers.get(position));
        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //数据库添加好友数据
                SqlExec se=SqlExec.getInstance(context);
                se.create();
                boolean add=se.addFriend(userNumber,userNumbers.get(position));
                se.close();
                if (add){
                    Toast.makeText(context, "添加好友  "+userNames.get(position)+"  成功"
                            , Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "添加好友失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                //更新用户好友视图
                FriendActivity.updateFriend(userNames.get(position),userNumbers.get(position));
                //移除数据并更新视图
                userNames.remove(position);
                userNumbers.remove(position);
                notifyDataSetChanged();
            }
        });
        
    }
    @Override
    public int getItemCount() {
        return userNumbers.size()>=8?8:userNumbers.size();
    }
    
    //视图管理器
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView number;
        Button addFriend;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            number=itemView.findViewById(R.id.number);
            addFriend=itemView.findViewById(R.id.addFriend);
            view=itemView.findViewById(R.id.view);
        }
    }
}
