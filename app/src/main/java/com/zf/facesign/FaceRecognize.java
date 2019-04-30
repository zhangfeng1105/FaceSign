package com.zf.facesign;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arcsoft.face.ErrorInfo.MOK;
import static com.arcsoft.face.FaceEngine.ASF_DETECT_MODE_IMAGE;
import static com.arcsoft.face.FaceEngine.ASF_DETECT_MODE_VIDEO;
import static com.arcsoft.face.FaceEngine.ASF_FACE_DETECT;
import static com.arcsoft.face.FaceEngine.ASF_FACE_RECOGNITION;
import static com.arcsoft.face.FaceEngine.ASF_LIVENESS;
import static com.arcsoft.face.FaceEngine.ASF_OP_0_HIGHER_EXT;
import static com.arcsoft.face.FaceEngine.CP_PAF_BGR24;
import static com.arcsoft.face.FaceEngine.CP_PAF_NV21;


public class FaceRecognize extends AppCompatActivity implements SurfaceHolder.Callback {

    private static int userclass;
    private static final float FACESAMILAR = (float) 0.9;
    private List<FaceInfo> faceInfoList;
    private FaceEngine faceEngine;
    private FaceEngine faceEngine1;
    private SurfaceView surfaceView;
    private Camera camera;
    private MySQLite mySQLite = MySQLite.getInstance();
    private FileHelper fileHelper = FileHelper.getInstance();

    private Map<Integer, FaceFeature> faceEngineMap = new HashMap<>();

    private int mWidth = 1280;
    private int mHeight = 960;

