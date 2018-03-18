package com.example.android.newsappabnd;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>>,SwipeRefreshLayout.OnRefreshListener {
    private ListView storiesListView;
    private  StoryArrayAdapter storyArrayAdapter;
    private TextView emptyView;
    private ProgressBar loadingListProgressBar;
    private SwipeRefreshLayout swipeRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storiesListView=findViewById(R.id.listview_stories);
        loadingListProgressBar=findViewById(R.id.progress_bar_main_activity);
        emptyView=(TextView) findViewById(R.id.empty_element);
        swipeRefresh=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this);
        storyArrayAdapter=new StoryArrayAdapter(this,0,new ArrayList<Story>());
        storiesListView.setAdapter(storyArrayAdapter);
        storiesListView.setEmptyView(emptyView);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            getSupportLoaderManager().initLoader(0,null,this);
        } else {
            loadingListProgressBar.setVisibility(View.GONE);
            emptyView.setText(getString(R.string.no_internet_connection));
        }
        storiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String webPage = storyArrayAdapter.getItem(position).getWebUrl();
                Intent launchInBrowser = new Intent(Intent.ACTION_VIEW);
                launchInBrowser.setData(Uri.parse(webPage));
                startActivity(launchInBrowser);
            }
        });
    }


    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        return new GetStoriesAsyncTaskLoader(this,getLink());
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {
        storyArrayAdapter.clear();
        if (stories != null)
            storyArrayAdapter.addAll(stories);
        loadingListProgressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {

    }
    
    public String getLink () {
        //Log.e(Utility.errorTag,"https://content.guardianapis.com/search?q=android&from-date=2016-01-01"+"&api-key="+Utility.apiKey+"&page=1");
        return "https://content.guardianapis.com/search?q=android&from-date=2016-01-01"+"&api-key="+Utility.apiKey+"&page=1";
    }

    @Override
    public void onRefresh() {
        getSupportLoaderManager().restartLoader(0,null,this);
    }
}
