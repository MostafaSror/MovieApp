package com.example.android.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.moviesapp.data.MoviesContract.moviesEntry;
import com.example.android.moviesapp.data.MoviesContract.trailersEntry;
import com.example.android.moviesapp.data.MoviesContract.reviewsEntry;


/**
 * Created by Mostafa on 12.09.2016.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold movies.
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + moviesEntry.TABLE_NAME + " (" +
                moviesEntry._ID + " INTEGER PRIMARY KEY," +
                moviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                moviesEntry.COLUMN_SORT_TYPE + " TEXT NOT NULL, " +
                moviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                moviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                moviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                moviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                moviesEntry.COLUMN_POPULARITY + " FLOAT NOT NULL, " +
                moviesEntry.COLUMN_VOTE + " FLOAT NOT NULL " +
                " );";
        // Create a table to hold trailers.
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + trailersEntry.TABLE_NAME + " (" +
                trailersEntry._ID + " INTEGER PRIMARY KEY," +
                trailersEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                trailersEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                trailersEntry.COLUMN_KEY + " TEXT NOT NULL " +
                " );";
        // Create a table to hold reviews.
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + reviewsEntry.TABLE_NAME + " (" +
                reviewsEntry._ID + " INTEGER PRIMARY KEY," +
                reviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                reviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                reviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + moviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + trailersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + reviewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
