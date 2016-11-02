package com.example.android.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Mostafa on 12-Aug-16.
 */
public class ImageAdapter extends CursorAdapter {

    public ImageAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragmentlayout, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView moviePoster = (ImageView) view ;
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + cursor.getString( cursor.getColumnIndex("poster_path") ))
                .fit().into(moviePoster);
    }
}


