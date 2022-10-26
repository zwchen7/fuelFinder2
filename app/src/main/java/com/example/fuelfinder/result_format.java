package com.example.fuelfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class result_format extends ArrayAdapter<String> {

    private ArrayList<String> results;
    private ArrayList<String> desc;
    private Activity context;

    public result_format(Activity context, ArrayList<String> results, ArrayList<String> desc) {
        super(context, R.layout.activity_result_format, results);
        this.context = context;
        this.results = results;
        this.desc = desc;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if (r == null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.activity_result_format, null, true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) r.getTag();
        }

        viewHolder.tvw1.setText(results.get(position));
        viewHolder.tvw2.setText(desc.get(position));
        return r;
    }

    class ViewHolder
    {
        TextView tvw1;
        TextView tvw2;
        ViewHolder(View v)
        {
            tvw1 = (TextView) v.findViewById(R.id.resultsView);
            tvw2 = (TextView) v.findViewById(R.id.description);
        }

    }

}
