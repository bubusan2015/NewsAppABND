package com.example.android.newsappabnd;

import android.content.Context;

import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GetStoriesAsyncTaskLoader extends AsyncTaskLoader<List<Story>> {
    private String urlString;

    public GetStoriesAsyncTaskLoader(Context context, String urlString) {
        super(context);
        this.urlString = urlString;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Story> loadInBackground() {
        return Utility.getStoriesList(urlString, 1, 5);
    }
}