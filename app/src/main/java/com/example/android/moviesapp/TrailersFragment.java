package com.example.android.moviesapp;

/**
 * Created by Mostafa on 18.09.2016.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.moviesapp.data.MoviesContract;



/**
 * Created by Mostafa on 11-Aug-16.
 */
public class TrailersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int FORECAST_LOADER = 0;

    static final String TRAILERS_URI = "URI";

    private String movieID ;

    private Uri mUri;

    private static final String[] TRAILER_COLUMNS = {
            MoviesContract.trailersEntry.TABLE_NAME + "." + MoviesContract.trailersEntry._ID,
            MoviesContract.trailersEntry.COLUMN_MOVIE_ID,
            MoviesContract.trailersEntry.COLUMN_TRAILER_NAME,
            MoviesContract.trailersEntry.COLUMN_KEY
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_TRAILER_ID = 0;
    static final int COL_TRAILER_MOVIE_ID = 1;
    static final int COL_TRAILER_NAME = 2;
    static final int COL_TRAILER_KEY = 3;


    public TrailersFragment(){
        setHasOptionsMenu(true);
    }

    ListView listView;
    TrailersAdapter listAdapter;



    // Launch Youtube to watch an URL
    public static void launchYoutube(Context context, String url) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchTrailersTask fetchTrailersTask = new FetchTrailersTask(getActivity());
        fetchTrailersTask.execute(movieID);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ReviewsFragment.REVIEWS_URI);
        }
        movieID = MoviesContract.moviesEntry.getMovieIDFromUri(mUri);

        listAdapter = new TrailersAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.trailers_layout, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_trailers);
        listView.setAdapter(listAdapter);


       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String url = "https://www.youtube.com/watch?v="
                            + cursor.getString(TrailersFragment.COL_TRAILER_KEY);
                    launchYoutube(getContext(),url);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri Trailers_URI = MoviesContract.trailersEntry.buildTrailerWithMovieIDUri(movieID);
        return new CursorLoader(getActivity(),
                Trailers_URI,
                TRAILER_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        listAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        listAdapter.swapCursor(null);
    }
}


