package com.satellite.gps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,View.OnClickListener {


    private GoogleMap mMap;
    final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    GoogleApiClient mGoogleApiClient;
    String destiny, sour, poly;
    String slat, slong, dlat, dlong;
    Double slatdd, slongdd, dlatdd, dlongdd;
    List<LatLng> waypoints;
    String total_time, total_distance;
    TextView ttdistance, tttime;

    ArrayList<String> placese_auto;
    Polyline line;
    ProgressDialog pd;







    Context mContext;


    String destination,source;
    AutoCompleteTextView etdestination,etsource;
    ImageView ivSearch;
    private String location_route = "";

    PolylineOptions polylineOptions;

    double srclat, srclng, deslat, deslng;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    SupportMapFragment mapFragment;

    InterstitialAd mInterstitialAd;
    AdRequest adIRequest;
    AdView adView;

    RadioButton normal,satellite,hybrid,terrain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adbRequest = new AdRequest.Builder()
                .addTestDevice("5900ABC73E0193C5C3BCDA48AF95F170")
               .build();
        adView.loadAd(adbRequest);



        etdestination = (AutoCompleteTextView) findViewById(R.id.etdestination);
        etsource = (AutoCompleteTextView) findViewById(R.id.etsource);
        ivSearch = (ImageView) findViewById(R.id.ivsearch);
        normal = (RadioButton) findViewById(R.id.normal);
        hybrid = (RadioButton) findViewById(R.id.hybrid);
        terrain = (RadioButton) findViewById(R.id.terrain);
        satellite = (RadioButton) findViewById(R.id.satellite);
        pd = new ProgressDialog(this);
        normal.setOnClickListener(this);
        hybrid.setOnClickListener(this);
        satellite.setOnClickListener(this);
        terrain.setOnClickListener(this);
        normal.setChecked(true);

        placese_auto = new ArrayList<>();

        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
         adIRequest = new AdRequest.Builder()
                .build();

        polylineOptions = new PolylineOptions();

        if (mMap == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MainActivity.this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.

            }
        }



        mContext = this;

        //Checked googleplayservices in case if available, initializegoogleapiclient.
        if (checkPlayServices()) {
            initializegoogleapiclient();
        }



        etsource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, final int count) {
                location_route = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + s + "&types=(cities)&key=AIzaSyBTJIfZUXveyCuejqLtF11MN05pK-8H3LQ";
                //location_route="https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+s+"&types=(cities)&key=AIzaSyBI7lBqgozreNnDhlGERQIogYZ63LVfPmo";
                Log.e("Log",location_route+"");


                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, location_route,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                showJSONprediction(response);
                                Log.i("Log","source response"+response);
                                final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, placese_auto);
                                etsource.setAdapter(adapter);
                                // final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,placese_auto);
                                // tv_autocomplete.setAdapter(adapter);

                                Toast toast = Toast.makeText(getApplication(), response, Toast.LENGTH_LONG);
//                                                     toast.show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(second_activity.this,error.toString(),Toast.LENGTH_LONG).show();
                            }
                        });

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etdestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, final int count) {
                location_route = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + s + "&types=(cities)&key=AIzaSyBTJIfZUXveyCuejqLtF11MN05pK-8H3LQ";
                //location_route="https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+s+"&types=(cities)&key=AIzaSyBI7lBqgozreNnDhlGERQIogYZ63LVfPmo";
                final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, placese_auto);
                etdestination.setAdapter(adapter);

                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, location_route,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                showJSONprediction(response);
                                // final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,placese_auto);
                                // tv_autocomplete.setAdapter(adapter);

                                Toast toast = Toast.makeText(getApplication(), response, Toast.LENGTH_LONG);
