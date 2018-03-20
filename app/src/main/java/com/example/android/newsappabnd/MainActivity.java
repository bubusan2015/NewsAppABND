package com.example.android.newsappabnd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>>, SwipeRefreshLayout.OnRefreshListener {
    private ListView storiesListView;
    private StoryArrayAdapter storyArrayAdapter;
    private TextView emptyView;
    private ProgressBar loadingListProgressBar;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storiesListView = findViewById(R.id.listview_stories);
        loadingListProgressBar = findViewById(R.id.progress_bar_main_activity);
        emptyView = (TextView) findViewById(R.id.empty_element);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this);
        storyArrayAdapter = new StoryArrayAdapter(this, 0, new ArrayList<Story>());
        storiesListView.setAdapter(storyArrayAdapter);
        storiesListView.setEmptyView(emptyView);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            getSupportLoaderManager().initLoader(Utility.taskLoaderId, null, this);
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

                PackageManager packageManager = getPackageManager();
                if (launchInBrowser.resolveActivity(packageManager) != null) {
                    startActivity(launchInBrowser);
                } else {
                    Log.d(Utility.errorTag, "No Intent available to handle action");
                }
            }
        });
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        return new GetStoriesAsyncTaskLoader(this, getLink());
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

    public String getLink() {
        String urlLink = "https://content.guardianapis.com/search?q=android&from-date=2016-01-01&show-tags=contributor" + "&api-key=" + Utility.apiKey;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderByValue = sharedPreferences.getString(Utility.orderTag, "relevance");
        String sectionValue = sharedPreferences.getString(Utility.sectionTag, "all");
        Uri baseUri = Uri.parse(urlLink);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(Utility.orderTag, orderByValue);
        if (!sectionValue.equals("all"))
            uriBuilder.appendQueryParameter(Utility.sectionTag, sectionValue);
        uriBuilder.appendQueryParameter("page", "1");
        return uriBuilder.toString();
    }

    @Override
    public void onRefresh() {
        getSupportLoaderManager().restartLoader(Utility.taskLoaderId, null, this);
    }
}