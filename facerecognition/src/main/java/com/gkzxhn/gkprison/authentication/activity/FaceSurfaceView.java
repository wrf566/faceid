package com.gkzxhn.gkprison.authentication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gkzxhn.gkprison.bean.FaceDetect;
import com.gkzxhn.gkprison.bean.FacesBean;
import com.google.gson.Gson;
import com.megvii.cloud.http.CommonOperate;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wrf on 2016/10/26.
 */
public class FaceSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback, Camera.PreviewCallback {

    public static final String FACE_TOKEN = "face_token";
    public static final String CONFIDENCE = "confidence";
    public static final String FACES = "faces";
    private static final String TAG = "CameraSurfaceView";
    private static final String API_KEY = "wRF9YGZD7Px9qi4UVKeKVdVvX6W-wTNe";
    private static final String API_SECRET = "Z2HDpqZBcS0R6K6-Wzf6zPW9GfCByV3s";
    private static final int CONFIDENCE_STANDARD = 90;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mScreenWidth;
    private int mScreenHeight;
    //    private String mFaceToken = "ba19ccd5ee1ce5d298a7e299284ed821";
    private String mFaceToken;
    private FaceTask mFaceTask;

    private boolean isStart;


    private String FACE_IMG_PATH = getContext().getExternalCacheDir() + "/" + "face.png";  //测试用，API 19 以下会要申请权限
    //    private String FACE_IMG_PATH = getContext().getCacheDir() + "/" + "face.png";
    private String mFaceURL;
    private File mOldFaceFile;

    public FaceSurfaceView(Context context) {
        this(context, null);
        getScreenMetrix(context);
        initView();
    }

    public FaceSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void setFaceToken(String faceToken) {
        this.mFaceToken = faceToken;
    }

    public void setFaceURL(String faceURL) {
        mFaceURL = faceURL;
    }

    public void setOldFaceFile(File oldFaceFile) {
        this.mOldFaceFile = oldFaceFile;
    }

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView() {
        mHolder = getHolder();//获得surfaceHolder引用
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            mCamera = Camera.open(1);//开启相机
            try {
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
                mCamera.setPreviewCallback(this);//设置预览回调
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        //设置参数并开始预览
        setCameraParams(mScreenWidth, mScreenHeight);
        mCamera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        mCamera.setPreviewCallback(null);//停止预览回调
        mCamera.stopPreview();//停止预览
        mCamera.release();//释放相机资源
        mCamera = null;
        mHolder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            Log.i(TAG, "onAutoFocus success=" + success);
        }
    }

    private void setCameraParams(int width, int height) {
        Log.i(TAG, "setCameraParams  width=" + width + "  height=" + height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
        this.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }

    //----分割线-----

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     * h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (!isStart) {
            return;
        }
        if (mFaceTask != null) {
            switch (mFaceTask.getStatus()) {
                case RUNNING:
                    return;
                case PENDING:
                    return;
                case FINISHED:
                    return;
            }
        }
        mFaceTask = new FaceTask(bytes, camera);
        mFaceTask.execute();

    }

    public void startAuthentication() {
        isStart = true;
    }

    private class FaceTask extends AsyncTask<Void, Void, Double> {

        private byte[] mData;
        private Camera camera;


        private ProgressDialog mProgressDialog = new ProgressDialog(getContext());


        //构造函数
        FaceTask(byte[] data, Camera camera) {
            this.mData = data;
            this.camera = camera;
        }

        /**
         * 旋转图片
         *
         * @param angle
         * @param bitmap
         * @return Bitmap
         */
        private Bitmap rotaingBitmap(int angle, Bitmap bitmap) {
            //旋转图片 动作
            Matrix matrix = new Matrix();
            ;
            matrix.postRotate(angle);
            // 创建新的图片
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizedBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setTitle("正在验证");
            mProgressDialog.setMessage("正在验证您的身份");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Double doInBackground(Void... params) {
            // TODO Auto-generated method stub
            double confidence = 0;

            Logger.e("doInBackground");

            Camera.Parameters parameters = camera.getParameters();
            int width = parameters.getPreviewSize().width;
            int height = parameters.getPreviewSize().height;

            YuvImage yuv = new YuvImage(mData, parameters.getPreviewFormat(), width, height, null);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

            byte[] bytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            bitmap = rotaingBitmap(-90, bitmap);
            try {
                File newFaceFile = new File(FACE_IMG_PATH);
                FileOutputStream outputStream = new FileOutputStream(newFaceFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
                outputStream.flush();
                outputStream.close();


                CommonOperate commonOperate = new CommonOperate(API_KEY, API_SECRET);
                String oldFaceToken;
                String newFaceToken;

//                if (mOldFaceFile != null && mOldFaceFile.exists()) {
                if(!TextUtils.isEmpty(mFaceURL)){
                    oldFaceToken = getFaceToken(commonOperate.detectUrl(mFaceURL));
                    newFaceToken = getFaceToken(commonOperate.detectFile(newFaceFile));

                    String compareStr = new String(commonOperate.compare(oldFaceToken, newFaceToken), "utf-8");
                    Logger.e("compareStr = " + compareStr);
                    JSONObject compareObject = new JSONObject(compareStr);
                    confidence = compareObject.optDouble(CONFIDENCE);
                    Logger.e("confidence = " + confidence);
                }


            } catch (Throwable e) {
                e.printStackTrace();
            }

            return confidence;
        }

        @Override
        protected void onPostExecute(Double confidence) {
            mProgressDialog.dismiss();
            if (confidence >= CONFIDENCE_STANDARD) {
                Toast.makeText(getContext(), "恭喜您！身份验证成功！！", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "很遗憾！身份验证失败！！", Toast.LENGTH_LONG).show();
            }
            Activity activity = (Activity) getContext();
            isStart = false;
            mFaceTask = null;
            Intent intent = new Intent();
            intent.putExtra(FaceRecognitionActivity.CONFIDENCE_RESULT, confidence >= CONFIDENCE_STANDARD);
            activity.setResult(RESULT_OK, intent);
            activity.onBackPressed();
        }


        private String getFaceToken(byte[] faceRsponses) {
            String faceToken = null;
            try {
                String face = new String(faceRsponses, "utf-8");
                Logger.e("face = " + face);
                Gson gson = new Gson();
                FaceDetect faceDetect = gson.fromJson(face, FaceDetect.class);
                Logger.e("faceDetect = " + faceDetect);
                List<FacesBean> faces = faceDetect.getFaces();
                if (!faces.isEmpty()) {
                    FacesBean facesBean = faces.get(0);
                    Logger.e("facesBean = " + facesBean);
                    faceToken = facesBean.getFace_token();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Logger.e("faceToken = " + faceToken);
            return faceToken;

        }
    }



    private void faceID(){

    }
}
