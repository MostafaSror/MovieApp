package com.example.android.moviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements FragmentMain.Callback {

    SharedPreferences prefs;
    String mSortOrder ;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSortOrder = prefs.getString(getString(R.string.pref_order_key),getString(R.string.pref_order_popular));
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.details_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_container, new DetailFragment())
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.trailers_container, new TrailersFragment())
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.reviews_container, new ReviewsFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String SortOrder = prefs.getString(getString(R.string.pref_order_key),getString(R.string.pref_order_popular));
        // update the location in our second pane using the fragment manager
        if (SortOrder != null && !SortOrder.equals(mSortOrder)) {
            FragmentMain ff = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.fragment);
            if ( null != ff ) {
                ff.onSortOrderChanged();
            }
            mSortOrder = SortOrder;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.order) {
            startActivity(new Intent(this,SettingsActivity.class));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle detailsargs = new Bundle();
            detailsargs.putParcelable(DetailFragment.DETAIL_URI, contentUri);
            Bundle trailersargs = new Bundle();
            trailersargs.putParcelable(TrailersFragment.TRAILERS_URI, contentUri);
            Bundle reviewsargs = new Bundle();
            reviewsargs.putParcelable(ReviewsFragment.REVIEWS_URI, contentUri);

            DetailFragment detailsFragment = new DetailFragment();
            detailsFragment.setArguments(detailsargs);

            TrailersFragment trailersFragment = new TrailersFragment();
            trailersFragment.setArguments(trailersargs);

            ReviewsFragment reviewsFragment = new ReviewsFragment();
            reviewsFragment.setArguments(reviewsargs);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_container, detailsFragment)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.trailers_container, trailersFragment)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.reviews_container, reviewsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
