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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.urz_1.MainActivity;
import com.example.urz_1.R;
import com.example.urz_1.RecoverPasswordActivity;
import com.example.urz_1.model.User;
import com.example.urz_1.shape.RoundImageView;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import org.litepal.LitePal;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment {
    private RoundImageView rivMineIcon;
    private TextView tvModifyNickname, tvModifyIcon, tvSwitchAccount, tvMineNickname, tvModifyPwd;
    private TextView tvChoosePhoto, tvCancel;

    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private View layout;
    private static String key_username = "username";
    private String currentUsername;
    private Intent intent;
    private User user;


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
            user = LitePal.where("username like ?", currentUsername).findFirst(User.class);
            tvMineNickname.setText(user.getNickname());
        }


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
                                    String str = edt.getText().toString();
                                    tvMineNickname.setText(str);
                                    user.setNickname(str);
                                    user.save();
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
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

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
}
