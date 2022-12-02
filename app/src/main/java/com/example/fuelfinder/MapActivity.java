package com.example.fuelfinder;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.Math;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean permsGranted = false;
    private static final int REQUEST_LOC_PERMISSION_CODE = 1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationListener mlocationListener;
    private LocationManager mlocationManager;
    //private GoogleApiClient mGoogleApiClient;
    private List<GasStation> stations = new ArrayList<>();
    private List<GasStation> tenClosest = new ArrayList<>();
    private LatLng center;


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        readGasData();
        //mMap.addMarker(new MarkerOptions().position(new LatLng(35.2, -80.8)).title("test"));
        if (permsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .position(center)
                .title("SEARCH EPICENTER"));
        List<Double> distances = getDistances(center.latitude, center.longitude);
        List<GasStation> selection = get20Smallest(distances);
        addMarkers(selection);
        Log.d(TAG, "outer class: " );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            center = new LatLng((Double)extras.get("lat"), (Double) extras.get("lng"));
        }

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

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (permsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            LatLng coords = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15f));
                        } else {
                            Log.d(TAG, "could not find location");
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private List<Double> getDistances(double lat, double lng) {
        List<Double> distances = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {
            double station_lat = stations.get(i).getLat();
            double station_long = stations.get(i).getLng();
            distances.add(distance(lat, lng, station_lat, station_long));
            Log.d(TAG, "getDistances: " + distances.get(i) + " id: " + stations.get(i).getId());
        }
        return distances;
    }

    private List<GasStation> get20Smallest(List<Double> distances) {
        List<GasStation> temp = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int index = distances.indexOf(Collections.min(distances));
            double lat = stations.get(index).getLat();
            double lng = stations.get(index).getLng();

            temp.add(stations.get(index));
            distances.remove(index);
            stations.remove(index);
        }
        return temp;
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1))
                * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }



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

    private void readGasData() {
        InputStream is = getResources().openRawResource(R.raw.gas_stations);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            reader.readLine(); //skip headers
            while ((line = reader.readLine()) != null) {
                //split by ','
                String[] tokens = line.split(",");
                //Log.d(TAG, "gasdata: " + line);
                //read data
                GasStation gs = new GasStation();
                if (tokens[0].length() > 0) {
                    gs.setX(Double.parseDouble(tokens[0]));
                } else {
                    gs.setX(0);
                }
                if (tokens[1].length() > 0) {
                    gs.setY(Double.parseDouble(tokens[1]));
                } else {
                    gs.setY(0);
                }
                if (tokens[2].length() > 0) {
                    gs.setId(Integer.parseInt(tokens[2]));
                } else {
                    gs.setId(0);
                }
                gs.setCounty(tokens[3]);
                gs.setName(tokens[4]);
                gs.setAddress(tokens[5]);
                gs.setCity(tokens[6]);
                gs.setZip(tokens[7]);
                gs.setPhone(tokens[8]);
                gs.setLat(gs.getY());
                gs.setLng(gs.getX());
                stations.add(gs);
//                LatLng gs_coords = new LatLng(gs.getLat(), gs.getLng());
//                mMap.addMarker(new MarkerOptions().position(gs_coords));
            }
        } catch (IOException e) {
            Log.d(TAG, "readGasData: error reading file");
        }
    }

    private void addMarkers(List<GasStation> list) {
        for (GasStation gs : list) {
            Log.d(TAG, "gs data: " + gs.toString());
            mMap.addMarker(new MarkerOptions().position(new LatLng(gs.getLat(), gs.getLng())).title(gs.getName()));
        }
    }

}
