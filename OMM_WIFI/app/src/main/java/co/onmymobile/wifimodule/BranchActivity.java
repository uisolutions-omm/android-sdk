package co.onmymobile.wifimodule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import co.onmymobile.wifimodule.utils.Constants;
import co.onmymobile.wifimodule.utils.SdkHelper;
import co.onmymobile.wifimodule.utils.SdkLogger;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

/**
 * Created by onmymobile on 6/22/2017.
 */

public class BranchActivity extends AppCompatActivity implements Constants {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    //To create branch instance
    public static void branchInstance(Context context) {
        Context appContext;
        appContext = context;
        Branch.getAutoInstance(appContext);

    }

    // Branch initialization
    public static void branchInitialization(final Context context) {


        final Context context1;

        context1 = context;

        SdkHelper.showProgressDialog(context, "Please wait...");

        context1.getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
        Branch branch = Branch.getInstance(context1);
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                String packageName = context1.getPackageName();
                Intent launchIntent = context1.getPackageManager().getLaunchIntentForPackage(packageName);
                String className = launchIntent.getComponent().getClassName();
                String appName=context1.getApplicationInfo().loadLabel(context1.getPackageManager()).toString();
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    // params will be empty if no data found
                    // Toast.makeText(getApplicationContext(), referringParams.toString(), Toast.LENGTH_LONG).show();

                    try {

                        SdkLogger.e(Constants.BRANCHPARAM_ISWIFI);

                        if (referringParams.has(Constants.BRANCHPARAM_ISWIFI)) {
                            if (!referringParams.has(Constants.BRANCHPARAM_MOBILE)) {
                                WifiAppPreferences.add(WifiAppPreferences.PrefType.phnum, referringParams.getString("mobileNumber"), context1.getApplicationContext());
                                if (referringParams.getString(Constants.BRANCHPARAM_CHECKINNOW).equals("true")) {

                                    if (referringParams.has(Constants.BRANCHPARAM_DOMAIN)) {


                                        WifiAppPreferences.add(WifiAppPreferences.PrefType.checkInLater, Constants.PARAM_NO, context1);
                                        Intent intent = new Intent(context1, BsnlWifiActivity.class);
                                        intent.putExtra(Constants.MAIN_CLASSNAME, className);
                                        intent.putExtra(Constants.WIFIPARAM_PACKAGENAME, packageName);
                                        intent.putExtra(Constants.APP_NAME,appName);
                                        intent.putExtra(Constants.WIFIPARAM_MOBILE, referringParams.getString("mobileNumber"));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context1.startActivity(intent);
                                    } else {
                                         if(referringParams.getString(Constants.BRANCHPARAM_DOMAIN).equals(Constants.BRANCHPARAM_DONATION)) {
                                           String name=referringParams.getString(Constants.BRANCHDONATIONPARAM_NAME);
                                           String mobileNumber=referringParams.getString(BRANCHDONATIONPARAMMOBILE);
                                           String email=referringParams.getString(BRANCHDONATIONPARAMEMAIL);
                                           String panNumber=referringParams.getString(BRANCHDONATIONPARAMPANNUMBER);
                                        Intent intent = new Intent(context1, DonationActivity.class);
                                        intent.putExtra(Constants.MAIN_CLASSNAME, className);
                                        intent.putExtra(Constants.WIFIPARAM_PACKAGENAME, packageName);
                                        intent.putExtra(Constants.DONATIONPARAM_NAME, name);
                                        intent.putExtra(Constants.DONATIONPARAM_EMAIL, email);
                                        intent.putExtra(DONATIONPARAM_MOBILE, mobileNumber);
                                        intent.putExtra(Constants.APP_NAME,appName);
                                        intent.putExtra(DONATIONPARAM_PANNUMBER, panNumber);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context1.startActivity(intent);
                                        }
                                    }

                                } else {

                                    WifiAppPreferences.add(WifiAppPreferences.PrefType.checkInLater, Constants.PARAM_YES, context1);
                                    Intent intent = new Intent(context1, BsnlWifiActivity.class);
                                    intent.putExtra(Constants.WIFIPARAM_MOBILE, "9999999999");
                                    intent.putExtra(Constants.FIRST_RUN, Constants.TRUE);
                                    //intent.putExtra("mobile", referringParams.getString(Constants.BRANCHDONATIONPARAMMOBILE));
                                    intent.putExtra(WIFIPARAM_PACKAGENAME, packageName);
                                   SdkHelper.dismissProgressDialog();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context1.startActivity(intent);

                                }

                            } else {
                                WifiAppPreferences.add(WifiAppPreferences.PrefType.checkInLater, Constants.APP_STORE, context1);
                                Intent intent = new Intent(context1, Class.forName(className));
                                SdkHelper.dismissProgressDialog();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context1.startActivity(intent);

                            }
                        } else {
                            WifiAppPreferences.add(WifiAppPreferences.PrefType.checkInLater, Constants.APP_STORE, context1);
                            Intent intent = new Intent(context1, Class.forName(className));
                            SdkHelper.dismissProgressDialog();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context1.startActivity(intent);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        SdkHelper.dismissProgressDialog();
                        Intent intent = null;
                        try {
                            intent = new Intent(context1, Class.forName(className));
                        } catch (ClassNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        assert intent != null;
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context1.startActivity(intent);

                    }


                } else {
                    SdkHelper.dismissProgressDialog();


                    Intent intent = null;
                    try {
                        intent = new Intent(context1, Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context1.startActivity(intent);


                }


            }


            //Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
            //.show();
        });

    }
}
