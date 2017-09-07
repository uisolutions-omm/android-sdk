package co.onmymobile.wifimodule;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.onmymobile.wifimodule.model.Contact;
import co.onmymobile.wifimodule.utils.Constants;
import co.onmymobile.wifimodule.utils.SdkHelper;


public class BsnlWifiActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private RequestQueue mRequestQueue;
    private String phoneNumber;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private AlertDialog alertDialog;
    private AlertDialog alertDialog1;
    private String checkInLater;
    private String packagename;
    private String className;
    private String appName;

    private FirebaseDatabase secondaryDatabase;

    static {
        System.loadLibrary(Constants.NDKKEYS);
    }


    public native String getSDKFirebaseAPI();

    public native String getSDKFirebaseAppId();

    public native String getSDKFirebaseUrl();

    public native String getSDKProductionUrl();

    public native String getSDKProductionUploadContactsUrl();

    public native String getSecretCode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bsnl);
        SdkHelper.showProgressDialog(this, getResources().getString(R.string.please_wait));
        CleverTapAPI cleverTap = null;
        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }


        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(getSDKFirebaseAppId()) // Required for Analytics.
                    .setApiKey(getSDKFirebaseAPI()) // Required for Auth.
                    .setDatabaseUrl(getSDKFirebaseUrl()) // Required for RTDB.
                    .build();
            FirebaseApp.initializeApp(this, options, Constants.SECONDARY);

        } catch (Exception e) {

        }

        FirebaseApp app = FirebaseApp.getInstance(Constants.SECONDARY);
        mAuth = FirebaseAuth.getInstance(app);
        secondaryDatabase = FirebaseDatabase.getInstance(app);


        //Branch.getAutoInstance(this);
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        if (getIntent().getExtras() != null) {
            phoneNumber = getIntent().getStringExtra(Constants.WIFIPARAM_MOBILE);
            packagename = getIntent().getStringExtra(Constants.WIFIPARAM_PACKAGENAME);
            className = getIntent().getStringExtra(Constants.MAIN_CLASSNAME);
            appName=getIntent().getStringExtra(Constants.APP_NAME);

        }


        String reg = WifiAppPreferences.getString(WifiAppPreferences.PrefType.reg, getApplicationContext());
        checkInLater = WifiAppPreferences.getString(WifiAppPreferences.PrefType.checkInLater, getApplicationContext());
        final String state = WifiAppPreferences.getString(WifiAppPreferences.PrefType.wifi, getApplicationContext());
        WifiAppPreferences.add(WifiAppPreferences.PrefType.phnum, phoneNumber, getApplicationContext());
        final String email = phoneNumber + Constants.EMAIL_ATTACH;
        //comment out after integration
     /*   packagename=packagename.replace(".","-");
        if(packagename!=null) {
            DatabaseReference database = secondaryDatabase.getReferenceFromUrl("https://sdkproject-8f693.firebaseio.com");
            DatabaseReference integration = database.child("IntegratedApps").child(packagename);
            HashMap map=new HashMap();
            map.put("Android","done");
            integration.updateChildren(map);

        }
*/
        final CleverTapAPI finalCleverTap = cleverTap;
        mAuth.signInWithEmailAndPassword(email, phoneNumber).addOnCompleteListener(BsnlWifiActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    SdkHelper.dismissProgressDialog();


                    String firstRun = null;
                    if (getIntent().getExtras() != null) {
                        firstRun = getIntent().getStringExtra(Constants.FIRST_RUN);

                    }

                    //String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (firstRun != null) {

                        Intent intent = null;
                        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                        profileUpdate.put("Name", "Test ");                  // String
                        profileUpdate.put("Identity", 6178913);                    // String or number
                        profileUpdate.put("Email", "jack@gmail.com");               // Email address of the user
                        profileUpdate.put("Phone", phoneNumber);                 // Phone (with the country code, starting with +)
                        profileUpdate.put("Gender", "M");                           // Can be either M or F
                        profileUpdate.put("Employed", "Y");                         // Can be either Y or N
                        profileUpdate.put("Education", "Graduate");                 // Can be either Graduate, College or School
                        profileUpdate.put("Married", "Y");                          // Can be either Y or N
                        profileUpdate.put("DOB", new Date());                       // Date of Birth. Set the Date object to the appropriate value first
                        profileUpdate.put("Age", 28);                               // Not required if DOB is set
                        profileUpdate.put("Tz", "Asia/Kolkata");                    //an abbreviation such as "PST", a full name such as "America/Los_Angeles",
                        //or a custom ID such as "GMT-8:00"
                        profileUpdate.put("Photo", "www.foobar.com/image.jpeg");    // URL to the Image

// optional fields. controls whether the user will be sent email, push etc.
                        profileUpdate.put("MSG-email", false);                      // Disable email notifications
                        profileUpdate.put("MSG-push", true);                        // Enable push notifications
                        profileUpdate.put("MSG-sms", false);                        // Disable SMS notifications

                        finalCleverTap.profile.push(profileUpdate);

                        CleverTapAPI.setDebugLevel(1);


                        try {
                            intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                            SdkHelper.dismissProgressDialog();
                            startActivity(intent);
                            finish();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                    } else {


                        getCoupons(phoneNumber);
                    }


                }
                if (!task.isSuccessful()) {
                    mAuth.createUserWithEmailAndPassword(email, phoneNumber).addOnCompleteListener(BsnlWifiActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Preferences.add(Preferences.PrefType.P_Launch, "1", getApplicationContext());
                                // Toast.makeText(SigninActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                if (checkInLater != null) {
                                    if (checkInLater.equals(Constants.PARAM_NO)) {
                                        SdkHelper.dismissProgressDialog();
                                        getCoupons(phoneNumber);
                                    } else {

                                        Intent intent = null;
                                        try {
                                            intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                                            SdkHelper.dismissProgressDialog();
                                            startActivity(intent);
                                            finish();
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                } else {

                                    Intent intent = null;
                                    try {
                                        intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                                        SdkHelper.dismissProgressDialog();
                                        startActivity(intent);
                                        finish();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }


                                }

                                WifiAppPreferences.add(WifiAppPreferences.PrefType.reg, Constants.PARAM_YES, getApplicationContext());

                            }
                            if (!task.isSuccessful()) {
                                try {
                                    //  Preferences.add(Preferences.PrefType.P_Launch, "0", getApplicationContext());
                                    String error = task.getException().getMessage();
                                    Toast.makeText(BsnlWifiActivity.this, error, Toast.LENGTH_SHORT).show();
                                    Intent intent = null;
                                    try {
                                        intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                                        SdkHelper.dismissProgressDialog();
                                        startActivity(intent);
                                        finish();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }


                                } catch (Exception e) {
                                    Log.e("catch", "error" + e.getMessage());
                                    Intent intent = null;
                                    try {
                                        intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                                        SdkHelper.dismissProgressDialog();
                                        startActivity(intent);
                                        finish();
                                    } catch (ClassNotFoundException e1) {
                                        e1.printStackTrace();
                                    }


                                }
                            }
                        }
                    });
                }
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener()

        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    WifiAppPreferences.add(WifiAppPreferences.PrefType.reg, Constants.PARAM_YES, getApplicationContext());

                }
            }
        };


    }

    public void getCoupons(String phone) {
        final String phoneNumber;
        phoneNumber = phone;

        FirebaseAuth.getInstance().signOut();
        final String api = getSDKProductionUrl();
        final StringRequest stringReq = new StringRequest(Request.Method.POST, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.contains(Constants.SUCCESS)) {
                    WifiAppPreferences.add(WifiAppPreferences.PrefType.checkInLater, Constants.PARAM_NO, getApplicationContext());
                    alertDialog = new AlertDialog.Builder(BsnlWifiActivity.this).create(); //Read Update
                    alertDialog.setMessage(getResources().getString(R.string.successRecharge));
                    alertDialog.setCanceledOnTouchOutside(false);
                    SdkHelper.dismissProgressDialog();
                    alertDialog.setButton(Dialog.BUTTON_POSITIVE, Constants.DIALOG_OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();


                            alertDialog1 = new AlertDialog.Builder(BsnlWifiActivity.this).create();
                            alertDialog1.setMessage(getResources().getString(R.string.uploadContacts));
                            alertDialog1.setCanceledOnTouchOutside(false);
                            alertDialog1.setButton(Dialog.BUTTON_POSITIVE, Constants.DIALOG_OK, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int which) {
                                    dialog1.dismiss();
                                    String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
                                    if (!hasPermissions(BsnlWifiActivity.this, PERMISSIONS)) {
                                        ActivityCompat.requestPermissions(BsnlWifiActivity.this, PERMISSIONS, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                                    } else {
                                        getContacts(phoneNumber);
                                        Intent intent = null;
                                        try {
                                            intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                                            startActivity(intent);
                                            finish();
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            alertDialog1.setButton(Dialog.BUTTON_NEGATIVE, Constants.DIALOG_NO, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int which) {
                                    dialog1.dismiss();
                                    Intent intent = null;
                                    try {
                                        intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                                        startActivity(intent);
                                        finish();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            alertDialog1.show();
                            alertDialog1.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(co.onmymobile.wifimodule.R.color.colorPrimary));
                            alertDialog1.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(co.onmymobile.wifimodule.R.color.colorPrimary));


                        }
                    });
                    alertDialog.show();
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(co.onmymobile.wifimodule.R.color.colorPrimary));


                } else if (response.contains(getResources().getString(R.string.redeemed))) {
                    WifiAppPreferences.add(WifiAppPreferences.PrefType.checkInLater, Constants.PARAM_NO, getApplicationContext());

                    alertDialog = new AlertDialog.Builder(BsnlWifiActivity.this).create(); //Read Update
                    alertDialog.setMessage(getResources().getString(R.string.alreadyRedeemed));
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setButton(Dialog.BUTTON_POSITIVE, Constants.DIALOG_OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = null;
                            try {
                                intent = new Intent(BsnlWifiActivity.this, Class.forName(className));

                                startActivity(intent);
                                finish();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                    alertDialog.show();
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(co.onmymobile.wifimodule.R.color.colorPrimary));

                } else {
                    SdkHelper.dismissProgressDialog();
                    WifiAppPreferences.add(WifiAppPreferences.PrefType.checkInLater, Constants.PARAM_YES, getApplicationContext());
                    alertDialog = new AlertDialog.Builder(BsnlWifiActivity.this).create(); //Read Update
                    alertDialog.setMessage(getResources().getString(R.string.somethingWentWrong));
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setButton(Dialog.BUTTON_POSITIVE, Constants.DIALOG_OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = null;
                            try {
                                intent = new Intent(BsnlWifiActivity.this, Class.forName(className));

                                startActivity(intent);
                                finish();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    alertDialog.show();
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(co.onmymobile.wifimodule.R.color.colorPrimary));
                }
            }


            //do other things with the received JSONObject

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SdkHelper.dismissProgressDialog();
                WifiAppPreferences.add(WifiAppPreferences.PrefType.checkInLater, "Y", getApplicationContext());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                Intent intent = null;
                try {
                    intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                    startActivity(intent);
                    finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put(Constants.CONTENT_TYPE, Constants.URL_ENCODED);
                return pars;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put(Constants.PHONE_NUMBER, phoneNumber);
                pars.put(Constants.SECRET_CODE, getSecretCode());
                return pars;
            }
        };
        stringReq.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringReq.setShouldCache(true);

        //add to the request queue

        mRequestQueue.add(stringReq);
    }

    public void getContacts(final String phoneNo) {
        final String phoneNumber;
        phoneNumber = phoneNo;
        ArrayList<Contact> contacts = new ArrayList<>();
        ArrayList<String> phoneNumbers = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //Query phone here.  Covered next
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        // Do something with phones
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        phone = phone.replaceAll("\\s+", "");
                        String name1 = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Contact contact = new Contact();
                        contact.setName(name1);
                        contact.setPhoneNumber(phone);
                        contacts.add(contact);
                        phoneNumbers.add(phone);


                    }
                }
            }
        }
        final JSONArray jsonArray = new JSONArray(phoneNumbers);
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        final String api = getSDKProductionUploadContactsUrl();
        final StringRequest stringReq = new StringRequest(Request.Method.POST, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SdkHelper.dismissProgressDialog();
                if (response.contains(Constants.SUCCESS)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.successfully_uploaded), Toast.LENGTH_LONG).show();
                }
                //do other things with the received JSONObject
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SdkHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put(Constants.CONTENT_TYPE, Constants.URL_ENCODED);
                return pars;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put(Constants.ARRAY, jsonArray.toString());
                pars.put(Constants.WIFIPARAM_MOBILE, phoneNo);
                pars.put(Constants.APP_NAME, appName);
                pars.put(Constants.SECRET_CODE, getSecretCode());

                return pars;
            }
        };
        stringReq.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringReq.setShouldCache(true);
        SdkHelper.showProgressDialog(this,getResources().getString(R.string.please_wait));
        //add to the request queue

        mRequestQueue.add(stringReq);


    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public static boolean hasPermissions(BsnlWifiActivity context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts.csv-related task you need to do.

                    getContacts(phoneNumber);

                    Intent intent = null;
                    try {
                        intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    SdkHelper.dismissProgressDialog();

                    startActivity(intent);
                    finish();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    SdkHelper.dismissProgressDialog();
                    Intent intent = null;
                    try {
                        intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    startActivity(intent);
                    finish();
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alertDialog1 != null && alertDialog1.isShowing()) {
            alertDialog1.cancel();
            Intent intent = null;
            try {
                intent = new Intent(BsnlWifiActivity.this, Class.forName(className));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            SdkHelper.dismissProgressDialog();
            startActivity(intent);
            finish();
        }
    }

    public static void initializeActivity(Application myapplication) {
        Application application;
        application = myapplication;
        ActivityLifecycleCallback.register(application);
    }


}