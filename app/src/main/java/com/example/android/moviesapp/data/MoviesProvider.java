package com.example.android.moviesapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Mostafa on 12.09.2016.
 */
public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MoviesDBHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIE_SORT = 101;
    static final int MOVIE_ID = 103;
    static final int TRAILERS = 102;
    static final int SINGLE_MOVIE_ID = 300;
    static final int REVIEWS = 400;
    static final int SINGLE_REVIEW = 500;


    private static final String sSortTypeSelection =
            MoviesContract.moviesEntry.TABLE_NAME +
                    "." + MoviesContract.moviesEntry.COLUMN_SORT_TYPE + " = ? ";

    private static final String sMovieIDSelection =
            MoviesContract.moviesEntry.TABLE_NAME+
                    "." + MoviesContract.moviesEntry.COLUMN_SORT_TYPE + " = ? AND " +
                    MoviesContract.moviesEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sTrailersMovieIDSelection =
            MoviesContract.trailersEntry.TABLE_NAME+
                    "." + MoviesContract.trailersEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sReviewsMovieIDSelection =
            MoviesContract.reviewsEntry.TABLE_NAME+
                    "." + MoviesContract.reviewsEntry.COLUMN_MOVIE_ID + " = ? ";


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIES , MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*", MOVIE_SORT);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*/#", MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS , TRAILERS);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS + "/#", SINGLE_MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS , REVIEWS);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/#", SINGLE_REVIEW);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesContract.moviesEntry.CONTENT_TYPE;
            case MOVIE_SORT:
                return MoviesContract.moviesEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MoviesContract.moviesEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return MoviesContract.trailersEntry.CONTENT_TYPE;
            case SINGLE_MOVIE_ID:
                return MoviesContract.trailersEntry.CONTENT_TYPE;
            case REVIEWS:
                return MoviesContract.reviewsEntry.CONTENT_TYPE;
            case SINGLE_REVIEW:
                return MoviesContract.reviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case MOVIES:
                retCursor = db.query(
                        MoviesContract.moviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case MOVIE_SORT:
                String movies_sort_Type = MoviesContract.moviesEntry.getSortTypeFromUri(uri);
                retCursor = db.query(
                        MoviesContract.moviesEntry.TABLE_NAME,
                        projection,
                        sSortTypeSelection,
                        new String[]{movies_sort_Type},
                        null,
                        null,
                        sortOrder
                );
                break;

            case MOVIE_ID:
                String[] movies_Sort_Type = MoviesContract.moviesEntry.getSortTypeAndIDFromUri(uri);
                retCursor = db.query(
                        MoviesContract.moviesEntry.TABLE_NAME,
                        projection,
                        sMovieIDSelection,
                        movies_Sort_Type,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILERS:
                retCursor = db.query(
                        MoviesContract.trailersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SINGLE_MOVIE_ID:
                String temp = MoviesContract.trailersEntry.getMovieIDFromUri(uri);
                retCursor = db.query(
                        MoviesContract.trailersEntry.TABLE_NAME,
                        projection,
                        sTrailersMovieIDSelection,
                        new String[]{temp},
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEWS:
                retCursor = db.query(
                        MoviesContract.reviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SINGLE_REVIEW:
                String temp_movieID = MoviesContract.reviewsEntry.getMovieIDFromUri(uri);
                retCursor = db.query(
                        MoviesContract.reviewsEntry.TABLE_NAME,
                        projection,
                        sReviewsMovieIDSelection,
                        new String[]{temp_movieID},
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MoviesContract.moviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.moviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_ID: {
                long _id = db.insert(MoviesContract.moviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.moviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {

            case MOVIES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.moviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case TRAILERS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.trailersEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case REVIEWS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.reviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    // This is a method specifically to assist the testing
    // framework in running smoothly.
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
