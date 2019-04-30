package com.zf.facesign;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText idtextview;
    private EditText passtextview;
    private Map<String, Object> usermap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getPermission();


        idtextview = findViewById(R.id.id);
        passtextview = findViewById(R.id.pass);


        Button login = this.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idtext = idtextview.getText().toString();
                String password = passtextview.getText().toString();
                if (idtext.equals("") || password.equals("")) {
                    Toast.makeText(Login.this, "请输入完整", Toast.LENGTH_SHORT).show();
                } else {
                    getUser(idtext, password);
                }
            }
        });
    }


    public void getUser(String id, String pass) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("password", pass);
        new OkHttp(Login.this).posthttp("android/getuser", hashMap, new OkHttp.DealResponse() {
            @Override
            public void parseResponse(String responseStr, int responseCode) {
                if (responseCode == 200) {
                    try {
                        JSONObject obj = new JSONObject(responseStr);
                        if (obj.has("user")) {
                            usermap.clear();
                            System.out.println(obj.getJSONObject("user"));
                            usermap.put("id", obj.getJSONObject("user").getInt("id"));
                            usermap.put("name", obj.getJSONObject("user").getString("name"));
                            usermap.put("class", obj.getJSONObject("user").getInt("class"));
                            usermap.put("level", obj.getJSONObject("user").getInt("level"));
                        }
                        if (obj.has("pass")) {
                            Toast.makeText(Login.this, "密码错误", Toast.LENGTH_SHORT).show();
                            usermap.clear();
                            return;
                        }
                    } catch (JSONException exp) {
                        exp.printStackTrace();
                    }
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", String.valueOf(usermap.get("name")));
                    bundle.putInt("class", (Integer) usermap.get("class"));
                    bundle.putInt("level", (Integer) usermap.get("level"));
                    intent.putExtras(bundle);
                    usermap.clear();
                    idtextview.setText("");
                    passtextview.setText("");
                    startActivity(intent);
                }else if(responseCode == 500) {
                    Toast.makeText(Login.this, "用户不存在", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(Login.this, "服务器维护", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    public void getPermission() {
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        List<String> mPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

}