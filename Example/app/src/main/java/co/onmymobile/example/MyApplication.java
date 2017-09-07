package co.onmymobile.example;

import android.app.Application;

import co.onmymobile.wifimodule.BranchActivity;

/**
 * Created by onmymobile on 9/7/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BranchActivity.branchInstance(getApplicationContext());
    }
}
