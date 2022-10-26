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
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class result extends AppCompatActivity {

    ListView lst;
    ArrayList<String> results = new ArrayList<>();
    ArrayList<String> desc = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        results.add("Gas Station 1");
        results.add("Gas Station 2");

        desc.add("1 mile, 3$, 5 star");
        desc.add("2 mile, 4$, 4 star");

        lst = (ListView) findViewById(R.id.listview);
        result_format customListview = new result_format(result.this, results, desc);
        lst.setAdapter(customListview);
        this.setTitle("Results");
    }
}