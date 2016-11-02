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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.moviesapp.data.MoviesContract;



/**
 * Created by Mostafa on 11-Aug-16.
 */
public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int FORECAST_LOADER = 0;

    static final String REVIEWS_URI = "URI";

    private Uri mUri;

    private String movieID ;

    private static final String[] TRAILER_COLUMNS = {
            MoviesContract.reviewsEntry.TABLE_NAME + "." + MoviesContract.reviewsEntry._ID,
            MoviesContract.reviewsEntry.COLUMN_MOVIE_ID,
            MoviesContract.reviewsEntry.COLUMN_AUTHOR,
            MoviesContract.reviewsEntry.COLUMN_CONTENT
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_REVIEW_ID = 0;
    static final int COL_REVIEW_MOVIE_ID = 1;
    static final int COL_REVIEW_AUTHOR = 2;
    static final int COL_REVIEW_CONTENT = 3;


    public ReviewsFragment(){
        setHasOptionsMenu(true);
    }

    ListView listView;
    ReviewsAdapter listAdapter;

    @Override
    public void onStart() {
        super.onStart();
        FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity());
        fetchReviewsTask.execute(movieID);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ReviewsFragment.REVIEWS_URI);
        }
        movieID = MoviesContract.moviesEntry.getMovieIDFromUri(mUri);

        listAdapter = new ReviewsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.reviews_layout, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_reviews);
        listView.setAdapter(listAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri Reviews_URI = MoviesContract.reviewsEntry.buildReviewsWithMovieIDUri(movieID);
        return new CursorLoader(getActivity(),
                Reviews_URI,
                null,
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


