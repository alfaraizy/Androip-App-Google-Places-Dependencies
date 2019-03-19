package com.example.lenovo.nearbylocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lenovo.nearbylocation.model.MyPlaces;
import com.example.lenovo.nearbylocation.model.Results;
import com.example.lenovo.nearbylocation.remote.IGoogleAPIService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private double latitude, longitude;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;

    IGoogleAPIService mService;


    MyPlaces currentPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //init Service
        mService = Common.getGoogleAPIService();


        //Request Runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CheckLocationPermission();
        }

       BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_restaurant:
                        nearByPlaces("restaurant");
                        break;
                    case R.id.action_attraction:
                        nearByPlaces("amusement_park");
                        break;
                    case R.id.action_subway:
                        nearByPlaces("subway_station");
                        break;
                    case  R.id.action_mosque:
                        nearByPlaces("mosque");
                        break;
                    default:
                        break;

                }

                return true;
            }
        });
    }

    private void nearByPlaces(final String placeType) {
        mMap.clear();
        String url = getUrl(latitude, longitude, placeType);
        mService.getNearbyPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                     @Override
                     public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                         currentPlace = response.body();

                         if(response.isSuccessful()){
                             for(int i=0;i < response.body().getResults().length; i++){
                                 MarkerOptions markerOptions = new MarkerOptions();
                                 Results googlePlace = response.body().getResults()[i];
                                 double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                 double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                 String PlaceName = googlePlace.getName();
                                 String vicinity = googlePlace.getVicinity();
                                 LatLng latLng = new LatLng(lat, lng);
                                 markerOptions.position(latLng);
                                 markerOptions.title(PlaceName);
                                 if(placeType.equals("restaurant")){
                                     markerOptions.icon(BitmapDescriptorFactory.
                                             defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                     //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant));
                                 }else if(placeType.equals("mosque")){
                                     markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                     //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image2vector));
                                 }else if(placeType.equals("amusement_park")){
                                     markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                     //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_toys_black_24dp));
                                 }else if(placeType.equals("subway_station")){
                                     markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                     //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_subway_black_24dp));
                                 }

                                 markerOptions.snippet(String.valueOf(i)); //Assign index for marker
                                 mMap.addMarker(markerOptions);

                                 mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                 mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                             }
                         }

                 }

                     @Override
                     public void onFailure(Call<MyPlaces> call, Throwable t) {
                     }
                 });
    }

    private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longitude);
        if(placeType == "restaurant")
        {
            googlePlacesUrl.append("&radius="+1000);
        } else if( placeType == "mosque") {
            googlePlacesUrl.append("&radius="+50000);
        } else {
            googlePlacesUrl.append("&radius="+10000);
        }
        googlePlacesUrl.append("&type="+placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("getUrl", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }

    private boolean CheckLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },MY_PERMISSION_CODE );
            else
                ActivityCompat.requestPermissions(this, new String[]{

                        Manifest.permission.ACCESS_FINE_LOCATION
                },MY_PERMISSION_CODE );
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                            mMap.setMyLocationEnabled(true);
                        }
                    }
                }else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }
            break;
        }
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }else{
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }

        //event click on marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // when user select marker then get result of place and Assign to static variable
                Common.currentResult = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];

                // start new Activity
                startActivity(new Intent(MapsActivity.this,ViewPlace.class));

                return true;
            }
        });
        }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
             LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(mMarker != null ){
            mMarker.remove();
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude,longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("You Are Here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMarker = mMap.addMarker(markerOptions);

        //move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if(mGoogleApiClient!= null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
    }

