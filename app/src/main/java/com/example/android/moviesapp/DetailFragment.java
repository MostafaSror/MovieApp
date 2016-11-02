package com.example.android.moviesapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviesapp.data.MoviesContract.moviesEntry;
import com.squareup.picasso.Picasso;

/**
 * Created by Mostafa on 12.09.2016.
 */
public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor>{

    private static final int DETAIL_LOADER = 0;

    // A cursor that points to the row representing data of the movie with the given ID
    Cursor movieRow;

    //The value of the uri passed from main activity
    static final String DETAIL_URI = "URI";

    private Uri mUri;

    Button b1;

    private static final String[] MOVIE_COLUMNS = {
            moviesEntry.TABLE_NAME + "." + moviesEntry._ID,
            moviesEntry.COLUMN_TITLE,
            moviesEntry.COLUMN_POSTER_PATH,
            moviesEntry.COLUMN_VOTE,
            moviesEntry.COLUMN_RELEASE_DATE,
            moviesEntry.COLUMN_MOVIE_ID,
            moviesEntry.COLUMN_SORT_TYPE,
            moviesEntry.COLUMN_OVERVIEW,
            moviesEntry.COLUMN_POPULARITY

    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_POSTER = 2;
    private static final int COL_MOVIE_VOTE = 3;
    private static final int COL_MOVIE_DATE = 4;
    private static final int COL_MOVIE_ID = 5;
    private static final int COL_MOVIE_SORT_TYPE = 6;
    private static final int COL_MOVIE_OVERVIEW = 7;
    private static final int COL_MOVIE_POPULARITY = 8;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        b1=(Button)rootView.findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues movieValues = new ContentValues();
                movieValues.put(moviesEntry.COLUMN_SORT_TYPE, "favourites");
                movieValues.put(moviesEntry.COLUMN_MOVIE_ID, movieRow.getLong(COL_MOVIE_ID));
                movieValues.put(moviesEntry.COLUMN_TITLE,movieRow.getString(COL_MOVIE_TITLE) );
                movieValues.put(moviesEntry.COLUMN_OVERVIEW, movieRow.getString(COL_MOVIE_OVERVIEW));
                movieValues.put(moviesEntry.COLUMN_RELEASE_DATE, movieRow.getString(COL_MOVIE_DATE));
                movieValues.put(moviesEntry.COLUMN_POSTER_PATH, movieRow.getString(COL_MOVIE_POSTER));
                movieValues.put(moviesEntry.COLUMN_POPULARITY, movieRow.getFloat(COL_MOVIE_POPULARITY));
                movieValues.put(moviesEntry.COLUMN_VOTE, movieRow.getFloat(COL_MOVIE_VOTE));

                Uri tempuri = moviesEntry.buildMoviesWithSortAndIdUrl("favourites", (int)movieRow.getLong(COL_MOVIE_ID));
                Cursor tempcursor = getContext().getContentResolver()
                        .query(tempuri,MOVIE_COLUMNS,null,null,null);
                if(!tempcursor.moveToFirst()) {
                    getContext().getContentResolver()
                            .insert(mUri, movieValues);
                }
                Toast.makeText(getContext(),"Added To Favourites",Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                mUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) { return; }
        movieRow = data ;
        ImageView moviePoster = (ImageView) getView().findViewById(R.id.imageUrl);
        TextView titleTextView = (TextView)getView().findViewById(R.id.original_title);
        TextView voteRateTextView = (TextView)getView().findViewById(R.id.VoteRate);
        TextView dateTextView = (TextView)getView().findViewById(R.id.dateOfMovie);

        String titleString = data.getString(COL_MOVIE_TITLE);
        String posterString = data.getString(COL_MOVIE_POSTER);
        Double voteString = data.getDouble(COL_MOVIE_VOTE);
        String dateString = data.getString(COL_MOVIE_DATE);

        Picasso.with(getContext()).load(posterString).fit().into(moviePoster);
        titleTextView.setText(titleString);
        voteRateTextView.setText("Rate: " + Double.toString(voteString));
        dateTextView.setText("Date" + dateString);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
