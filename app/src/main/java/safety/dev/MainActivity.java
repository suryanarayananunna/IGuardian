package safety.dev;


import android.Manifest;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;




public class MainActivity extends AppCompatActivity implements ShakeDetector.Listener{



    GridLayout mainGrid;
    Contacting contacting = new Contacting();
    NotificationUtils notificationUtils = new NotificationUtils();


    private static final String TAG = "MainActivity";

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callPermissions();

        setTheme(R.style.AppTheme);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        ShakeDetector shakeDetector = new ShakeDetector(this);



        shakeDetector.start(sensorManager);




    }
    @Override
    public void onStart() {
        super.onStart();
        requestLocationUpdates();
    }
    @Override
    public void onPause() {
        super.onPause();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        ShakeDetector shakeDetector = new ShakeDetector(this);
        shakeDetector.start(sensorManager);
    }

    @Override
    public void onStop() {
        super.onStop();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        ShakeDetector shakeDetector = new ShakeDetector(this);
        shakeDetector.start(sensorManager);
    }








    //fused Location

    public void requestLocationUpdates() {

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            // Initialize Places.
            // Create a new Places client instance.

            if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                fusedLocationProviderClient = new FusedLocationProviderClient(this);


                locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setFastestInterval(2000);
                locationRequest.setInterval(4000);

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        contacting.getLocation(locationResult, getApplicationContext());
                    }

                }, getMainLooper());
            }
            else{
                buildAlertMessageNoGps();
            }
        }


    public void callPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS};
        String rationale = "Please provide required permissions";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Provide Permission")
                .setSettingsDialogTitle("Warning");

        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {

                //Display Notification
               // notificationUtils.displayNotification(getApplicationContext());

                requestLocationUpdates();


                mainGrid = (GridLayout) findViewById(R.id.mainGrid);

                //Set Event
                setSingleEvent(mainGrid);


            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                callPermissions();
            }

        });
    }


    private void setSingleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (finalI == 0) {

                        contacting.contactPolice(MainActivity.this);
                    } else if (finalI == 1) {

                        contacting.contactHosipital(MainActivity.this);

                    } else if (finalI == 2) {
                        contacting.contactFireDep(MainActivity.this);
                    } else {
                        contacting.contactEmergencyContacts(MainActivity.this);
                       // contacting.contactWomenSafety(MainActivity.this);

                    }
                }
            });
        }

    }

    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();


    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }


    @Override
    public void hearShake() {
        Log.i(TAG,"Shakeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        //contacting.contactEmergencyContacts(MainActivity.this);
        //contacting.contactWomenSafety(getApplicationContext());

        Toast.makeText(MainActivity.this,"Shake activity detected invoking a method",Toast.LENGTH_LONG).show();
        //contacting.send_sms(MainActivity.this,"contacts");
    }


    public void changeToAddContactsView(View view) {


        Intent intent = new Intent(MainActivity.this, AddEmergencyContactsActivity.class);
        startActivity(intent);

    }
}
//    https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=BB:2F:00:0F:EC:A1:19:74:2D:F5:A8:BF:37:C7:33:DE:CA:DC:52:8A%3Bcom.example.myapplication