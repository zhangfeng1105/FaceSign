package com.zf.facesign;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLite extends SQLiteOpenHelper {
    private static MySQLite mInstance;

    private MySQLite(Context context) {
        super(context, "facefeature.db", null, 1);
    }

    //单例模式
    public static MySQLite getInstance() {
        if(mInstance == null) {
            Context context =  MyApplication.getContext();
            mInstance = new MySQLite(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE facefeature(faceid INTEGER PRIMARY KEY UNIQUE,id INTEGER,name varchar(20),image varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void clearDB(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from facefeature");
    }

    public String getImgpath(int id){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM facefeature where id =" + id, null);
        if (cursor.moveToFirst()) {
            do {
                String image = cursor.getString((cursor.getColumnIndex("image")));
                return image;
            }while (cursor.moveToNext());
        }
        return "";
    }
    public void updateImgpath(int id,String path){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update facefeature set image = '" + path +"' where id="+id,null);
    }

    public void insert(int id, String name, String image) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO facefeature(id,name,image) values(" + id + ",'" + name + "','" + image + "')");
    }

    public Map<Integer,String> qurryId() {
        Map<Integer,String> map = new HashMap<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM facefeature", null);
        if (cursor.moveToFirst()) {
            do {
                int idstudent = cursor.getInt((cursor.getColumnIndex("id")));
                String image = cursor.getString((cursor.getColumnIndex("image")));
                map.put(idstudent,image);
            }while (cursor.moveToNext());
        }
        System.out.println(map.size());
        cursor.close();
        return map;
    }

    public Map<Integer,String> qurryIdandName() {
        Map<Integer,String> map = new HashMap<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM facefeature", null);
        if (cursor.moveToFirst()) {
            do {
                int idstudent = cursor.getInt((cursor.getColumnIndex("id")));
                String image = cursor.getString((cursor.getColumnIndex("name")));
                map.put(idstudent,image);
            }while (cursor.moveToNext());
        }
        System.out.println(map.size());
        cursor.close();
        return map;
    }

    public List<List<String>> qurruStudent() {
        List<List<String>> lists = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM facefeature", null);
        if (cursor.moveToFirst()) {
            do {
                List<String> list = new ArrayList<>();
                int idstudent = cursor.getInt((cursor.getColumnIndex("id")));
                String namestudent = cursor.getString((cursor.getColumnIndex("name")));
                String image = cursor.getString((cursor.getColumnIndex("image")));
                list.add(String.valueOf(idstudent));
                list.add(namestudent);
                if (image.equals("")){
                    list.add("空");
                }{
                    list.add("存在");
                }                lists.add(list);
            }while (cursor.moveToNext());
            cursor.close();
            System.out.println(lists.size());
            return lists;
        }
        cursor.close();
        return lists;
    }
    public List<List<String>> qurruStudent(int id) {
        List<List<String>> lists = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        if (id != 0){
            Cursor cursor = db.rawQuery("SELECT * FROM facefeature WHERE id = " + id, null);
            if (cursor.moveToFirst()) {
                do {
                    List<String> list = new ArrayList<>();
                    int idstudent = cursor.getInt((cursor.getColumnIndex("id")));
                    String namestudent = cursor.getString((cursor.getColumnIndex("name")));
                    String image = cursor.getString((cursor.getColumnIndex("image")));
                    list.add(String.valueOf(idstudent));
                    list.add(namestudent);
                    if (image.equals("")){
                        list.add("空");
                    }{
                        list.add("存在");
                    }
                    lists.add(list);
                }while (cursor.moveToNext());
                cursor.close();
                return lists;
            }
            cursor.close();
        }
        return lists;
    }
    public List<List<String>> qurruStudent(String name) {
        List<List<String>> lists = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        if (!name.equals("")){
            Cursor cursor = db.rawQuery("SELECT * FROM facefeature WHERE name = " + name, null);
            if (cursor.moveToFirst()) {
                do {
                    List<String> list = new ArrayList<>();
                    int idstudent = cursor.getInt((cursor.getColumnIndex("id")));
                    String namestudent = cursor.getString((cursor.getColumnIndex("name")));
                    String image = cursor.getString((cursor.getColumnIndex("image")));
                    list.add(String.valueOf(idstudent));
                    list.add(namestudent);
                    if (image.equals("")){
                        list.add("空");
                    }{
                        list.add("存在");
                    }                    lists.add(list);
                }while (cursor.moveToNext());
                cursor.close();
                return lists;
            }
            cursor.close();
        }
        return lists;
    }

}
