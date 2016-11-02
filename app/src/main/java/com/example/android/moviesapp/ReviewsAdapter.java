package com.example.android.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mostafa on 10.09.2016.
 */
public class ReviewsAdapter extends CursorAdapter {
    public ReviewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.reviews_item_layout, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tv1 = (TextView)view.findViewById(R.id.list_item_reviews_textview_1);
        TextView tv2 = (TextView)view.findViewById(R.id.list_item_reviews_textview_2);
        tv1.setText("Review: " + cursor.getString(ReviewsFragment.COL_REVIEW_AUTHOR));
        tv2.setText(cursor.getString(ReviewsFragment.COL_REVIEW_CONTENT));
    }
}