//                                                     toast.show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(second_activity.this,error.toString(),Toast.LENGTH_LONG).show();
                            }
                        });

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });






        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Load ads into Interstitial Ads
                mInterstitialAd.loadAd(adIRequest);

                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        showInterstitial();
                    }
                });

                drawLine();
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }



    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(MainActivity.this, "Error:" + e, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, "Error: Location services connection failed with code=" + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }




    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode,
                        this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void initializegoogleapiclient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }





    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.satellite:
                normal.setChecked(false);
                hybrid.setChecked(false);
                terrain.setChecked(false);

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Load ads into Interstitial Ads
                mInterstitialAd.loadAd(adIRequest);

                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        showInterstitial();
                    }
                });

                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                satellite.setChecked(false);
                hybrid.setChecked(false);
                terrain.setChecked(false);
                inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Load ads into Interstitial Ads
                mInterstitialAd.loadAd(adIRequest);

                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        showInterstitial();
                    }
                });

                break;
            case R.id.hybrid:
                normal.setChecked(false);
                satellite.setChecked(false);
                terrain.setChecked(false);
                inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Load ads into Interstitial Ads
                mInterstitialAd.loadAd(adIRequest);

                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        showInterstitial();
                    }
                });

                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.terrain:
                normal.setChecked(false);
                hybrid.setChecked(false);
                satellite.setChecked(false);
                 inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Load ads into Interstitial Ads
                mInterstitialAd.loadAd(adIRequest);

                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        showInterstitial();
                    }
                });

                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }

    }

    void drawLine(){
        mMap.clear();
        if (!Utill.verifyConection(MainActivity.this)){
            Toast.makeText(MainActivity.this, "Network Not Available", Toast.LENGTH_SHORT).show();
            return;
        }
        if (line != null) {
            line.remove();
        }
        if (etsource.getText().toString().equals("")) {
            etsource.setError("Required Field");
        } else if (etdestination.getText().toString().equals("")) {
            etdestination.setError("Required Field");
        } else {

            String sourcelocation = etsource.getText().toString();
            String[] arr = sourcelocation.split(",");
            String source_removed_comma_data = arr[0];

            if (Utill.verifyConection(MainActivity.this)) {

                requestingSoureceLatLng(source_removed_comma_data);
            }else {
                Toast.makeText(MainActivity.this, "Network Not Available", Toast.LENGTH_SHORT).show();
            }



        }


    }

    private void requestingSoureceLatLng(final String sourceData) {

        class requestData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {

                pd.setTitle("Finding Your Path");
                pd.setMessage("Please Wait..");
                pd.show();
                pd.setCancelable(false);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                String latLng = null;

                String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + URLEncoder.encode(sourceData) + "&key=AIzaSyC1K9py0hD9-q_yty4mNAdy21johpH-l5Q";
                Log.e("sourcelatlng:",url+"");
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;


                try {

                    URL url1 = new URL(url);

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url1.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {

                        buffer.append(line).append("\n");
                    }

                    Log.d("myResponse", buffer.toString());


                    JSONObject jsonObject = new JSONObject(buffer.toString());
                    JSONArray routes_array = jsonObject.getJSONArray("results");
                    JSONObject result_object = routes_array.getJSONObject(0);
                    JSONObject geometry = result_object.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    String source_method_lat = location.getString("lat");
                    String source_method_long = location.getString("lng");


                    latLng = source_method_lat + "," + source_method_long;


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return latLng;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                pd.dismiss();
                if (s==null){
                    Utill.showToast("No data received..!",MainActivity.this);
                }else {
                    String[] array = s.split(",");
                    slatdd = Double.parseDouble(array[0]);
                    slongdd = Double.parseDouble(array[1]);

                    String destiny = etdestination.getText().toString();
                    String[] array1 = destiny.split(",");
                    String destination_removed_comma_data = array1[0];

                    requestingDestinationLatLng(destiny);

                }


            }
        }


        new requestData().execute();
    }


    private void requestingDestinationLatLng(final String sourceData) {
        //                         param coming,progress,result
        class requestData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {

                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = null;

                String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + URLEncoder.encode(sourceData) + "&key=AIzaSyC1K9py0hD9-q_yty4mNAdy21johpH-l5Q";
                Log.e("Destiny url:",url+"");
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;


                try {

                    URL url1 = new URL(url);

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url1.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {

                        buffer.append(line).append("\n");
                    }

                    Log.d("myResponse", buffer.toString());


                    JSONObject jsonObject = new JSONObject(buffer.toString());
                    JSONArray routes_array = jsonObject.getJSONArray("results");
                    JSONObject result_object = routes_array.getJSONObject(0);
                    JSONObject geometry = result_object.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    String source_method_lat = location.getString("lat");
                    String source_method_long = location.getString("lng");


                    result = source_method_lat + "," + source_method_long;


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();
                Log.e("destiny latlong rspns",s+"");
                if (s!=null){
                    String[] array = s.split(",");
                    dlatdd = Double.parseDouble(array[0]);
                    dlongdd = Double.parseDouble(array[1]);
                    requestingPollyLine();
                }


            }
        }


        new requestData().execute();
    }


    private void requestingPollyLine() {

        class requestData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {

                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + slatdd + "," + slongdd + "&destination=" + dlatdd + "," + dlongdd + "&key=AIzaSyC1K9py0hD9-q_yty4mNAdy21johpH-l5Q";
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                try {

                    URL url1 = new URL(url);

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url1.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {

                        buffer.append(line).append("\n");
                    }



                    JSONObject jsonObject = new JSONObject(buffer.toString());
                    JSONArray routes_array = jsonObject.getJSONArray("routes");
                    JSONObject zero = routes_array.getJSONObject(0);
                    JSONArray legs = zero.getJSONArray("legs");
                    JSONObject zero_legs = legs.getJSONObject(0);
                    JSONObject bounds = zero.getJSONObject("bounds");
                    JSONObject distance = zero_legs.getJSONObject("distance");
                    JSONObject time = zero_legs.getJSONObject("duration");


                    destiny = zero_legs.getString("end_address");
                    sour = zero_legs.getString("start_address");
                    total_distance = distance.getString("text");
                    total_time = time.getString("text");

                    JSONObject over_view = zero.getJSONObject("overview_polyline");
                    poly = over_view.getString("points");


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return poly;
            }

            @Override
            protected void onPostExecute(String s) {
                pd.dismiss();

                try {
                    if (s.equals("")) {
                        pd.dismiss();
                        tttime.setVisibility(View.INVISIBLE);
                    } else {
                        waypoints = decodePoly(s);
                        PolylineOptions options = new PolylineOptions();
                        options.addAll(waypoints).color(Color.RED);

                        LatLng source = new LatLng(slatdd, slongdd);
                        mMap.addMarker(new MarkerOptions().position(source).title("source"));
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                        LatLng destination = new LatLng(dlatdd, dlongdd);
                        mMap.addMarker(new MarkerOptions().position(destination).title("destination"));
                        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                        //LatLng cur_Latlng=new LatLng(21.0000,78.0000); // giving your marker to zoom to your location area.
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(7));
                        Log.e("Log",slatdd+" "+ slongdd+" and "+dlatdd+" "+dlongdd);

                        line = mMap.addPolyline(options);




                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                super.onPostExecute(s);
            }
        }


        new requestData().execute();
    }

    private void showJSONprediction(String response) {
        String prediction_place;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray routes_array = jsonObject.getJSONArray("predictions");
            for (int s = 0; s < routes_array.length(); s++) {
                JSONObject prediction_object = routes_array.getJSONObject(s);
                prediction_place = prediction_object.getString("description");
                placese_auto.add(prediction_place);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


}
