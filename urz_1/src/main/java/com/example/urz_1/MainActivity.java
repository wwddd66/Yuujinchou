package com.example.urz_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.urz_1.model.User;

import org.litepal.LitePal;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUsername, edtPassword;
    private CheckBox cbRememberMe, cbAutoLogin;
    private Button btnLogin;
    private TextView tvRegister, tvForgetPassword;

    private Intent intent;
    private SharedPreferences pref;//临时保存上一次登录的账号和密码，记录“记住密码”和“自动登录”的选中状态
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        initView();

        //使用LitePal创建数据库urz.db
        SQLiteDatabase db = LitePal.getDatabase();


        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_pwd", false);
        boolean isAutoLogin = pref.getBoolean("auto_login", false);

        Intent intentMineFragment = getIntent();
        boolean auto = intentMineFragment.getBooleanExtra("auto", false);
        isAutoLogin = auto;

        //如果选择了记住密码，下次登录时账号和密码会自动添加
        if (isAutoLogin) {
            String username = pref.getString("username", "");
            String pwd = pref.getString("pwd", "");
            edtUsername.setText(username);
            edtPassword.setText(pwd);
            cbAutoLogin.setChecked(true);

            //如果选择了自动登录，直接跳转到登陆后的界面
            List<User> users = LitePal.findAll(User.class);
            for (User user : users) {
                if (username.equals(user.getUsername()) && pwd.equals(user.getPassword())) {
                    intent = new Intent(MainActivity.this, LoggedActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                    break;
                }
            }
        } else if (isRemember) {
            String username = pref.getString("username", "");
            String pwd = pref.getString("pwd", "");
            edtUsername.setText(username);
            edtPassword.setText(pwd);
            cbRememberMe.setChecked(true);
        }

        //登录功能实现
        btnLogin.setOnClickListener(this);

        //注册功能的实现
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        //找回密码功能实现
        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, RecoverPasswordActivity.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    void initView() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        cbAutoLogin = findViewById(R.id.cbAutoLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);

    }


    //此函数仅用作登录功能
    @Override
    public void onClick(View v) {
        String username = edtUsername.getText().toString();
        String pwd = edtPassword.getText().toString();
        boolean flag = false;

        //账号和密码都匹配的情况下，跳转到登陆后的主界面LoggedActivity
        List<User> users = LitePal.findAll(User.class);
        for (User user : users) {
            if (username.equals(user.getUsername()) && pwd.equals(user.getPassword())) {
                flag = true;
                editor = pref.edit();
                editor.putString("username", username);
                editor.putString("pwd", pwd);
                if (cbAutoLogin.isChecked()) {
                    editor.putBoolean("auto_login", true);
                } else if (cbRememberMe.isChecked()) {
                    editor.putBoolean("remember_pwd", true);
                } else {
                    editor.clear();
                }
                editor.apply();

                intent = new Intent(MainActivity.this, LoggedActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
                break;
            }
        }
        if (!flag) {
            Toast.makeText(MainActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    //处理Intent返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1://注册后，返回账号和密码
                if (resultCode == 10) {
                    String username = data.getStringExtra("username");
                    String pwd = data.getStringExtra("pwd");
                    edtUsername.setText(username);
                    edtPassword.setText(pwd);
                }
                break;
            case 2://修改密码后，返回账号
                if (resultCode == 20) {
                    String username = data.getStringExtra("username");
                    edtUsername.setText(username);
                    edtPassword.setText("");
                }
                break;
        }

    }
}
