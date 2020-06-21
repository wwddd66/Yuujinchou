package com.example.urz_1.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.urz_1.MainActivity;
import com.example.urz_1.R;
import com.example.urz_1.RecoverPasswordActivity;
import com.example.urz_1.model.User;
import com.example.urz_1.model.UserRelation;
import com.example.urz_1.shape.RoundImageView;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment {
    private RoundImageView rivMineIcon;
    private TextView tvModifyNickname, tvModifyIcon, tvSwitchAccount, tvMineNickname, tvModifyPwd;
    private TextView tvChoosePhoto, tvCancel;
    private Spinner spinnerViewFriends;
    private ArrayAdapter<String> adapter;

    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private View layout;
    private static String key_username = "username";
    private String currentUsername;
    private Intent intent;
    private User currentUser;
    private List<UserRelation> userRelations;


    public MineFragment() {
        // Required empty public constructor
    }

    public static MineFragment getInstance(String username) {
        Bundle bundle = new Bundle();
        bundle.putString(key_username, username);
        MineFragment mineFragment = new MineFragment();
        mineFragment.setArguments(bundle);
        return mineFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

        //获取从 LoggedActivity 传递的username
        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.d("error", "MineBundle为空");
        } else {
            currentUsername = bundle.getString(key_username, "username");
            currentUser = LitePal.where("username like ?", currentUsername).findFirst(User.class);
            tvMineNickname.setText(currentUser.getNickname());
        }

        initFriendList();//初始化好友列表
        spinnerViewFriends.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pos = adapter.getItem(position);
                if ("请选择:".equals(pos)) {

                } else {
                    final String _username = pos.split("（", 2)[1].split("）", 2)[0];
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//提示是否删除的弹出框
                    builder.setTitle("即将进行的操作")
                            .setMessage("是否删除该好友(单向删除)")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    User _user = LitePal.where("username like ?", _username).findFirst(User.class, true);
                                    for (UserRelation item : userRelations) {
                                        if (_user.getId() == item.getFriendId() && _user.getId() != currentUser.getId()) {
                                            item.delete();
                                            initFriendList();
                                        } else {
                                            Toast.makeText(getContext(), "我删我自己，哈哈哈。当然不能删除自己啦！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    parent.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setVisibility(View.VISIBLE);
            }
        });


        //修改头像（未保存至数据库）
        tvModifyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(getContext());//创建对话框
                inflater = getLayoutInflater();
                layout = inflater.inflate(R.layout.dialog_select_photo, null);//获取自定义布局
                builder.setView(layout);//设置对话框的布局
                dialog = builder.create();//生成最终的对话框
                dialog.show();//显示对话框
                tvChoosePhoto = layout.findViewById(R.id.photograph);
                tvCancel = layout.findViewById(R.id.photo);

                //  6.0之后动态申请权限 SD卡写入权限
                if (ContextCompat.checkSelfPermission(getView().getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getView().getContext(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_ADD_CASE_CALL_PHONE2);

                } else {
                    //打开相册
                    choosePhoto();
                }
                dialog.dismiss();
            }
        });

        //修改昵称
        tvModifyNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // final User user = LitePal.where("username like ?", currentUsername).find(User.class).get(0);
                builder = new AlertDialog.Builder(getContext());//评论的弹出框
                final EditText edt = new EditText(getContext());
                builder.setTitle("修改昵称")
                        .setView(edt)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!"".equals(edt.getText().toString())) {
                                    currentUser = LitePal.where("username like ?", currentUsername).findFirst(User.class);
                                    String str = edt.getText().toString();
                                    tvMineNickname.setText(str);
                                    currentUser.setNickname(str);
                                    currentUser.update(currentUser.getId());
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //修改密码
        tvModifyPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(getContext());//评论的弹出框
                builder.setTitle("警告");
                builder.setMessage("即将进入修改密码界面，无论密码是否有变动，都将重新登陆！确定要修改密码吗？");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                intent = new Intent(getActivity(), RecoverPasswordActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();

            }
        });

        //切换账号（退出到登陆界面重新选择账号登录）
        tvSwitchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mine, container, false);
    }

    private void initView() {
        rivMineIcon = getActivity().findViewById(R.id.rivMineIcon);
        tvModifyNickname = getActivity().findViewById(R.id.tvModifyNickname);
        tvModifyIcon = getActivity().findViewById(R.id.tvModifyIcon);
        tvSwitchAccount = getActivity().findViewById(R.id.tvSwitchAccount);
        tvMineNickname = getActivity().findViewById(R.id.tvMineNickname);
        tvModifyPwd = getActivity().findViewById(R.id.tvModifyPwd);
        spinnerViewFriends = getActivity().findViewById(R.id.spinnerViewFriends);
    }

    /**
     * 打开相册
     */
    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, 42);
    }

    /**
     * 申请权限回调方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_ADD_CASE_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                //"权限拒绝";
                // TODO: 这里可以给用户一个提示,请求权限被拒绝了
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * startActivityForResult执行后的回调方法，接收返回的图片
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 42:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    try {
                        Uri selectedImage = data.getData();//获取路径
                        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                        Tiny.getInstance().source(selectedImage).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                            @Override
                            public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                                saveImageToServer(bitmap, outfile);
                            }
                        });
                    } catch (Exception e) {
                        //"上传失败";
                    }
                }
                break;
        }

    }

    private void saveImageToServer(final Bitmap bitmap, String outfile) {
        File file = new File(outfile);
        // TODO: 这里就可以将图片文件 file 上传到服务器,上传成功后可以将bitmap设置给你对应的图片展示
        rivMineIcon.setImageBitmap(bitmap);
    }

    void initFriendList() {
        userRelations = new ArrayList<>();
        userRelations.addAll(LitePal.where("userid like ?", String.valueOf(currentUser.getId())).find(UserRelation.class));
        List<User> userList = new ArrayList<>();
        for (UserRelation item : userRelations) {
            userList.addAll(LitePal.select("username", "nickname").where("id like ?", String.valueOf(item.getFriendId())).find(User.class));
        }
        String[] temp = new String[userList.size() + 1];
        temp[0] = "请选择:";
        int i = 1;
        for (User item : userList) {
            String temp0 = item.getUsername();
            String temp1 = item.getNickname();
            temp[i++] = temp1 + "（" + temp0 + "）";
        }
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, temp);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerViewFriends.setAdapter(adapter);
    }
}