    private Button signbtn;
    private TextView showcount;
    private ListView listView;
    private SignlisetAdapter signlisetAdapter;
    private List<Integer> signdata;
    private Map<Integer, String> nameMap = mySQLite.qurryIdandName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognize);
        //保持亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle bundle = this.getIntent().getExtras();
        userclass = bundle.getInt("class");

        faceEngine = new FaceEngine();
        faceEngine1 = new FaceEngine();
        faceInfoList = new ArrayList<>();

        int isInit1 = faceEngine1.init(this, ASF_DETECT_MODE_IMAGE, ASF_OP_0_HIGHER_EXT, 32, 1, ASF_FACE_DETECT | ASF_FACE_RECOGNITION);
        if (isInit1 != MOK) {
            Toast.makeText(this, "人脸识别功能初始化失败!", Toast.LENGTH_LONG).show();
        } else {
            setFaceMap();
        }
        int isInit = faceEngine.init(this, ASF_DETECT_MODE_VIDEO, ASF_OP_0_HIGHER_EXT, 16, 50, ASF_FACE_DETECT | ASF_FACE_RECOGNITION | ASF_LIVENESS);
        if (isInit != MOK) {
            Toast.makeText(this, "人脸识别功能初始化失败!", Toast.LENGTH_LONG).show();
        }

        System.out.println("face:" + faceEngineMap.size());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            //拥有权限
            surfaceView = this.findViewById(R.id.surfaceView);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            showcount = findViewById(R.id.showsigncount);
            signbtn = findViewById(R.id.signbtn);
            signbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (signdata.isEmpty()) {
                        Toast.makeText(view.getContext(), "空列表", Toast.LENGTH_LONG).show();
                    } else {
                        System.out.println(signdata.get(0));
                        push(signdata);
                        signdata.clear();
                        signlisetAdapter.setList(signdata);
                        signlisetAdapter.notifyDataSetChanged();
                        showcount.setText("");
                    }
                }
            });
            listView = findViewById(R.id.signlist);
            signlisetAdapter = new SignlisetAdapter(this) {
                @Override
                public void delelte(int i) {
                    signdata.remove(i);
                    signlisetAdapter.setList(signdata);
                    signlisetAdapter.notifyDataSetChanged();
                    showcount.setText("已签到" + signdata.size() + "人");
                }
            };
            signdata = signlisetAdapter.getList();
            listView.setAdapter(signlisetAdapter);
        }

    }

    private void push(List<Integer> signdata) {
        String date = Date.getDate();
        if (date.equals("")) {
            Toast.makeText(this, "非上课时间", Toast.LENGTH_LONG).show();
        } else {
//            new OkHttp(FaceRecognize.this).gethttp("android/clearsign", new OkHttp.DealResponse() {
//                @Override
//                public void parseResponse(String responseStr, int responseCode) {
//                    if (responseCode == 200){
//
//                    }
//                }
//            });
            for (Map.Entry<Integer, String> entry : nameMap.entrySet()) {
                if (signdata.contains(entry.getKey())){
                    push(entry.getKey(), date, 1);
                }else {
                    push(entry.getKey(), date, 2);
                }
            }
            Toast.makeText(FaceRecognize.this, "提交成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void push(Integer integer, String date, Integer sign) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", String.valueOf(integer));
        hashMap.put("date", date);
        hashMap.put("class", String.valueOf(userclass));
        hashMap.put("name", nameMap.get(integer));
        hashMap.put("sign", String.valueOf(sign));
        new OkHttp(FaceRecognize.this).posthttp("android/pushsign", hashMap, new OkHttp.DealResponse() {
            @Override
            public void parseResponse(String responseStr, int responseCode) {
                if (responseCode == 200) {
                    return;
                } else {
                    Toast.makeText(FaceRecognize.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void setFaceMap() {
        Map<Integer, String> filemap = mySQLite.qurryId();
        if (filemap != null) {
            for (Integer key : filemap.keySet()) {
                Bitmap bitmap = ImageUtil.alignBitmapForBgr24(fileHelper.getBitmap(filemap.get(key)));
                byte[] imgbyte = ImageUtil.bitmapToBgr(bitmap);
                FaceFeature facefeature = dofeature(imgbyte, bitmap.getWidth(), bitmap.getHeight());
                if (facefeature != null) {
                    faceEngineMap.put(key, facefeature);
                }
            }
            System.out.println("face:" + faceEngineMap.size());
        } else {
            Toast.makeText(this, "无学生信息", Toast.LENGTH_LONG).show();
        }
    }

    private FaceFeature dofeature(byte[] nv21img, int width, int height) {
        if (faceInfoList.size() > 0)
            faceInfoList.clear();
        int detect = faceEngine1.detectFaces(nv21img, width, height, CP_PAF_BGR24, faceInfoList);
        System.out.println("faceInfoList:" + faceInfoList.size());
        if (detect == MOK && faceInfoList.size() > 0) {
            FaceFeature feature = new FaceFeature();
            int extract = faceEngine1.extractFaceFeature(nv21img, width, height, CP_PAF_BGR24, faceInfoList.get(0), feature);
            if (extract == MOK) {
                return feature;
            }
        }
        return null;
    }


    @Override
    public void surfaceCreated(final SurfaceHolder holder) {

        try {
            if (camera == null)
                camera = Camera.open(1);
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewFormat(ImageFormat.NV21);
            //对焦模式设置
            List<String> supportedFocusModes = parameters.getSupportedFocusModes();
            if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
                if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
            }
            camera.setParameters(parameters);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (faceInfoList.size() > 0)
                        faceInfoList.clear();
                    int detect = faceEngine.detectFaces(data, mWidth, mHeight, CP_PAF_NV21, faceInfoList);
                    if (detect == MOK && faceInfoList.size() > 0) {
                        for (FaceInfo faceInfo : faceInfoList) {
                            FaceFeature feature = new FaceFeature();
                            int extract = faceEngine.extractFaceFeature(data, mWidth, mHeight, CP_PAF_NV21, faceInfo, feature);
                            if (extract == MOK) {
                                for (Map.Entry<Integer, FaceFeature> entry : faceEngineMap.entrySet()) {
                                    FaceSimilar faceSimilar = new FaceSimilar();
                                    int compare = faceEngine.compareFaceFeature(feature, entry.getValue(), faceSimilar);
                                    if (compare == MOK) {
                                        System.out.println(entry.getKey() + "相似度:" + faceSimilar.getScore());
                                        float similar = faceSimilar.getScore();
                                        if (similar > FACESAMILAR && similar <= (float) 1.0) {
                                            if (!signdata.contains(entry.getKey())) {
                                                signdata.add(entry.getKey());
                                                signlisetAdapter.setList(signdata);
                                                signlisetAdapter.notifyDataSetChanged();
                                                showcount.setText("已签到" + signdata.size() + "人");
                                            }
                                            ;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
            camera.setDisplayOrientation(0);
//            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != camera) {
            System.out.println("销毁相机");
            holder.removeCallback(this);
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
        }
        if (faceEngine != null) {
            faceEngine.unInit();
        }
    }

}