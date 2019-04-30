package com.zf.facesign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
    private static FileHelper mInstance;

    private FileHelper() {

    }

    //单例模式
    public static FileHelper getInstance() {
        if (mInstance == null) {
            mInstance = new FileHelper();
        }
        return mInstance;
    }

    private String getDataDirectory() {
        Context context = MyApplication.getContext();
        //如果找到外部存储,使用外部存储
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return context.getExternalFilesDir("").getAbsolutePath();
        } else {
            //否则使用内部存储
            return context.getFilesDir().getAbsolutePath();
        }
    }

    public void saveBitmap(String fileName, Bitmap bmp) {
        File files = new File(FileHelper.getInstance().getDataDirectory() + "/img");
        if (!files.exists()) {//如果文件夹不存在
            files.mkdir();//创建文件夹
        }
        File file = new File(FileHelper.getInstance().getDataDirectory() + "/img", fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 10, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearImg() {
        File files = new File(FileHelper.getInstance().getDataDirectory() + "/img");
        if (files.exists()) {//如果文件夹存在
            for (File file : files.listFiles()) {
                file.delete();
            }
            System.out.println("图片清空");
        }
    }

    public Bitmap getBitmap(String fileName) {
        File file = new File(FileHelper.getInstance().getDataDirectory() + "/img", fileName);
        if (!file.exists()) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        return bitmap;
    }
}
