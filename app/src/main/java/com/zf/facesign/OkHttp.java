package com.zf.facesign;


import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp {
    public static final String SERVERIP = "face.vaiwan.com";
    public static final int SERVERPORT = 1337;
    private Context context;
    private DealResponse _mDealResponse;

    public OkHttp(Context context) {
        this.context = context;
    }

    public interface DealResponse {
        void parseResponse(String responseStr, int responseCode);
    }


    public void posthttp(String path, HashMap<String, String> date, final DealResponse dealResponse){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        FormBody.Builder formBuilder = new FormBody.Builder();
        if(date != null) {
            for (String key : date.keySet()){
                formBuilder.add(key, date.get(key));
            }
        }
        _mDealResponse = dealResponse;
        FormBody formBody = formBuilder.build();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(SERVERIP)
//                .port(SERVERPORT)
                .addPathSegments(path)
                .build();
        final Request request = new Request.Builder().url(url).post(formBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("请求失败");
                e.printStackTrace();
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器出错", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                final int responseCode = response.code();

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(_mDealResponse != null) {
                            _mDealResponse.parseResponse(responseStr, responseCode);
                        }
                    }
                });
            }
        });
    }


    public void gethttp(String path, final DealResponse dealResponse){
        OkHttpClient okHttpClient = new OkHttpClient();
        _mDealResponse = dealResponse;
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(SERVERIP)
//                .port(SERVERPORT)
                .addPathSegments(path)
                .build();
        final Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("请求失败"+e);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器出错", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                final int responseCode = response.code();

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(_mDealResponse != null) {
                            _mDealResponse.parseResponse(responseStr, responseCode);
                        }
                    }
                });
            }
        });
    }


}
