package com.example.urz_1.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urz_1.R;
import com.example.urz_1.model.User;

import org.litepal.LitePal;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> mUserList;
    private User currentUser;//当前登录的用户
    private String currentUsername;


    public UserAdapter(List<User> userList, String username) {
        mUserList = userList;
        this.currentUsername = username;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        final UserViewHolder holder = new UserViewHolder(view);
        holder.userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final User user = mUserList.get(position);
                //TODO: 弹出对话框询问是否将其添加至好友列表（...）
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("添加好友")
                        .setMessage("是否添加其为好友？对方的好友列表中不会有你。")
                        .setPositiveButton("确定添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO: 将点击的对象添加至好友列表，并保存至数据库
                                currentUser = LitePal.where("username like ?", currentUsername).findFirst(User.class, true);

                                boolean isExist = false;
                                for (int i = 0; i < currentUser.getUserList().size(); i++) {
                                    if (user.equals(currentUser.getUserList().get(i))) {//好友列表已存在此人
                                        AlertDialog.Builder subBuilder = new AlertDialog.Builder(view.getContext());
                                        subBuilder.setTitle("提示")
                                                .setMessage("你已经添加对方为好友，请勿重复添加")
                                                .setPositiveButton("好滴", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                        isExist = true;
                                        return;
                                    }
                                }
                                if (!isExist) {
                                    if (currentUser.getUserList().add(user)) {
                                        currentUser.setUserList(currentUser.getUserList());
                                    }

                                }
                            }
                        })
                        .setNegativeButton("再想想", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.rivIcon_item.setImageResource(R.mipmap.l3);
        holder.tvUsername_item.setText(user.getUsername());
        holder.tvNickname_item.setText(user.getNickname());
        if (position == 0) {
            currentUser = user;
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


    static class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView rivIcon_item;
        private TextView tvUsername_item, tvNickname_item;
        private View userView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userView = itemView;
            rivIcon_item = itemView.findViewById(R.id.rivIcon_item);
            tvUsername_item = itemView.findViewById(R.id.tvUsername_item);
            tvNickname_item = itemView.findViewById(R.id.tvNickname_item);
        }
    }

}
