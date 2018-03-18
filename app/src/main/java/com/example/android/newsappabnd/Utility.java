package com.example.android.newsappabnd;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utility {
    public static final String errorTag = "newsAppErr";
    public static final String apiKey = "818ef47d-1951-491d-9e1f-42c92afb5c09";

    public static String fetchStringFromHttp(String urlString) {
        String resultString = "";
        URL url = createUrl(urlString);
        if (url == null) {
            return "";
        }
        // open the connection and get the inputStream
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
            } else {
                Log.e(errorTag, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException exception) {
            Log.e(Utility.errorTag, exception.getMessage());
            return resultString;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        // read the string from inputStream
        if (inputStream == null)
            return resultString;
        StringBuilder output = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(errorTag, e.getMessage());
            return resultString;
        }

        // return the string
        return output.toString();
    }

    public static ArrayList<Story> extractJsonNews(String inputString, String urlString, int currentPage, int numberOfPagesToFetch) {
        // extract Json data into a List of Objects.
        ArrayList<Story> storiesList = new ArrayList<>();
        int availablePages = 0;
        try {
            JSONObject root = new JSONObject(inputString);
            availablePages = root.getJSONObject("response").getInt("pages");
            JSONArray results = root.getJSONObject("response").getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject storyJSONObject = results.getJSONObject(i);
                Story newStory = new Story();
                newStory.setId(storyJSONObject.getString("id"));
                newStory.setSectionName(storyJSONObject.getString("sectionName"));
                newStory.setWebUrl(storyJSONObject.getString("webUrl"));
                newStory.setTitle(storyJSONObject.getString("webTitle"));
                newStory.setDate(extractJSONDate(storyJSONObject.getString("webPublicationDate")));
                storiesList.add(newStory);
            }
        } catch (JSONException e) {
            Log.e(errorTag, e.getMessage());
        }
        if (numberOfPagesToFetch > 1 && currentPage < availablePages) {
            //Log.e(errorTag, "currentPage=" + currentPage + " pagesToFetch=" + numberOfPagesToFetch);
            urlString = urlString.replaceAll("&page=" + currentPage + "$", "&page=" + (currentPage + 1));
            storiesList.addAll(Utility.getStoriesList(urlString, currentPage + 1, numberOfPagesToFetch - 1));
        }
        //Log.e(Utility.errorTag, urlString);
        return storiesList;
    }

    public static URL createUrl(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException exception) {
            Log.e(Utility.errorTag, "malformed url " + exception.getMessage());
            return null;
        }
        return url;
    }

    public static Date extractJSONDate(String date) {
        SimpleDateFormat jSONGuardianFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        try {
            return jSONGuardianFormat.parse(date);
        } catch (ParseException e) {
            Log.e(errorTag, "Error parsing json date:" + e.getMessage());
            return null;
        }
    }

    public static String getFormatedDate(Date input) {
        SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy/MM/dd",Locale.US);
        return formattedDate.format(input);
    }

    public static ArrayList<Story> getStoriesList(String urlString, int currentPage, int numberOfPagesToFetch) {
        String fromHttpResult = Utility.fetchStringFromHttp(urlString);
        //Log.e(Utility.errorTag,fromHttpResult);
        return Utility.extractJsonNews(fromHttpResult, urlString, currentPage, numberOfPagesToFetch);
    }
}
