package com.example.urz_1.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import com.example.urz_1.util.FileUtil;

import org.litepal.LitePal;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment {
    private RoundImageView rivMineIcon;
    private TextView tvModifyNickname, tvModifyIcon, tvSwitchAccount, tvMineNickname, tvModifyPwd;
    private Spinner spinnerViewFriends;
    private ArrayAdapter<String> adapter;

    private AlertDialog.Builder builder;
    private static String key_username = "username";//从LoggedActivity.java传值的key
    private String currentUsername;//当前登录用户的用户名
    private Intent intent;
    private User currentUser;//当前登录用户
    private List<UserRelation> userRelations;
    private Context mContext;//当前布局的上下文

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Uri imageUri;


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
        mContext = getContext();
        //获取从 LoggedActivity 传递的username
        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.d("error", "MineBundle为空");
        } else {
            currentUsername = bundle.getString(key_username, "username");
            currentUser = LitePal.where("username like ?", currentUsername).findFirst(User.class);
            tvMineNickname.setText(currentUser.getNickname());

        }

        read();//获取头像

        initFriendList();//初始化好友列表
        spinnerViewFriends.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pos = adapter.getItem(position);
                if (!"请选择:".equals(pos)) {
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


        //修改头像
        tvModifyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
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
                intent.putExtra("auto", "1000");
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
        rivMineIcon.setDrawingCacheEnabled(true);
        tvModifyNickname = getActivity().findViewById(R.id.tvModifyNickname);
        tvModifyIcon = getActivity().findViewById(R.id.tvModifyIcon);
        tvSwitchAccount = getActivity().findViewById(R.id.tvSwitchAccount);
        tvMineNickname = getActivity().findViewById(R.id.tvMineNickname);
        tvModifyPwd = getActivity().findViewById(R.id.tvModifyPwd);
        spinnerViewFriends = getActivity().findViewById(R.id.spinnerViewFriends);
    }


    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        //TODO:
                        rivMineIcon.setImageBitmap(bitmap);
                        write(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitkat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKathy(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
            //如果是document 类型Uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri ，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接使用普通的方式处理
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
    }

    private void handleImageBeforeKitKathy(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            rivMineIcon.setImageBitmap(bitmap);
            write(bitmap);
        } else {
            Toast.makeText(getActivity(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }


    private void initFriendList() {
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

    //获取头像
    private void read() {
        String image = currentUser.getImage();
        if (image == null) {//当前用户无头像
            /**
             * 设置默认头像
             */
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.l7);
            String imageString = FileUtil.bitmapToString(bitmap);
            // 将String保持至数据库
            currentUser.setImage(imageString);
            currentUser.update(currentUser.getId());
            //显示头像
            rivMineIcon.setImageBitmap(bitmap);
        } else {
            Bitmap bitmap = FileUtil.stringToBitmap(image);
            rivMineIcon.setImageBitmap(bitmap);//显示更新后的头像
        }

    }

    //保存头像
    private void write(Bitmap bitmap) {
        String image = FileUtil.bitmapToString(bitmap);
        // 第三步:将String保持至数据库
        currentUser.setImage(image);
        currentUser.update(currentUser.getId());
    }

}
