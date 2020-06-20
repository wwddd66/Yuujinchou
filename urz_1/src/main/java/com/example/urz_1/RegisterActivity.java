package com.example.urz_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.urz_1.model.User;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtRegisterUsername, edtRegisterPassword, edtRegisterPasswordAgain, edtMail;
    private Button btnRegister;
    private ImageView ivBack;

    private Intent intent;
    /*private SQLiteDatabase db;
    private MyDatabaseHelper dbHelper;*/
    //private List<User> users = LitePal.findAll(User.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);
        initView();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtRegisterUsername.getText().toString();
                String pwd = edtRegisterPassword.getText().toString();
                String email = edtMail.getText().toString();
                //检测用户名是否合法或已经存在
                Pattern patternUsername = Pattern.compile("^[a-zA-Z_]+[a-zA-Z0-9_]*$");
                Matcher matcherUsername = patternUsername.matcher(username);
                if (!matcherUsername.find()) {//账号应该以字母或下划线开头，且长度至少为2
                    Toast.makeText(RegisterActivity.this, "账号包含非法字符", Toast.LENGTH_SHORT).show();
                    return;
                } else {//用户名已存在（与数据库中的用户表对比）
                    List<User> users = LitePal.findAll(User.class);
                    for (User user : users) {
                        if (username.equals(user.getUsername())) {
                            Toast.makeText(RegisterActivity.this, "账号已存在，请重新输入", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                }
                //检测密码的合法性
                Pattern patternPwd = Pattern.compile("^[\f\n\r\t ]*$");//匹配任何空白字符
                Matcher matcherPwd = patternPwd.matcher(pwd);
                if (matcherPwd.find()) {//密码不能包含换页、换行、回车、空格等空白字符
                    Toast.makeText(RegisterActivity.this, "密码包含非法字符或长度不足", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!pwd.equals(edtRegisterPasswordAgain.getText().toString())) {//判断两次输入的密码是否一致
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不同，请核对后重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                //检测邮箱的合法性
                // 判断邮箱是否符合正则表达式：xxx@xx.xx
                Pattern patternMail = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+$");
                Matcher matcherMail = patternMail.matcher(email);
                if (!matcherMail.find()) {
                    Toast.makeText(RegisterActivity.this, "邮箱不合法，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }

                //账号、邮箱和密码都检测合格以后，在这里调用数据库更新对应账号的密码
                User user = new User();
                user.setUsername(username);
                user.setNickname(username);//注册时，默认将用户名作为昵称
                user.setPassword(pwd);
                user.setEmail(email);
                List<User> users = new ArrayList<>();
                users.add(user);
                user.setUserList(users);
                user.save();

                //返回登陆界面，将账号和密码的值带回
                intent = new Intent();
                intent.putExtra("username", username);
                intent.putExtra("pwd", pwd);
                setResult(10, intent);
                finish();
            }
        });
    }

    void initView() {
        edtRegisterUsername = findViewById(R.id.edtRegisterUsername);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        edtRegisterPasswordAgain = findViewById(R.id.edtRegisterPasswordAgain);
        edtMail = findViewById(R.id.edtMail);
        btnRegister = findViewById(R.id.btnRegister);
        ivBack = findViewById(R.id.ivBack);
    }
}
