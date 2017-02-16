package com.wrf.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.megvii.licensemanager.Manager;
import com.megvii.livenessdetection.LivenessLicenseManager;
import com.megvii.livenesslib.LivenessActivity2;
import com.megvii.livenesslib.util.ConUtil;
import com.orhanobut.logger.Logger;

import static com.megvii.livenesslib.LivenessActivity2.IMAGE_REF_PATH;
import static com.megvii.livenesslib.LivenessActivity2.UUID;

public class MyMainActivity extends AppCompatActivity {


    private static final int ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        CrashReport.testJavaCrash();

        new WarrantyTask().execute(); //验证授权代码移到，此页面的前一个页面

        findViewById(R.id.screenshot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyMainActivity.this, LivenessActivity2.class);
                intent.putExtra(UUID,"111111");
                intent.putExtra(IMAGE_REF_PATH,getExternalCacheDir()+"/test.png");
                startActivityForResult(intent
                        , ACTIVITY_REQUEST_CODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e("onActivityResult");
        Logger.e("resultCode = "+resultCode);
        Logger.e("requestCode = "+requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_REQUEST_CODE) {

             boolean verify =  data.getBooleanExtra(LivenessActivity2.CONFIDENCE_RESULT, false);
             double  value  = data.getDoubleExtra(LivenessActivity2.CONFIDENCE_VALUE, 0);

                Logger.e("CONFIDENCE_RESULT = verify = "+verify);
                Logger.e("CONFIDENCE_VALUE = value = "+value);

//                Toast.makeText(this, "返回结果 = "
//                        + data.getBooleanExtra(LivenessActivity2.CONFIDENCE_RESULT, false), Toast.LENGTH_LONG).show();
//
//                Toast.makeText(this, "返回对比值 = "
//                        + data.getDoubleExtra(LivenessActivity2.CONFIDENCE_VALUE, 0), Toast.LENGTH_LONG).show();
            }
        }
    }

    class WarrantyTask extends AsyncTask<Void, Void, Integer> {


        private ProgressDialog mProgressDialog = new ProgressDialog(MyMainActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("授权");
            mProgressDialog.setMessage("正在联网授权中...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {


            Manager manager = new Manager(MyMainActivity.this);
            LivenessLicenseManager licenseManager = new LivenessLicenseManager(
                    MyMainActivity.this);
            manager.registerLicenseManager(licenseManager);

            manager.takeLicenseFromNetwork(ConUtil.getUUIDString(MyMainActivity.this));
            if (licenseManager.checkCachedLicense() > 0)
                return 1;
            else
                return 0;


        }


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mProgressDialog.dismiss();
//            if (integer == 1) {
//                uuid = getIntent().getStringExtra(UUID);
//                imageRefPath = getIntent().getStringExtra(IMAGE_REF_PATH);
//
//
//            } else if (integer == 0) {
//                setFaceResult(RESULT_CANCELED, false, 0);
//            }
        }
    }
}
