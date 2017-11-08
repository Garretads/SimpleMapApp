package com.simplemapapp.sergej.simplemapapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    public Marker marker;
    TextView tvLocality;
    TextView tvLat;
    TextView tvLng;
    TextView tvSnippet;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // goToLocationZoom(37,37,15);

        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View infoView = getLayoutInflater().inflate(R.layout.info_window, null);

                    tvLocality = (TextView) infoView.findViewById(R.id.tv_locality);
                    tvLat = (TextView) infoView.findViewById(R.id.tv_lat);
                    tvLng = (TextView) infoView.findViewById(R.id.tv_lng);
                    tvSnippet = (TextView) infoView.findViewById(R.id.tv_snippet);

                    LatLng ll = marker.getPosition();
                    tvLocality.setText(marker.getTitle().toString());
                    tvLat.setText("Latitude "+ll.latitude);
                    tvLng.setText("Longitude "+ll.longitude);
                    tvSnippet.setText(marker.getSnippet());
                    return infoView;
                }
            });
        }
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
    }

    public void geoLocate(View view) throws IOException {
        String location = ((EditText) findViewById(R.id.enterLocation)).getText().toString();
        Geocoder mGeocoder = new Geocoder(this);
        List<Address> addressList = mGeocoder.getFromLocationName(location, 1);
        Address address = addressList.get(0);
        String locality = address.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
        goToLocationZoom(address.getLatitude(), address.getLongitude(), 15);
        double lat = address.getLatitude();
        double lng = address.getLongitude();

        setMarker(locality, lat, lng);

    }

    public void setMarker(String locality, double lat, double lng) {
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions options = new MarkerOptions()
                                .title(locality)
                                .position(new LatLng(lat, lng))
                                .draggable(true)
                                .snippet("Chosen location");
        marker = mMap.addMarker(options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null) {
            Toast.makeText(this, "Can't get current location", Toast.LENGTH_LONG).show();
        }
        else {
            LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng,20);
            mMap.animateCamera(update);
        }
    }

    public void onClick(View view) {
        String nearbyName;
        Object dataTransfer[] = new Object[2];
        switch (view.getId()) {
            case R.id.button: {
                String location = null;
                if ((findViewById(R.id.enterLocation)) != null) {
                    location = ((EditText)findViewById(R.id.enterLocation)).getText().toString();
                }
                List<Address> addressList = null;
                MarkerOptions markerOptions = new MarkerOptions();

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                        Address selAddress = addressList.get(0);
                        latitude = selAddress.getLatitude();
                        longitude = selAddress.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        markerOptions.position(latLng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                }
            }
            break;
            case R.id.B_hospital: {
                mMap.clear();
                nearbyName = "hospital";
                String url = getUrl(latitude,longitude,nearbyName);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this,"Show nearby hospitals",Toast.LENGTH_SHORT).show();


            }
            break;
            case R.id.B_restaurant: {
                mMap.clear();
                nearbyName = "restaurant";
                String url = getUrl(latitude,longitude,nearbyName);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this,"Show nearby restaurants",Toast.LENGTH_SHORT).show();

            }
            break;
            case R.id.B_school: {
                mMap.clear();
                nearbyName = "school";
                String url = getUrl(latitude,longitude,nearbyName);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this,"Show nearby schools",Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                break;
        }
    }
    private String getUrl(double latitude, double longitude,String nearbyPlaceName) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlaceName);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=AIzaSyDEdVVsRJELaHeiFRHrHA8fQMKg2iZ2LtY");
        return googlePlaceUrl.toString();
    }
}
