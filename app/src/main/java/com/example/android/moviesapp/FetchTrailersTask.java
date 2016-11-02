package com.example.android.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.moviesapp.data.MoviesContract.trailersEntry;

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
public class FetchTrailersTask extends AsyncTask<String,Void,Void> {

    private final Context mContext;

    public FetchTrailersTask(Context context ) {
        mContext = context;
    }

    private void getTrailersDataFromJson(String trailerJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";
        final String OWM_KEY = "key";
        final String OWM_NAME = "name";
        final String OWM_ID = "id";
        final String OWM_SITE = "site";

        try {
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailersArray = trailerJson.getJSONArray(OWM_LIST);

            int movieID = trailerJson.getInt(OWM_ID);

            // Insert the new trailers information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(trailersArray.length());


            for(int i = 0; i < trailersArray.length(); i++) {

                JSONObject trailerObject = trailersArray.getJSONObject(i);
                ContentValues trailerValues = new ContentValues();

                String key = trailerObject.getString(OWM_KEY);
                String name = trailerObject.getString(OWM_NAME);
                String site = trailerObject.getString(OWM_SITE);

                trailerValues.put(trailersEntry.COLUMN_MOVIE_ID, movieID);
                trailerValues.put(trailersEntry.COLUMN_KEY, key);
                trailerValues.put(trailersEntry.COLUMN_TRAILER_NAME, name + "(" + site + ")");

                cVVector.add(trailerValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(trailersEntry.CONTENT_URI, cvArray);
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
                            + "/videos" + APPID_PARAM;

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
