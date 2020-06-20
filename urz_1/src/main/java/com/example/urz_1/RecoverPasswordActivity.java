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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecoverPasswordActivity extends AppCompatActivity {
    private ImageView ivBack;
    private EditText edtRPUsername, edtRPMail, edtRPNewPwd;
    private Button btnRP;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recoverpassword);
        initView();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(RecoverPasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = edtRPUsername.getText().toString();
                final String email = edtRPMail.getText().toString();
                String newPwd = edtRPNewPwd.getText().toString();
                boolean flag = false;
                //从数据库中检测账号是否存在，邮箱是否正确
                List<User> users = LitePal.findAll(User.class);
                for (User user : users) {
                    if (username.equals(user.getUsername()) && email.equals(user.getEmail())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    Toast.makeText(RecoverPasswordActivity.this, "账号不存在或邮箱不正确，请核对后重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }

                //检测密码的合法性
                Pattern patternPwd = Pattern.compile("^[\f\n\r\t ]*$");//匹配任何空白字符
                Matcher matcherPwd = patternPwd.matcher(newPwd);
                if (matcherPwd.find()) {//密码不能包含换页、换行、回车、空格等空白字符
                    Toast.makeText(RecoverPasswordActivity.this, "密码包含非法字符或长度不足", Toast.LENGTH_SHORT).show();
                    return;
                }

                //账号、邮箱和密码都检测合格以后，在这里调用数据库更新对应账号的密码
                User userToUpdate = new User();
                userToUpdate.setPassword(newPwd);
                userToUpdate.updateAll("username=?", username);

                //返回登陆界面，将账号的值带回
                intent = new Intent();
                intent.putExtra("username", username);
                setResult(20, intent);

                //启动MainActivity，主要在修改密码时使用
                intent = new Intent(RecoverPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    void initView() {
        ivBack = findViewById(R.id.ivBack);
        edtRPUsername = findViewById(R.id.edtRPUsername);
        edtRPMail = findViewById(R.id.edtRPMail);
        edtRPNewPwd = findViewById(R.id.edtRPNewPwd);
        btnRP = findViewById(R.id.btnRP);

    }
}
