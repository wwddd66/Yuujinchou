package com.example.urz_1.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urz_1.R;
import com.example.urz_1.model.User;
import com.example.urz_1.model.UserRelation;
import com.example.urz_1.util.FileUtil;

import org.litepal.LitePal;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> mUserList;
    private User currentUser;//当前登录的用户
    private String currentUsername;
    private Context mContext;

    public UserAdapter(List<User> userList, String username) {
        mUserList = userList;
        this.currentUsername = username;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        final UserViewHolder holder = new UserViewHolder(view);
        mContext = view.getContext();

        holder.userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int position = holder.getAdapterPosition();
                final User user = mUserList.get(position);
                //TODO: 弹出对话框询问是否将其添加至好友列表
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("添加好友")
                        .setMessage("是否添加其为好友？对方的好友列表中不会有你。")
                        .setPositiveButton("确定添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO: 将点击的对象添加至好友列表，并保存至数据库
                                currentUser = LitePal.where("username like ?", currentUsername).findFirst(User.class, true);
                                List<UserRelation> userRelations = LitePal.where("userid like ?", String.valueOf(currentUser.getId())).find(UserRelation.class);
                                boolean isExist = false;
                                for (UserRelation item : userRelations) {
                                    if (item.getFriendId() == user.getId()) {
                                        AlertDialog.Builder subBuilder = new AlertDialog.Builder(v.getContext());
                                        subBuilder.setTitle("提示")
                                                .setMessage("你已经添加对方为好友，请勿重复添加")
                                                .setPositiveButton("好滴", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                        AlertDialog dialog1 = subBuilder.create();
                                        dialog1.setCanceledOnTouchOutside(false);
                                        dialog1.show();
                                        //如果好友列表已存在此人，把标志位设为true，然后跳出循环
                                        isExist = true;
                                        break;
                                    }
                                }
                                //如果标志位为false，则执行讲此好友存库操作
                                if (!isExist) {
                                    UserRelation relation = new UserRelation();
                                    relation.setUserId(currentUser.getId());
                                    relation.setFriendId(user.getId());
                                    relation.save();
                                    Toast.makeText(v.getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("再想想", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user = mUserList.get(position);
        if (user.getImage() == null) {
            holder.rivIcon_item.setImageResource(R.mipmap.l7);
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.l7);
            user.setImage(FileUtil.bitmapToString(bitmap));
            user.update(user.getId());
        } else {
            holder.rivIcon_item.setImageBitmap(FileUtil.stringToBitmap(user.getImage()));
        }

        holder.tvUsername_item.setText(user.getUsername());
        holder.tvNickname_item.setText(user.getNickname());
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
