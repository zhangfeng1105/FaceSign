package com.zf.facesign;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.face.FaceEngine;

import static com.arcsoft.face.ErrorInfo.MERR_ASF_NOT_ACTIVATED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String APP_ID = "Dt4H27r3ZeyBtQe85323CDBvuRDEQRxVYfsZMax7hKVa";
    public static final String SDK_KEY = "AQQpadc3vomyt7GNpTWrqTobkGXmejTWDRnHbqsz4zDw";

    private static int userclass;

    private ImageView studentview;
    private ImageView feceview;
    private ImageView managementview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        active();
        Bundle bundle = this.getIntent().getExtras();
        String username = bundle.getString("name");
        userclass = bundle.getInt("class");
        int userlevel = bundle.getInt("level");
        TextView textusername = findViewById(R.id.username);
        TextView textuserclass = findViewById(R.id.userclass);
        if (userlevel == 1) {
            textusername.setText(username + "老师");
        } else {
            textusername.setText(username + "同学");
            textuserclass.setText(userclass + "班");
        }

        studentview = findViewById(R.id.student);
        feceview = findViewById(R.id.fece);
        managementview = findViewById(R.id.management);
        studentview.setOnClickListener(this);
        feceview.setOnClickListener(this);
        managementview.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.student :
                Intent intent1 = new Intent( MainActivity.this,StudentManagement.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("class", userclass);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.fece :
                Intent intent = new Intent( MainActivity.this,FaceRecognize.class);
                Bundle bundle = new Bundle();
                bundle.putInt("class", userclass);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.management :
                Intent intent2 = new Intent( MainActivity.this,SignManagement.class);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("class", userclass);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
        }
    }

    public void active() {
        FaceEngine faceEngine = new FaceEngine();
        int isActive = faceEngine.active(MainActivity.this, APP_ID, SDK_KEY);
        if (isActive == MERR_ASF_NOT_ACTIVATED) {
            Toast.makeText(this, "人脸识别功能激活失败!", Toast.LENGTH_SHORT).show();
        }
    }
}
