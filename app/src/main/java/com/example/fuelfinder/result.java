package com.example.fuelfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class result extends AppCompatActivity {
    private final String TAG = "result";

    ListView lst;
    ArrayList<String> results = new ArrayList<>();
    ArrayList<String> desc = new ArrayList<>();

    private List<GasStation> stations = new ArrayList<>();
    private LatLng center = new LatLng(0,0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            center = new LatLng((Double)extras.get("lat"), (Double) extras.get("lng"));

        }

        readGasData();
        List<Double> distances = getDistances(center.latitude, center.longitude);
        List<Double> display_distances = new ArrayList<>();
        List<GasStation> selection = get20Smallest(distances);

        formatTable(selection);
    }


    private void formatTable(List<GasStation> list) {
        for (int i = 1; i <=10; i++) {
            //format ID strings
            String name = "Name"+i;
            String distance = "Distance"+i;
            String address = "Address"+i;
            String phone = "Phone"+i;
            String city = "City"+i;

            int nameID = getResources().getIdentifier(name, "id", getPackageName());
            int distanceID = getResources().getIdentifier(distance, "id", getPackageName());
            int phoneID = getResources().getIdentifier(phone, "id", getPackageName());
            int addressID = getResources().getIdentifier(address, "id", getPackageName());
            int cityID = getResources().getIdentifier(city, "id", getPackageName());

            //TextViews
            TextView nameView = (TextView) findViewById(nameID);
            TextView distanceView = (TextView) findViewById(distanceID);
            TextView addressView = (TextView) findViewById(addressID);
            TextView phoneView = (TextView) findViewById(phoneID);
            TextView dieselView = (TextView) findViewById(cityID);

            //update text
            nameView.setText(list.get(i).getName());
            distanceView.setText(String.valueOf(list.get(i).getDistance()).substring(0, 6));
            addressView.setText(list.get(i).getAddress());
            phoneView.setText(list.get(i).getPhone());
            dieselView.setText(list.get(i).getCity());
        }
    }

    private List<Double> getDistances(double lat, double lng) {
        List<Double> distances = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {
            double station_lat = stations.get(i).getLat();
            double station_long = stations.get(i).getLng();
            distances.add(distance(lat, lng, station_lat, station_long));
        }
        return distances;
    }

    private List<GasStation> get20Smallest(List<Double> distances) {
        List<GasStation> temp = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int index = distances.indexOf(Collections.min(distances));
            double lat = stations.get(index).getLat();
            double lng = stations.get(index).getLng();

            stations.get(index).setDistance(distances.get(index));
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
}