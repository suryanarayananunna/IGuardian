package safety.dev;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Contacting extends AppCompatActivity {


    Double lat,lon;

  //String ApiKey = "AIzaSyDxzBQriUTlLvc_Cd4Dp4tbJkd8a0DBqKQ";   //coll156
  //String ApiKey = "AIzaSyA7zw60CBHiZPZARI9eowdZCZi_n6tp2S8"; //coll55
     //String ApiKey = "AIzaSyBlPyv3sA7v8OEzipw0u8u9wdNhQo1Whds"; //surya
   String ApiKey = "AIzaSyAzhl3_c3sKdxgNelNwvSayuPcxWxFuwKE";  //sourabh
  //String ApiKey = "AIzaSyA9GWA8gbpvJCl1Ajy5RCQcGbl-YYgk9fA";


    LocationResult locres;

    public void getLocation(LocationResult locationResult,Context context){
        locres = locationResult;
        final LocationResult loc = locationResult;
        lat = locationResult.getLastLocation().getLatitude();
        lon = locationResult.getLastLocation().getLongitude();
        //Toast.makeText(context,"Lat:"+locationResult.getLastLocation().getLatitude()+"Lon:"+locationResult.getLastLocation().getLongitude(),Toast.LENGTH_LONG).show();
        Log.e(TAG, "Lat:"+locationResult.getLastLocation().getLatitude()+"Lon:"+locationResult.getLastLocation().getLongitude() );

    }



    private void getPlacePhonoDetails(final Context context, final List<HashMap<String, String>> ParsedData) {

        Places.initialize(context, ApiKey);
        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(/*Place.Field.ID, Place.Field.NAME,*/ Place.Field.PHONE_NUMBER);

        //String[] placeIds = new String[ParsedData.size()];
        HashMap<String , String> placeids = new HashMap<String , String>();
        for(HashMap<String,String> i : ParsedData){
            placeids.put(i.get("place_id"),i.get("place_name"));
        }
        Log.i(TAG, placeids.toString());
        // Define a Place ID.
         String placeId = "ChIJj9uSb_CjNzoRSNDslyiH0ak";

        final HashMap<String , String> placePhoneDetails = new HashMap<String , String>();

        for (Map.Entry mapElement : placeids.entrySet()) {
            String key = (String) mapElement.getKey();
            final String value = (String) mapElement.getValue();
            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(key, placeFields);
            PlacesClient placesClient = Places.createClient(context);
            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    HashMap<String,String> hashMap = new HashMap<>();
                    Place place = response.getPlace();
                    Log.i(TAG, "Place found: " + place.getName() + place.getPhoneNumber());
                    hashMap.put("Name",place.getName());
                    hashMap.put("Phone Number",place.getPhoneNumber());
                    hashMap.put("Location",place.getAddress());
                    String pho = place.getPhoneNumber();
                    placePhoneDetails.put(value,pho);
                    Log.i(TAG,"Phone Number"+pho);
                    Log.i(TAG, placePhoneDetails.toString());



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    }
                }
            });
        }


        Iterator myVeryOwnIterator = placePhoneDetails.keySet().iterator();
        while(myVeryOwnIterator.hasNext()) {
            String key=(String)myVeryOwnIterator.next();
            String value=(String)placePhoneDetails.get(key);
            Toast.makeText(context, "Key: "+key+" Value: "+value, Toast.LENGTH_LONG).show();

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Contacting"+key+"with number:"+value)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {


                    dialog.cancel();
                    }
                })
                ;
        final AlertDialog alert = builder.create();
        alert.show();
        }
        Toast.makeText(context,"These are place names and phone_numbers : "+placePhoneDetails.toString(),Toast.LENGTH_LONG).show();
    }


    public void send_sms(final Context context, final String whotocontact) {

       final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Sending Alerts.........")
               .setMessage("Click cancel to stop sending the alerts!")
               .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                   @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: Add positive button action code here
                        if(whotocontact!="contacts")
                            getNearPlaces(context,whotocontact);
                        String num = "8328619345", txt = "I am in distress and my location is : https://www.google.com/maps/search/?api=1&query="+lat+","+lon;
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(num,null,txt,null,null);
                       Log.i(TAG,"sms sent");
                       Toast.makeText(context,"Alert message sent!",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: Add positive button action code here

                        Log.i(TAG,"sms sent");
                        Toast.makeText(context,"Alert message not sent!",Toast.LENGTH_LONG).show();
                    }
                })
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private static final int AUTO_DISMISS_MILLIS = 10000;
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                final CharSequence negativeButtonText = defaultButton.getText();
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                negativeButtonText,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                        ));
                       // Toast.makeText(context,"Alert stopped!",Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            dialog.dismiss();
                            //if(whotocontact!="contacts")
                            getNearPlaces(context,whotocontact);
                            String num = "9908418466", txt = "I am in distress and my location is : https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(num,null,txt,null,null);
                            Log.i(TAG, "sms sent");
                            Toast.makeText(context,"Alert message sent!",Toast.LENGTH_LONG).show();
                        }
                    }
                }.start();
            }
        });

         dialog.show();



    }



        private void getNearPlaces(final Context context , String place) {
        int PROXIMITY_RADIUS = 20000;
        String nearbyPlace = place;
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + lat + "," + lon);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + ApiKey);    //surya

        Log.i(TAG, "The url is: " + googlePlacesUrl.toString());

        String url = googlePlacesUrl.toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,  url,  null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG,"Response from the Near by Places API: " + response.toString());
                        DataParser dataParser = new DataParser();
                        List<HashMap<String, String>> ParsedData = dataParser.parsing(response.toString());
                        Log.i(TAG,"The data received from the parsing method:"+ ParsedData);
                        getPlacePhonoDetails(context,ParsedData);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i(TAG,"NO Response: ");

                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

        /*    RequestQueue requestQueue;

// Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);

// Start the queue
            requestQueue.start();



// Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Do something with the response

                            Log.i(TAG,"Response from the Near by Places API: " + response.toString());
                            DataParser dataParser = new DataParser();
                            List<HashMap<String, String>> ParsedData = dataParser.parsing(response.toString());
                            Log.i(TAG,"The data received from the parsing method:"+ ParsedData);
                            getPlacePhonoDetails(context,ParsedData);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                        }
                    });

// Add the request to the RequestQueue.
            requestQueue.add(stringRequest);*/



        }

    public void contactEmergencyContacts(Context context){
        getLocation(locres,context);
        Toast.makeText(context,"Contacting Emergency Contacts!",Toast.LENGTH_LONG).show();
        send_sms( context,"contacts");
        // getPlaceDetails(context);      Uses the placeid to get the phone number**************************************
        //System.out.println("contacts called");

    }



    public void contactPolice(Context context) {
        Toast.makeText(context,"Contacting near by Police station!",Toast.LENGTH_LONG).show();
        getNearPlaces(context,"police");
        send_sms(context,"police");
    }

    public void contactHosipital(Context context) {
        Toast.makeText(context,"Contacting near by Hospital!",Toast.LENGTH_LONG).show();
        getNearPlaces(context,"hospital");
        send_sms(context,"hospital");

    }

    public void contactFireDep(Context context) {
        Toast.makeText(context,"Contacting near by Fire Station",Toast.LENGTH_LONG).show();
        getNearPlaces(context,"fire_station");
        send_sms(context,"fire_station");

    }

    public void contactWomenSafety(Context context) {
        Toast.makeText(context,"Contacting Women Safety Contacts!",Toast.LENGTH_LONG).show();
        getNearPlaces(context,"police");
        send_sms(context,"contacts");
    }
}
