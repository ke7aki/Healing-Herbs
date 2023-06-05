package com.applicationslab.ayurvedictreatment.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.applicationslab.ayurvedictreatment.R;
import com.applicationslab.ayurvedictreatment.utility.PermissionHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_PERMISSION_ACCESS_FINE_WRITE_EXTERNAL = 3;
    String PLACES_API_KEY = "AIzaSyB2R6lQ7Lq54XXaalmJFw2UjwhO2C8Zdiw";

    PlacesClient placesClient;
    PermissionHandler permission;

    ProgressBar progressBar;

    GoogleMap googleMap = null;

    double myLat = 0, myLon = 0;
    String myLocName = "";
    ArrayList<String> allNames = null;
    ArrayList<String> allIds = null;
    ArrayList<Double> allLats = null;
    ArrayList<Double> allLons = null;
    boolean markersAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initData();
        checkPermissions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_FINE_WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView();
                } else {
                    Toast.makeText(getApplicationContext(), "Access location and write external storage permissions are required", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView();
                } else {
                    Toast.makeText(getApplicationContext(), "Access location permission is required", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView();
                } else {
                    Toast.makeText(getApplicationContext(), "Write external storage permission is required", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (allLats != null) {
            addMarkersToMap();
        }
    }

    private void initData() {
        permission = new PermissionHandler(this);
    }

    private void checkPermissions() {
        if (!permission.hasAccessFineLocationPermission() && !permission.hasWriteExternalStoragePermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_ACCESS_FINE_WRITE_EXTERNAL);
        } else if (!permission.hasAccessFineLocationPermission()) {
            //requestAccessLocationPermission();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        } else if (!permission.hasWriteExternalStoragePermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            initView();
        }
    }

    private void requestAccessLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permission.showPermissionSettingsSnackbar("You need to authorize this app to access GPS location from your Settings");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        }
    }


    private void initView() {
        Toolbar toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Ayurvedic Hospitals");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        initPlacesApi();
    }

    private void initPlacesApi() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), PLACES_API_KEY);
        }
        placesClient = Places.createClient(this);

        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);

        FindCurrentPlaceRequest findCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(fields);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<FindCurrentPlaceResponse> currentPlaceResponse = placesClient.findCurrentPlace(findCurrentPlaceRequest);
            currentPlaceResponse.addOnSuccessListener((response) -> {
                myLocName = response.getPlaceLikelihoods().get(0).getPlace().getName();
                LatLng currentPlaceLatLng = response.getPlaceLikelihoods().get(0).getPlace().getLatLng();

                assert currentPlaceLatLng != null;
                myLat = currentPlaceLatLng.latitude;
                myLon = currentPlaceLatLng.longitude;

                String placeURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + myLat
                        + ","
                        + myLon
                        + "&radius=5000&type=hospital&keyword=ayurvedic&key=\"" + PLACES_API_KEY;
                PlaceFinder finder = new PlaceFinder();
                finder.execute(placeURL);
            });

            currentPlaceResponse.addOnFailureListener((exception) -> {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            });
    }


    private void addMarkersToMap() {
        if(!markersAdded) {
            markersAdded = true;

            for (int i = 0; i < allLats.size(); i++) {
                LatLng markerPos = new LatLng(allLats.get(i), allLons.get(i));
                googleMap.addMarker(new MarkerOptions()
                        .position(markerPos)
                        .title("Hospital")
                        .snippet(allNames.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

            LatLng myPos = new LatLng(myLat, myLon);
            googleMap.addMarker(new MarkerOptions()
                    .position(myPos)
                    .title("You are here")
                    .snippet(myLocName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 14));
        }
    }



    public String readConnectionString(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("ConnectionString", "Failed to connect");
            }
        } catch (Exception e) {
            Log.d("ConnectionString", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }



    class PlaceFinder extends AsyncTask<String, Void, String> {

        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<Double> lats = new ArrayList<Double>();
        ArrayList<Double> lons = new ArrayList<Double>();

        @Override
        protected String doInBackground(String... params) {
            return readConnectionString(params[0]);
        }

        @Override
        protected void onPostExecute(String JSONString) {
            try {
                JSONObject jsonObject = new JSONObject(JSONString);
                JSONArray placeItems = jsonObject.getJSONArray("results");
                for (int i = 0; i < placeItems.length(); i++) {
                    JSONObject placeItem = placeItems.getJSONObject(i);
                    Double lat = placeItem.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    Double lon = placeItem.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    String name = placeItem.getString("name");
                    String id = placeItem.getString("place_id");
                    names.add(name);
                    ids.add(id);
                    lats.add(lat);
                    lons.add(lon);
                }

                progressBar.setVisibility(View.GONE);

                allNames = names;
                allIds = ids;
                allLats = lats;
                allLons = lons;

                if (googleMap != null) {
                    addMarkersToMap();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
