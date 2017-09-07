package co.onmymobile.wifimodule;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import co.onmymobile.wifimodule.utils.Constants;
import co.onmymobile.wifimodule.utils.SdkHelper;
import co.onmymobile.wifimodule.utils.SdkLogger;

/**
 * Created by onmymobile on 6/1/2017.
 */

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private float dist;
    private Location mylocation;
    private ArrayList<String> locations;
    private String mobileNumber;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private String loc = null;
    private String className;

    static {
        System.loadLibrary(Constants.NDKKEYS);
    }

    private native String getMobileNumber();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mobileNumber = WifiAppPreferences.getString(WifiAppPreferences.PrefType.phnum, getApplicationContext());
        SdkHelper.showProgressDialog(this, getResources().getString(R.string.please_wait_access));
        if (getIntent().getExtras() != null) {
            className = getIntent().getStringExtra(Constants.MAIN_CLASSNAME);
        }
        locations = new ArrayList<>();
        try {
            InputStreamReader is = new InputStreamReader(getAssets()
                    .open(getResources().getString(R.string.locations_csv)));
            BufferedReader reader = new BufferedReader(is);
            //reader.readLine();
            String line;
            String[] b = null;
            while ((line = reader.readLine()) != null) {

                locations.add(line);

            }
        } catch (Exception e) {

        }
        setUpGClient();

    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(LocationActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    private void getMyLocation() {

        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(LocationActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(LocationActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                        // Log.e("suceesss", "" + mylocation);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(LocationActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    //finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code here
            try {
                Intent intent = new Intent(LocationActivity.this, Class.forName(className));
                startActivity(intent);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(LocationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        checkPermissions();
    }


    @Override
    public synchronized void onLocationChanged(Location location) {

        mylocation = location;
        if (mylocation != null && loc == null) {

            loc = "entered";

            Double latitude = mylocation.getLatitude();
            Double longitude = mylocation.getLongitude();
            // Toast.makeText(getApplicationContext(), latitude + "--" + longitude, Toast.LENGTH_SHORT).show();
            SdkLogger.e(latitude.toString());

            if (locations.size() > 0) {
                for (int i = 0; i < locations.size(); i++) {
                    String lat = locations.get(i).toString();
                    //Log.e("LatAndLong",""+lat);
                    String[] lon = lat.split(",");
                    String preLats = lon[0];
                    String preLong = lon[1];
                    Location areaOfIinterest = new Location(Constants.RADIUS);
                    Location currentPosition = new Location(Constants.RADIUS);

                    areaOfIinterest.setLatitude(Double.parseDouble(preLats));
                    areaOfIinterest.setLongitude(Double.parseDouble(preLong));

                    currentPosition.setLatitude(latitude);
                    currentPosition.setLongitude(longitude);

                    dist = areaOfIinterest.distanceTo(currentPosition);
                    Log.e("distance", "" + dist);
                    if (dist < 200) {
                        SdkHelper.dismissProgressDialog();

                        if (SdkHelper.checkNetwork(this)) {
                            Intent intent = new Intent(getApplicationContext(), BsnlWifiActivity.class);
                            intent.putExtra("mobile", mobileNumber);
                            startActivity(intent);
                            finish();
                        } else {
                            final String number = getMobileNumber();
                            AlertDialog alertDialog = new AlertDialog.Builder(LocationActivity.this).create(); //Read Update
                            alertDialog.setMessage("Looks like you don't have internet connection please give a missed call to this number" + " " + number + " " + "to get your coupon !");
                            alertDialog.setCanceledOnTouchOutside(false);

                            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Call", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                    //alertDialog.dismiss();
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:" + number));
                                    if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    startActivity(intent);
                                    //finish();
                                }
                            });
                            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, Constants.DIALOG_CANCEL, new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //alertDialog.dismiss();

                                    Intent intent = null;
                                    try {
                                        intent = new Intent(LocationActivity.this, Class.forName(className));

                                        startActivity(intent);
                                        finish();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
                            alertDialog.show();

                        }

                    }

                }
                if (dist > 200) {
                    SdkHelper.dismissProgressDialog();

                    Toast.makeText(getApplicationContext(), Constants.NOT_IN_QFI_LOCATION, Toast.LENGTH_SHORT).show();
                    Intent intent = null;
                    try {
                        intent = new Intent(getApplicationContext(), Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    startActivity(intent);
                    finish();
                }


            }

        }
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
