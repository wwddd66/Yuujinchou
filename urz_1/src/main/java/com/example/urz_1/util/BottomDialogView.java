package com.example.urz_1.util;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.example.urz_1.R;


/**
 * 自定义底部弹出对话框
 * Created by zhaomac on 2017/9/8.
 */

public class BottomDialogView extends Dialog {

    private boolean isCancelable;//控制点击dialog外部是否dismiss
    private boolean isBackCancelable = true;//控制返回键是否dismiss
    private View view;
    private Context context;

    //这里的view其实可以替换直接传layout过来的
    public BottomDialogView(Context context, View view, boolean isCancelable, @Nullable boolean isBackCancelable) {
        super(context, R.style.MyDialog);

        this.context = context;
        this.view = view;
        this.isCancelable = isCancelable;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);//这行一定要写在前面
        setCancelable(isCancelable);//点击外部不可dismiss
        setCanceledOnTouchOutside(isBackCancelable);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
}

