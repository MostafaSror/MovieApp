package com.example.android.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.moviesapp.data.MoviesContract.reviewsEntry;

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
public class FetchReviewsTask extends AsyncTask<String,Void,Void> {

    private final Context mContext;

    public FetchReviewsTask(Context context ) {
        mContext = context;
    }

    private void getTrailersDataFromJson(String reviewsJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";
        final String OWM_KEY = "author";
        final String OWM_NAME = "content";
        final String OWM_ID = "id";

        try {
            JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
            JSONArray reviewsArray = reviewsJson.getJSONArray(OWM_LIST);

            int movieID = reviewsJson.getInt(OWM_ID);

            // Insert the new trailers information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewsArray.length());


            for(int i = 0; i < reviewsArray.length(); i++) {

                JSONObject reviewObject = reviewsArray.getJSONObject(i);
                ContentValues trailerValues = new ContentValues();

                String authorName = reviewObject.getString(OWM_KEY);
                String review = reviewObject.getString(OWM_NAME);

                trailerValues.put(reviewsEntry.COLUMN_MOVIE_ID, movieID);
                trailerValues.put(reviewsEntry.COLUMN_AUTHOR, authorName);
                trailerValues.put(reviewsEntry.COLUMN_CONTENT, review);

                cVVector.add(trailerValues);
            }


            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(reviewsEntry.CONTENT_URI, cvArray);
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
        String trailersJsonStr = null;


        try {

            // Construct the URL for the MovieDB API query
            final String QUERY_PARAM = params[0];
            final String APPID_PARAM = "?api_key=6aa29c0d00989285cdf8bc920b76985c";

            final String TRAILERDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie/" + QUERY_PARAM
                            + "/reviews" + APPID_PARAM;

            Uri builtUri = Uri.parse(TRAILERDB_BASE_URL);

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
            trailersJsonStr = buffer.toString();

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
            getTrailersDataFromJson(trailersJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }
}
