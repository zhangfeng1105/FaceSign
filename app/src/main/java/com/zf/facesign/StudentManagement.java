package com.zf.facesign;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kelin.scrollablepanel.library.ScrollablePanel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class StudentManagement extends AppCompatActivity implements View.OnClickListener {

    private EditText idedit;
    private EditText nameedit;
    private Button resetButton;
    private Button queryButton;
    private Button getStudent;
    private ScrollablePanel scrollablePanel;
    private TestPanelAdapter testPanelAdapter;
    private MySQLite mySQLite = MySQLite.getInstance();
    private static int userclass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        Bundle bundle = this.getIntent().getExtras();
        userclass = bundle.getInt("class");

        testPanelAdapter = new TestPanelAdapter() {
            @Override
            public void resetFace(int tag) {
                System.out.println(tag);
                int id = Integer.valueOf(this.getData().get(tag).get(0));
                String imgpath = mySQLite.getImgpath(id);
                if (imgpath.equals("") || imgpath == null) {
                    mySQLite.updateImgpath(id, id + ".jpg");
                    getimage(id, id + ".jpg");
                } else {
                    getimage(id, imgpath);
                }
                Toast.makeText(StudentManagement.this, id + "刷新成功", Toast.LENGTH_SHORT).show();
            }
        };

        scrollablePanel = (ScrollablePanel) findViewById(R.id.student);
        scrollablePanel.setPanelAdapter(testPanelAdapter);
        idedit = findViewById(R.id.id_sudent);
        nameedit = findViewById(R.id.name_student);
        resetButton = findViewById(R.id.reset);
        queryButton = findViewById(R.id.query);
        getStudent = findViewById(R.id.getstudent);
        resetButton.setOnClickListener(this);
        queryButton.setOnClickListener(this);
        getStudent.setOnClickListener(this);
        List<String> head = new ArrayList<>();
        head.add("学号");
        head.add("姓名");
        head.add("人脸信息");
        testPanelAdapter.set_mHeaders(head);

        List<List<String>> lists = mySQLite.qurruStudent();
        testPanelAdapter.setData(lists);
        scrollablePanel.notifyDataSetChanged();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                testPanelAdapter.setData(mySQLite.qurruStudent());
                scrollablePanel.notifyDataSetChanged();
                break;
            case R.id.query:
                String id = idedit.getText().toString();
                String name = nameedit.getText().toString();
                if (id.equals("")) {
                    if (name.equals("")) {
                        Toast.makeText(StudentManagement.this, "请输入查询信息", Toast.LENGTH_SHORT).show();
                    } else {
                        //查名字
                        testPanelAdapter.setData(mySQLite.qurruStudent(name));
                    }
                } else {
                    //查学号
                    testPanelAdapter.setData(mySQLite.qurruStudent(Integer.valueOf(id)));
                }
                scrollablePanel.notifyDataSetChanged();
                break;
            case R.id.getstudent:
                mySQLite.clearDB();
                FileHelper.getInstance().clearImg();
                getstudent();
                testPanelAdapter.setData(mySQLite.qurruStudent());
                scrollablePanel.notifyDataSetChanged();
                break;
        }
    }

    private void getImge() {
        Map<Integer, String> map = mySQLite.qurryId();
        if (map != null) {
            for (Integer key : map.keySet()) {
                getimage(key, map.get(key));
            }
        }
    }

    private void getimage(Integer integer, String filepath) {
        gethttp("android/getimage/" + integer, filepath);
    }

    public void gethttp(final String path, final String filepath) {
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host("face.vaiwan.com")
                .addPathSegments(path)
                .build();
        final Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("请求失败" + e);
                (StudentManagement.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(StudentManagement.this, "服务器出错", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    InputStream inputStream = response.body().byteStream();//得到图片的流
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    (StudentManagement.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(path);
                            FileHelper.getInstance().saveBitmap(filepath, bitmap);
                        }
                    });
                }
            }
        });
    }


    private void getstudent() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("classid", String.valueOf(userclass));
        new OkHttp(StudentManagement.this).posthttp("android/getStudent", hashMap, new OkHttp.DealResponse() {
            @Override
            public void parseResponse(String responseStr, int responseCode) {
                if (responseCode == 200) {
                    try {
                        JSONObject obj = new JSONObject(responseStr);
                        if (obj.has("student")) {
                            System.out.println(obj.getJSONArray("student"));
                            JSONArray array = obj.getJSONArray("student");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                int studentid = o.getInt("id");
                                String studentname = o.getString("name");
                                String studentimage = o.getString("image");
                                mySQLite.insert(studentid, studentname, studentimage);
                            }
                            getImge();
                        }
                    } catch (JSONException exp) {
                        exp.printStackTrace();
                    }
                } else if (responseCode == 500) {
                    Toast.makeText(StudentManagement.this, "无该班级", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(StudentManagement.this, "服务器维护", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }


}