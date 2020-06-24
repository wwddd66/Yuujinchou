package com.example.urz_1.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import java.util.ArrayList;
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
        currentUser = LitePal.where("username like ?", currentUsername).findFirst(User.class, true);

        holder.userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int position = holder.getAdapterPosition();
                final User user = mUserList.get(position);
                showBottomDialog(user);
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

    private void showBottomDialog(final User user) {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(mContext, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(mContext, R.layout.dialog_custom_layout, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //TODO: 弹出对话框询问是否将其添加至好友列表
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("添加好友")
                        .setMessage("是否添加其为好友？对方的好友列表中不会有你。")
                        .setPositiveButton("确定添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO: 将点击的对象添加至好友列表，并保存至数据库

                                List<UserRelation> userRelations = LitePal.where("userid like ?", String.valueOf(currentUser.getId())).find(UserRelation.class);
                                boolean isExist = false;
                                for (UserRelation item : userRelations) {
                                    if (item.getFriendId() == user.getId()) {
                                        AlertDialog.Builder subBuilder = new AlertDialog.Builder(view.getContext());
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
                                    Toast.makeText(view.getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("再想想", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog1 = builder.create();
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());//提示是否删除的弹出框
                builder.setTitle("即将进行的操作")
                        .setMessage("是否删除该好友(单向删除)")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User _user = user;
                                List<UserRelation> userRelations = new ArrayList<>();
                                userRelations.addAll(LitePal.where("userid like ?", String.valueOf(currentUser.getId())).find(UserRelation.class));
                                /*List<User> userList = new ArrayList<>();
                                for (UserRelation item : userRelations) {
                                    userList.addAll(LitePal.select("username", "nickname", "image").where("id like ?", String.valueOf(item.getFriendId())).find(User.class));
                                }*/
                                for (UserRelation item : userRelations) {
                                    if (_user.getId() == item.getFriendId() && _user.getId() != currentUser.getId()) {
                                        item.delete();
                                        Toast.makeText(view.getContext(), "删除成功，刷新一下试试", Toast.LENGTH_SHORT).show();
                                        break;
                                        //TODO: 更新好友列表
                                    } else {
                                        Toast.makeText(view.getContext(), "我删我自己？哈哈哈。那可不行！(没有好友啦，快在[发现]加点好友吧！)", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog1 = builder.create();
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


}
