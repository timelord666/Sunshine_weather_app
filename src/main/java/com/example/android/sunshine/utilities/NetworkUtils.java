/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.sunshine.data.SunshinePreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();




    private static final String FORECAST_QUERY_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    private static final String FORECAST_COORD_BASE_URL = "https://api.openweathermap.org/data/2.5/onecall";

    private static final String API_KEY = "ur api key";




    /* The units we want our API to return */
    private static final String units = "metric";


    // This is what we excluding from response, we only need daily forecast
    private static final  String parts = "current,minutely,hourly";



    /* The query parameter allows us to provide a location string to the API */
    private static final String QUERY_PARAM = "q";
    private static final String LAT_PARAM = "lat";
    private static final String LON_PARAM = "lon";
    private static final String PART_PARAM = "exclude";

    private static final String KEY_PARAM = "appid";






    /* The units parameter allows us to designate whether we want metric units or imperial units */
    private static final String UNITS_PARAM = "units";


    /**
     * Retrieves the proper URL to query for the weather data. The reason for both this method as
     * well as {@link #buildUrlWithLocationQuery(String)} is two fold.
     * <p>
     * 1) You should be able to just use one method when you need to create the URL within the
     * app instead of calling both methods.
     * 2) Later in Sunshine, you are going to add an alternate method of allowing the user
     * to select their preferred location. Once you do so, there will be another way to form
     * the URL using a latitude and longitude rather than just a location String. This method
     * will "decide" which URL to build and return it.
     *
     * @param context used to access other Utility methods
     * @return URL to query weather service
     */

    /*

    Deprecated
     */

//    public static URL getUrl(Context context) {
//        if (SunshinePreferences.isLocationLatLonAvailable(context)) {
//            double[] preferredCoordinates = SunshinePreferences.getLocationCoordinates(context);
//            double latitude = preferredCoordinates[0];
//            double longitude = preferredCoordinates[1];
//            return buildUrlWithLatitudeLongitude(latitude, longitude);
//        } else {
//            String locationQuery = SunshinePreferences.getPreferredWeatherLocation(context);
//            return buildUrlWithLocationQuery(locationQuery);
//        }
//    }

    /**
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.
     *
     * @param latitude  The latitude of the location
     * @param longitude The longitude of the location
     * @return The Url to use to query the weather server.
     */
    public static URL buildUrlWithLatitudeLongitude(Double latitude, Double longitude) {
        Uri weatherQueryUri = Uri.parse(FORECAST_COORD_BASE_URL).buildUpon()
                .appendQueryParameter(LAT_PARAM, String.valueOf(latitude))
                .appendQueryParameter(LON_PARAM, String.valueOf(longitude))
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(PART_PARAM, parts)
                .appendQueryParameter(KEY_PARAM, API_KEY)
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param locationQuery The location that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrlWithLocationQuery(String locationQuery) {
        Uri weatherQueryUri = Uri.parse(FORECAST_QUERY_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(KEY_PARAM, API_KEY)
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}
