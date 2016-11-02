package com.example.android.moviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mostafa on 21-Aug-16.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        if (savedInstanceState == null) {

            Bundle detailsargs = new Bundle();
            detailsargs.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            Bundle trailersargs = new Bundle();
            trailersargs.putParcelable(TrailersFragment.TRAILERS_URI,getIntent().getData());
            Bundle reviewsargs = new Bundle();
            reviewsargs.putParcelable(ReviewsFragment.REVIEWS_URI, getIntent().getData());

            DetailFragment detailsFragment = new DetailFragment();
            detailsFragment.setArguments(detailsargs);

            TrailersFragment trailersFragment = new TrailersFragment();
            trailersFragment.setArguments(trailersargs);

            ReviewsFragment reviewsFragment = new ReviewsFragment();
            reviewsFragment.setArguments(reviewsargs);


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container, detailsFragment)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.trailers_container, trailersFragment)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.reviews_container, reviewsFragment)
                    .commit();
        }
    }

}

