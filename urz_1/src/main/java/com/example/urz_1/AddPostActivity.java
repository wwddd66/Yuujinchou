package com.example.urz_1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {
    private EditText edtPostContent;
    private Button btnPublish;


    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_post);

        iniView();

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postContent = edtPostContent.getText().toString();
                if (postContent.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);//评论的弹出框
                    builder.setTitle("提示");
                    builder.setMessage("嘿！发布内容不能为空噢~");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Date date = new Date();
                    //long times = date.getTime();//时间戳
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = formatter.format(date);

                    intent = new Intent();
                    intent.putExtra("postContent", postContent);
                    intent.putExtra("dateString", dateString);
                    setResult(30, intent);
                    finish();
                }
            }
        });
    }

    void iniView() {
        edtPostContent = findViewById(R.id.edtPostContent);
        btnPublish = findViewById(R.id.btnPublish);

    }
}
