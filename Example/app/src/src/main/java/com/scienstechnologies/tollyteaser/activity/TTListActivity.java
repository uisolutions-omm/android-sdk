package com.scienstechnologies.tollyteaser.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.scienstechnologies.tollyteaser.R;
import com.scienstechnologies.tollyteaser.activity.*;
import com.scienstechnologies.tollyteaser.adapter.TTListAdapter;
import com.scienstechnologies.tollyteaser.model.List;
import com.scienstechnologies.tollyteaser.model.ListModel;
import com.scienstechnologies.tollyteaser.navigationdrawer.FragmentDrawer;

import co.onmymobile.wifimodule.BsnlWifiActivity;
import co.onmymobile.wifimodule.LocationActivity;
import co.onmymobile.wifimodule.WifiAppPreferences;

public class TTListActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    Firebase ref;
    ArrayList<ListModel> listArray;
    ListView listView;
    DrawerLayout drawerLayout;
    private FragmentDrawer drawerFragment;
    Intent mIntent = null;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttlist);
        textView=(TextView)findViewById(R.id.text);
        listView = (ListView) findViewById(R.id.tollyteaser_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TollyTeaser");
        final ProgressDialog progressDialog = new ProgressDialog(TTListActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseAuth firebase = FirebaseAuth.getInstance();
        final Drawable navigationDrawer = getResources().getDrawable(R.mipmap.menu);
        navigationDrawer.setColorFilter(getResources().getColor(R.color.blackColor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(navigationDrawer);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener((FragmentDrawer.FragmentDrawerListener) this);
        drawerFragment.onDestroy();

        Firebase.setAndroidContext(this);
        FirebaseOptions options;
        try {
            options = new FirebaseOptions.Builder()
                    .setApplicationId("1:202877477493:android:aa8f5f8622adc993") // Required for Analytics.
                    .setApiKey("AIzaSyC2IiwspoEFOcd8VpawZFZtw3kiyGDMvlo") // Required for Auth.
                    .setDatabaseUrl("https://tollyteaser-2f8cf.firebaseio.com") // Required for RTDB.
                    .build();
            FirebaseApp.initializeApp(this, options, "primary");

        } catch (Exception e) {
            Log.e("illegalException", "" + e);
        }

        FirebaseApp app = FirebaseApp.getInstance("primary");
        Log.e("Firebase",""+FirebaseAuth.getInstance(app).getCurrentUser());
        Log.e("Db",""+FirebaseDatabase.getInstance(app).getReference());
        DatabaseReference reference = FirebaseDatabase.getInstance(app).getReferenceFromUrl(getResources().getString(R.string.firebase_url));
        DatabaseReference listRef = reference.child("list");
        //fetching data from firebase
        Log.e("Reference",""+listRef);
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listArray = new ArrayList<ListModel>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

Log.e("snap",""+snapshot);
                    ListModel listModel = new ListModel();

                    List list = new List();
                    list = snapshot.getValue(List.class);

                    listModel.setTitle(list.getTitle());
                    listModel.setDescription(list.getDescription());
                    listModel.setImage_url(list.getImage_url());
                    listArray.add(listModel);
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                textView.setVisibility(View.GONE);
                if(listArray.size()>0) {
                    TTListAdapter listAdapter = new TTListAdapter(TTListActivity.this, R.layout.ttlist_item, listArray);
                    listView.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("entered","error");
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                textView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        //String title = getString(R.string.app_name);


        switch (position) {
            case 0:
                String checkInLater= WifiAppPreferences.getString(WifiAppPreferences.PrefType.checkInLater,getApplicationContext());
                if(checkInLater!=null) {
                    if (checkInLater.equals("Y")) {
                        mIntent = new Intent(this, LocationActivity.class);
                        startActivity(mIntent);

                    } else if (checkInLater.equals("AppStore"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(TTListActivity.this).create(); //Read Update
                        alertDialog.setMessage("You are not eligible for this offer !");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = null;
                                try {
                                    intent = new Intent(TTListActivity.this, Class.forName("com.scienstechnologies.tollyteaser.activity.TTListActivity"));

                                    startActivity(intent);
                                    finish();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                        alertDialog.show();
                        // alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(co.onmymobile.wifimodule.R.color.colorPrimary));
                       // alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(co.onmymobile.wifimodule.R.color.colorPrimary));
                        //Toast.makeText(TTListActivity.this,"You are not eligible for this offer !",Toast.LENGTH_LONG).show();
                    }
                    else{
                        AlertDialog alertDialog = new AlertDialog.Builder(TTListActivity.this).create(); //Read Update
                        alertDialog.setMessage("You have already redeemed your coupon !");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = null;
                                try {
                                    intent = new Intent(TTListActivity.this, Class.forName("com.scienstechnologies.tollyteaser.activity.TTListActivity"));

                                    startActivity(intent);
                                    finish();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                        alertDialog.show();
                       // Toast.makeText(TTListActivity.this,"You have already redeemed your coupon !",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                   // Toast.makeText(TTListActivity.this,"You are not eligible to this offer !",Toast.LENGTH_LONG).show();
                    AlertDialog alertDialog = new AlertDialog.Builder(TTListActivity.this).create(); //Read Update
                    alertDialog.setMessage("You are not eligible for this offer !");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = null;
                            try {
                                intent = new Intent(TTListActivity.this, Class.forName("com.scienstechnologies.tollyteaser.activity.TTListActivity"));

                                startActivity(intent);
                                finish();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                    alertDialog.show();
                }
                break;


            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }

    }
}
