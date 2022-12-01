package com.example.fuelfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    Button enterButton;
    private List<GasStation> stations = new ArrayList();
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        enterButton = findViewById(R.id.enterButton);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, result.class));
            }
        });

        Button btnMap = (Button) findViewById(R.id.mapTestButton);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        readGasData();
    }

//    private void init() {
//        Button btnMap = (Button) findViewById(R.id.mapTestButton);
//        btnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, MapActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

    public boolean isServicesEnabled() {
        Log.d("MainActivity", "isServicesEnabled: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d("MainActivity", "isServicesEnabled: Google Play Services working");
            return true;
        }
        return false;
        //return to API setup part 2
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
                Log.d(TAG, "gasdata: " + line);
                //read data
                GasStation gs = new GasStation();
                gs.setX(Double.parseDouble(tokens[0]));
                gs.setY(Double.parseDouble(tokens[1]));
                gs.setId(Integer.parseInt(tokens[2]));
                gs.setCounty(tokens[3]);
                gs.setName(tokens[4]);
                gs.setAddress(tokens[5]);
                gs.setCity(tokens[6]);
                gs.setZip(tokens[7]);
                gs.setPhone(tokens[8]);
                gs.setLat(Double.parseDouble(tokens[9]));
                gs.setLng(Double.parseDouble(tokens[10]));
                stations.add(gs);
            }
        } catch (IOException e) {
            Log.d(TAG, "readGasData: error reading file");
        }
    }


}