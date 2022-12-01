package com.example.fuelfinder;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.Manifest;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean permsGranted = false;
    private static final int REQUEST_LOC_PERMISSION_CODE = 1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private GoogleApiClient mGoogleApiClient;



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng zero = new LatLng(0, 0);
        LatLng gatech = new LatLng(33.77, -84.39);
        mMap.addMarker(new MarkerOptions().position(zero).title("marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(zero));
        mMap.addMarker(new MarkerOptions().position(gatech).title("Georgia Institute of Technology"));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();

        if (permsGranted) {
            initMap();
            geoLocate();
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permsGranted = true;
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOC_PERMISSION_CODE);
        }
    }

    private void geoLocate() {
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName("RaceTrac", 10);

        } catch (IOException e) {
            Log.e(TAG, "geolocate error");
        }
        if(list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "location found: "+ address.toString());
        }
    }

//    private void readGasData() {
//        InputStream is = getResources().openRawResource(R.raw.gas_stations);
//        BufferedReader reader = new BufferedReader(
//                new InputStreamReader(is, Charset.forName("UTF-8"))
//        );
//
//        String line = "";
//        try {
//            while ((line = reader.readLine()) != null) {
//                //split by ','
//                String[] tokens = line.split(",");
//                Log.d(TAG, "gasdata: " + tokens[1]);
//                //read data
//                GasStation gs = new GasStation();
//                gs.setX(Double.parseDouble(tokens[0]));
//                gs.setY(Double.parseDouble(tokens[1]));
//                gs.setId(Integer.parseInt(tokens[2]));
//                gs.setCounty(tokens[3]);
//                gs.setName(tokens[4]);
//                gs.setAddress(tokens[5]);
//                gs.setCity(tokens[6]);
//                gs.setZip(tokens[7]);
//                gs.setPhone(tokens[8]);
//                gs.setLat(Double.parseDouble(tokens[9]));
//                gs.setLng(Double.parseDouble(tokens[10]));
//                stations.add(gs);
//            }
//        } catch (IOException e) {
//            Log.d(TAG, "readGasData: error reading file");
//        }
//    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permsGranted = false;
        switch (requestCode) {
            case REQUEST_LOC_PERMISSION_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permsGranted = false;
                            return;
                        }
                    }
                    permsGranted = true;
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
