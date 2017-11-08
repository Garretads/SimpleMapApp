package com.simplemapapp.sergej.simplemapapp;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sergej on 24.08.17.
 */

public class GetNearbyPlacesData extends AsyncTask<Object,String,String> {
    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... params) {
        mMap = (GoogleMap)params[0];
        url = (String)params[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();

        }
        return googlePlacesData;
    }

    private void showNearbyPlaces(List<HashMap<String,String>> nearbyPlacesList) {
        for (int i=0; i<nearbyPlacesList.size();i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlePlace = nearbyPlacesList.get(i);
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            markerOptions.position(new LatLng(lat,lng));
            markerOptions.title(placeName + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }
    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);
        showNearbyPlaces(nearbyPlacesList);
    }
}
