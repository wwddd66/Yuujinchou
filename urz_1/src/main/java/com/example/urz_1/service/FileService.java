package com.example.urz_1.service;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileService {

    private Context context;

    public FileService(Context context) {
        this.context = context;
    }

    public FileService() {

    }

    public byte[] getFileFromSdcard(String fileName) {
        FileInputStream inputStream = null;
        // 缓存的流，和磁盘无关，不需要关闭
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        File file = new File(Environment.getExternalStorageDirectory(),
                fileName);
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                inputStream = new FileInputStream(file);
                int len = 0;
                byte[] data = new byte[1024];
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }

        return  outputStream.toByteArray();
    }

    /**
     * @param fileName 文件的名称
     * @param content  文件的内容
     * @return
     */
    public boolean saveContentToSdcard(String fileName, String content) {
        boolean flag = false;
        FileOutputStream fileOutputStream = null;
        // 获得sdcard卡所在的路径
        File file = new File(Environment.getExternalStorageDirectory(),
                fileName);
        // 判断sdcard卡是否可用
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(content.getBytes());
                flag = true;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return flag;
    }

}
