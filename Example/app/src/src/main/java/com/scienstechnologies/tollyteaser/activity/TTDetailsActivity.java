package com.scienstechnologies.tollyteaser.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scienstechnologies.tollyteaser.navigationdrawer.FragmentDrawer;
import com.squareup.picasso.Picasso;

import com.scienstechnologies.tollyteaser.R;

import co.onmymobile.wifimodule.utils.Constants;
import co.onmymobile.wifimodule.LocationActivity;
import co.onmymobile.wifimodule.WifiAppPreferences;

/**
 * Created by onmymobile on 5/17/2017.
 */

public class TTDetailsActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private String title;
    private String image;
    private String description;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView imageView;
    private DrawerLayout drawerLayout;
    private FragmentDrawer drawerFragment;
    private Intent mIntent = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttdetails);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TollyTeaser"); //setting title for actionbar
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

        titleTextView = (TextView) findViewById(R.id.ttdetails_title);
        descriptionTextView = (TextView) findViewById(R.id.ttdetails_description);
        imageView = (ImageView) findViewById(R.id.ttdetails_image);

        // Getting data from another activity
        if (getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("title");
            image = getIntent().getStringExtra("image");
            description = getIntent().getStringExtra("description");
        }
        titleTextView.setText(title);
        Picasso.with(getApplicationContext()).load(image).resize(400, 400).error(R.mipmap.tollyteaser).into(imageView);
        descriptionTextView.setText(description);

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
                    String packageName = this.getPackageName();
                    Intent launchIntent = this.getPackageManager().getLaunchIntentForPackage(packageName);
                    final String className = launchIntent.getComponent().getClassName();
                    if(checkInLater!=null) {

                        if (checkInLater.equals("Y")) {

                            mIntent = new Intent(this, LocationActivity.class);
                            mIntent.putExtra(Constants.MAIN_CLASSNAME,className);
                            startActivity(mIntent);

                        } else if (checkInLater.equals("AppStore"))
                        {
                            AlertDialog alertDialog = new AlertDialog.Builder(TTDetailsActivity.this).create(); //Read Update
                            alertDialog.setMessage("You are not eligible for this offer !");
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = null;
                                    try {
                                        intent = new Intent(TTDetailsActivity.this, Class.forName(className));
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
                            AlertDialog alertDialog = new AlertDialog.Builder(TTDetailsActivity.this).create(); //Read Update
                            alertDialog.setMessage("You have already redeemed your coupon !");
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = null;
                                    try {
                                        intent = new Intent(TTDetailsActivity.this, Class.forName(className));

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
                        AlertDialog alertDialog = new AlertDialog.Builder(TTDetailsActivity.this).create(); //Read Update
                        alertDialog.setMessage("You are not eligible for this offer !");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = null;
                                try {
                                    intent = new Intent(TTDetailsActivity.this, Class.forName(className));

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
