package com.example.android.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.moviesapp.data.MoviesContract.moviesEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;


/**
 * Created by Mostafa on 12.09.2016.
 */
public class FetchMovieTask extends AsyncTask<String,Void,Void> {

    private final Context mContext;
    private final String SORT_TYPE;

    public FetchMovieTask(Context context , String sorttype) {
        mContext = context;
        SORT_TYPE = sorttype;
    }

    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";
        final String OWM_ID = "id";
        final String OWM_TITLE = "original_title";
        final String OWM_OVERVIEW = "overview";
        final String OWM_DATE = "release_date";
        final String OWM_PATH = "poster_path";
        final String OWM_POPULARITY = "popularity";
        final String OWM_VOTE = "vote_average";



        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray moviesArray = movieJson.getJSONArray(OWM_LIST);


            // Insert the new movies information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

            for(int i = 0; i < moviesArray.length(); i++) {

                JSONObject movieObject = moviesArray.getJSONObject(i);
                ContentValues movieValues = new ContentValues();

                String id = movieObject.getString(OWM_ID);
                String title = movieObject.getString(OWM_TITLE);
                String overview = movieObject.getString(OWM_OVERVIEW);
                String date = movieObject.getString(OWM_DATE);
                String path = movieObject.getString(OWM_PATH);
                String url = "http://image.tmdb.org/t/p/w185/" + path ;
                String popularity = movieObject.getString(OWM_POPULARITY);
                String vote = movieObject.getString(OWM_VOTE);

                //We save the Sort Type in a separate field in the Table
                movieValues.put(moviesEntry.COLUMN_SORT_TYPE, SORT_TYPE);

                movieValues.put(moviesEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(moviesEntry.COLUMN_TITLE, title);
                movieValues.put(moviesEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(moviesEntry.COLUMN_RELEASE_DATE, date);
                movieValues.put(moviesEntry.COLUMN_POSTER_PATH, url);
                movieValues.put(moviesEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(moviesEntry.COLUMN_VOTE, vote);

                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add values of movies to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(moviesEntry.CONTENT_URI, cvArray);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected Void doInBackground(String ... params){

        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;


        try {

            // Construct the URL for the MovieDB API query
            final String QUERY_PARAM = params[0];
            final String APPID_PARAM = "?api_key=6aa29c0d00989285cdf8bc920b76985c";

            final String MOVIEDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie/" + QUERY_PARAM + APPID_PARAM;

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL);

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();

        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }

        try {
            getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the movies.
        return null;
    }
}
