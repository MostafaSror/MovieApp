package com.example.android.moviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.android.moviesapp.data.MoviesContract;

/**
 * Created by Mostafa on 11-Aug-16.
 */
public class FragmentMain extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int FORECAST_LOADER = 0;

    public FragmentMain() {
        setHasOptionsMenu(true);
    }

    GridView gridView;
    ImageAdapter gridAdapter;
    SharedPreferences prefs;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gridAdapter = new ImageAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    final String movieOrder = prefs.getString(getString(R.string.pref_order_key)
                            ,getString(R.string.pref_order_popular));
                    ((Callback) getActivity())
                            .onItemSelected(MoviesContract.moviesEntry
                                    .buildMoviesWithSortAndIdUrl(movieOrder,
                                            cursor.getInt( cursor.getColumnIndex("movie_id"))));
                    mPosition = position;
                }
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Gridview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);}
        super.onSaveInstanceState(outState);
    }

    void onSortOrderChanged( ) {
        updateMovies();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    private void updateMovies() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String movieSortOrder = prefs.getString(getString(R.string.pref_order_key),getString(R.string.pref_order_popular));
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(),movieSortOrder);
        fetchMovieTask.execute(movieSortOrder);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String movieSort = prefs.getString(getString(R.string.pref_order_key),getString(R.string.pref_order_popular));

        Uri moviesUri = MoviesContract.moviesEntry.buildMoviesWithSortUrl(
                movieSort);

        return new CursorLoader(getActivity(),
                moviesUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        gridAdapter.swapCursor(cursor);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        gridAdapter.swapCursor(null);
    }
}

