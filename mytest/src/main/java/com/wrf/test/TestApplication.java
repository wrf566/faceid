package com.wrf.test;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by wrf on 2016/10/31.
 */

public class TestApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
//             This process is dedicated to LeakCanary for heap analysis.
//             You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...


        CrashReport.initCrashReport(getApplicationContext(), "826562b6cc", false);
    }
}
