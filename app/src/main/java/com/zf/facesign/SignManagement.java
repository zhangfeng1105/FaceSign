package com.zf.facesign;

import android.app.DatePickerDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kelin.scrollablepanel.library.ScrollablePanel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SignManagement extends AppCompatActivity {
    private static int userclass;
    private static final String[] m = {"第一节课", "第二节课", "第三节课", "第四节课"};
    private static int classid = 1;
    private static String datefanal;

    private EditText dateedit;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private Button findSignbtn;
    private ScrollablePanel scrollablePanel;
    private TestPanelAdapter testPanelAdapter;

    private List<List<String>> dataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_management);
        Bundle bundle = this.getIntent().getExtras();
        userclass = bundle.getInt("class");
        TextView textView = findViewById(R.id.classid);
        textView.setText(userclass + "班");

        dateedit = findViewById(R.id.dateedit);
        dateedit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                showDateChoose();
            }
        });
        dateedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateChoose();
            }
        });

        spinner = (Spinner) findViewById(R.id.chooseClass);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                classid = i + 1;
                System.out.println(classid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setVisibility(View.VISIBLE);

        findSignbtn = findViewById(R.id.findSign);
        findSignbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = String.valueOf(dateedit.getText());
                System.out.println(date);
                if (date.equals("")) {
                    Toast.makeText(SignManagement.this, "请选择日期", Toast.LENGTH_SHORT).show();
                } else {
                    dataList.clear();
                    findSign(date + classid);
                    datefanal = date + classid;
                }
            }
        });


        scrollablePanel = (ScrollablePanel) findViewById(R.id.signPanel);
        testPanelAdapter = new TestPanelAdapter() {
            @Override
            public void resetFace(int tag) {
                //手动签到
                int id = Integer.valueOf(this.getData().get(tag).get(0));
                if (datefanal != null && !datefanal.equals("")) {
                    pushSign(id, datefanal);
                    testPanelAdapter.getData().get(tag).set(2, "已签到");
                    scrollablePanel.notifyDataSetChanged();
                }
            }
        };
        testPanelAdapter.setBtnname("签到");
        scrollablePanel.setPanelAdapter(testPanelAdapter);
        List<String> head = new ArrayList<>();
        head.add("学号");
        head.add("姓名");
        head.add("签到状况");
        testPanelAdapter.set_mHeaders(head);
        String datenow = Date.getDate();
        if (datenow!=null){
            findSign(datenow);
        }

    }

    private void pushSign(int id, String datefanal) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("date", datefanal);
        hashMap.put("id", String.valueOf(id));
        new OkHttp(SignManagement.this).posthttp("android/updateSign", hashMap, new OkHttp.DealResponse() {
            @Override
            public void parseResponse(String responseStr, int responseCode) {
                if (responseCode == 200) {
                    Toast.makeText(SignManagement.this, "修改成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findSign(final String s) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("date", s);
        hashMap.put("class", String.valueOf(userclass));
        new OkHttp(SignManagement.this).posthttp("android/getSign", hashMap, new OkHttp.DealResponse() {
            @Override
            public void parseResponse(String responseStr, int responseCode) {
                if (responseCode == 200) {
                    try {
                        JSONObject obj = new JSONObject(responseStr);
                        if (obj.has("date")) {
                            System.out.println(obj.get("date"));
                            JSONArray array = obj.getJSONArray("date");
                            for (int i = 0; i < array.length(); i++) {
                                List<String> list = new ArrayList<>();
                                JSONObject o = array.getJSONObject(i);
                                int studentid = o.getInt("id");
                                String studentname = o.getString("name");
                                String sign = o.getString("sign");
                                if (sign.equals("1")) {
                                    sign = "已签到";
                                } else {
                                    sign = "旷课";
                                }
                                list.add(String.valueOf(studentid));
                                list.add(studentname);
                                list.add(sign);
                                dataList.add(list);
                            }
                        }
                        testPanelAdapter.setData(dataList);
                        scrollablePanel.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showDateChoose() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(SignManagement.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    String s = "" + i + (i1 + 1) + i2;
                    (SignManagement.this).dateedit.setText(s);
                }
            });
        }
    }
}
