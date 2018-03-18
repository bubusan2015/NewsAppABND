package com.example.android.newsappabnd;

import android.content.Context;

import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nibos on 3/16/2018.
 */

public class GetStoriesAsyncTaskLoader extends AsyncTaskLoader<List<Story>> {
    private String urlString;
    public GetStoriesAsyncTaskLoader(Context context, String urlString) {
        super(context);
        this.urlString=urlString;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Story> loadInBackground() {

//        String fromHttpResult=Utility.fetchStringFromHttp(urlString);
//        //Log.e(Utility.errorTag,fromHttpResult);
//        ArrayList<Story> storiesList=Utility.extractJsonNews(fromHttpResult);
    ArrayList<Story> storiesList = Utility.getStoriesList(urlString,5);
     Log.e(Utility.errorTag,storiesList.toString());
        return storiesList;
    }
}