package com.gkzxhn.gkprison.authentication.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.net.FaceIDRequest;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by wrf on 2016/10/26.
 */

public class FaceRecognitionActivity extends AppCompatActivity {

    public static final String FACE_URL = "faceUrl";
    public static final String CONFIDENCE_RESULT = "confidence_result";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final int CAMERA_REQUEST_CODE = 1;
    private Button mButton;
    private FaceSurfaceView mCameraSurfaceView;
    private FrameLayout mFrameLayout;
    private int PICK_IMAGE_REQUEST = 1;
    private File imageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.face_recognition_activity);


        mFrameLayout = (FrameLayout) findViewById(R.id.contentview);
        mButton = (Button) findViewById(R.id.button_start_authentication);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "选择照片"), PICK_IMAGE_REQUEST);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        } else {
            Logger.e("小于23");
            initSurfaceView();
        }

        ApiTest();

    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {

        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    private void ApiTest() {
        Logger.e("ApiTest");

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.faceid.com/faceid/")
                .baseUrl("https://api.megvii.com/faceid/")
                //.baseUrl("https://api.megvii.com/facepp/")
                .build();
        File file = new File(getExternalCacheDir() + "/face.png");


        FaceIDRequest faceIDRequest = retrofit.create(FaceIDRequest.class);


        verify(faceIDRequest,file);
//        detectMG();

    }

    private void detectMG() {

        Retrofit retrofit = new Retrofit.Builder()
                //                .baseUrl("https://api.faceid.com/faceid/")
//                .baseUrl("https://api.megvii.com/faceid/")
                .baseUrl("https://api.megvii.com/facepp/")
                .build();
        File file = new File(getExternalCacheDir() + "/face.png");


        FaceIDRequest faceIDRequest = retrofit.create(FaceIDRequest.class);

        RequestBody partFromString1 = createPartFromString("HNlrtygFijtuRqBhfyGldXrCNE1byjIU");
        RequestBody partFromString2 = createPartFromString("m9yn6GtAsc-7iIqBSo3BkIoCjwpJYBRi");
        MultipartBody.Part image_file = prepareFilePart("image_file", file);

        Call<ResponseBody> detect1 = faceIDRequest.detectMG(partFromString1, partFromString2, image_file);


        detect1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Logger.e("response raw  = " + response.raw());
                try {
                    Logger.e("response body  = " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Logger.e("response message  = " + response.message());
                Logger.e("response code  = " + response.code());
                Logger.e("response errorBody  = " + response.errorBody());
                Logger.e("response headers  = " + response.headers());
                Logger.e("response isSuccessful  = " + response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.e("出错  = " + t.getMessage());

            }
        });
    }

    private void detect(FaceIDRequest faceIDRequest, File file) {
        RequestBody partFromString1 = createPartFromString("VL24sGJRPm7HgL46W2L2jekJvJRw0c9j");
        RequestBody partFromString2 = createPartFromString("f2lX0zkQMoeZc7rKkREL3X8JF-sSnsaP");
        MultipartBody.Part image_file = prepareFilePart("image", file);

        Call<ResponseBody> detect1 = faceIDRequest.detect(partFromString1, partFromString2, image_file);


        detect1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                printLog(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.e("出错  = " + t.getMessage());

            }
        });
    }

    private void verify(FaceIDRequest faceIDRequest, File file){

        RequestBody partFromString1 = createPartFromString("VL24sGJRPm7HgL46W2L2jekJvJRw0c9j");
        RequestBody partFromString2 = createPartFromString("f2lX0zkQMoeZc7rKkREL3X8JF-sSnsaP");
        RequestBody partFromString3 = createPartFromString("0");
        RequestBody partFromString4 = createPartFromString("raw_image");
        RequestBody partFromString5 = createPartFromString("1111");

        MultipartBody.Part image_ref1 = prepareFilePart("image_ref1", file);
        MultipartBody.Part image = prepareFilePart("image", file);

        Call<ResponseBody> verify = faceIDRequest.verify(partFromString1
                , partFromString2
                , partFromString3
                , partFromString4
                , partFromString5
                , image_ref1
                , image);


        verify.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                printLog(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.e("出错  = " + t.getMessage());
                Logger.e("出错  = " + t);
            }
        });
    }

    private void printLog(Response<ResponseBody> response) {
        Logger.e("response raw  = " + response.raw());
        try {
            Logger.e("response body  = " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.e("response message  = " + response.message());
        Logger.e("response code  = " + response.code());
        Logger.e("response errorBody  = " + response.errorBody());
        Logger.e("response headers  = " + response.headers());
        Logger.e("response isSuccessful  = " + response.isSuccessful());
    }


    private void initSurfaceView() {
        mCameraSurfaceView = new FaceSurfaceView(this);
        mCameraSurfaceView.setLayoutParams(
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT
                        , FrameLayout.LayoutParams.MATCH_PARENT));
        mFrameLayout.addView(mCameraSurfaceView, 0);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraSurfaceView.setOldFaceFile(new File(getExternalCacheDir() + "/" + "test.jpg"));
                //                String faceUrl = getIntent().getStringExtra(FACE_URL);
                String faceUrl = "http://img5.duitang.com/uploads/item/201408/23/20140823145710_iwdLQ.jpeg";
                Logger.e("faceUrl = " + faceUrl);
                if (TextUtils.isEmpty(faceUrl)) {
                    setResult(RESULT_CANCELED);
                    onBackPressed();
                    return;
                }

                mCameraSurfaceView.startAuthentication();
                mCameraSurfaceView.setFaceURL(faceUrl);
            }
        });

    }


    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            initSurfaceView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSurfaceView();
            } else {
                //用户勾选了不再询问
                //提示用户手动打开权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "相机权限已被禁止", Toast.LENGTH_SHORT).show();


                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.please_enter_the_authorization);
                    builder.setMessage(R.string.must_get_camera_permission);

                    builder.setPositiveButton("设置授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setResult(RESULT_CANCELED);
                            onBackPressed();
                        }
                    });
                    builder.show();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri uri = data.getData();
                Logger.e("uri= " + uri);
                String path = uri.getPath();
                Logger.e("path= " + path);
                imageFile = new File(path);
                mCameraSurfaceView.setOldFaceFile(imageFile);


            }
        }
    }


}
