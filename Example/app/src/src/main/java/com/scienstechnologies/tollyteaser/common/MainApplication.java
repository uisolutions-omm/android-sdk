package com.scienstechnologies.tollyteaser.common;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import com.scienstechnologies.tollyteaser.activity.MainActivity;
import com.scienstechnologies.tollyteaser.activity.TTListActivity;

import org.json.JSONObject;

import co.onmymobile.wifimodule.BranchActivity;
import co.onmymobile.wifimodule.BsnlWifiActivity;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

/**
 * Created by onmymobile on 5/18/2017.
 */

public class MainApplication extends Application {
    public static MainApplication sApplication;

    @Override
    public void onCreate() {

       BsnlWifiActivity.initializeActivity(this);
        super.onCreate();
        //Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

       // Branch.getAutoInstance(this);
       // Branch branch = Branch.getInstance();
        BranchActivity.branchInstance(getApplicationContext());



    }

}
