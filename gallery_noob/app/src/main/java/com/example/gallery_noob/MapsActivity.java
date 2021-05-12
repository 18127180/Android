package com.example.gallery_noob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Button save_btn_location, back_btn_location;
    EditText addressField;
    private Intent intent;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationUserMarker;
    private MarkerOptions markerOptions;
    private double cur_lat,cur_long;

    private static final int Request_User_Location_Code=99;

    private void setColor(int lang)
    {
        if (lang!=-1)
        {
            SharedPreferences.Editor editor = getSharedPreferences("SetColor", Context.MODE_PRIVATE).edit();
            editor.putInt("My_color_sl",lang);
            editor.apply();
            if (lang==0)
            {
                setTheme(R.style.ThemeChoice);
            }
            if (lang==1)
            {
                setTheme(R.style.ThemeChoice1);
            }
            if (lang==2)
            {
                setTheme(R.style.ThemeChoice2);
            }
        }
    }

    public void loadColor(){
        SharedPreferences preferences=getSharedPreferences("SetColor", MODE_PRIVATE);
        int language=preferences.getInt("My_color_sl",-1);
        setColor(language);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadColor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        save_btn_location=findViewById(R.id.save_location_btn);
        back_btn_location=findViewById(R.id.exit_location_btn);
        addressField=findViewById(R.id.location_search);

        save_btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data=new Intent();
                data.putExtra("req_lat",cur_lat);
                data.putExtra("req_long",cur_long);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        back_btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        addressField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()==0)
                {
                    save_btn_location.setEnabled(false);
                    save_btn_location.setTextColor(Color.parseColor("#CAC7C7"));
                }
                else
                {
                    save_btn_location.setEnabled(true);
                    save_btn_location.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.search_map_btn:
                Button save_btn=findViewById(R.id.save_location_btn);
                String address=addressField.getText().toString();
                List<Address> addressList = null;
                if (!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder=new Geocoder(this);
                    try {
                        addressList=geocoder.getFromLocationName(address,6);
                        String name_address=getAddress(cur_lat,cur_long);
                        if (addressList!=null)
                        {
                            for (int i=0;i<addressList.size();i++)
                            {
                                Address userAddress=addressList.get(i);
                                cur_lat=userAddress.getLatitude();
                                cur_long=userAddress.getLongitude();
                                addressField.setText(getAddress(cur_lat,cur_long));
                                LatLng latLng=new LatLng(cur_lat,cur_long);

                                currentLocationUserMarker.setPosition(latLng);
                                currentLocationUserMarker.setTitle(address);
//                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

//                                mMap.addMarker(userMarketOptions);

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userAddress.getLatitude(), userAddress.getLongitude()), 17.0f));
                            }
                        }
                        else
                        {
                            Toast.makeText(this,"Location not found!",Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(this,"Please write any location name!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.back_map_btn:
                onBackPressed();
                break;
            case R.id.exit_location_btn:
                onBackPressed();
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

        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }
    }

    public void current_location_image(String address){

        List<Address> addressList = null;
//        MarkerOptions userMarketOptions= new MarkerOptions();
        if (!TextUtils.isEmpty(address))
        {
            Geocoder geocoder=new Geocoder(this);
            try {
                addressList=geocoder.getFromLocationName(address,6);
                if (addressList!=null)
                {
                    for (int i=0;i<addressList.size();i++)
                    {
                        Address userAddress=addressList.get(i);
                        addressField.setText(userAddress.getAddressLine(0));
                        cur_lat=userAddress.getLatitude();
                        cur_long=userAddress.getLongitude();
                        LatLng latLng=new LatLng(cur_lat,cur_long);

                        currentLocationUserMarker.setPosition(latLng);
                        currentLocationUserMarker.setTitle(address);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userAddress.getLatitude(), userAddress.getLongitude()), 17.0f));
                    }
                }
                else
                {
                    Toast.makeText(this,"Location not found!",Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this,"Please write any location name!",Toast.LENGTH_SHORT).show();
        }
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_User_Location_Code:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if (googleApiClient==null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
//                        current_location_image();
                    }
                }
                else {
                    Toast.makeText(this,"Permission Denied...",Toast.LENGTH_SHORT).show();
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String getAddress(double lat, double lng) {
        String name_adress=null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            name_adress = obj.getAddressLine(0);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return name_adress;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastLocation=location;
        if (currentLocationUserMarker != null)
        {
            currentLocationUserMarker.remove();
        }

        cur_lat=location.getLatitude();
        cur_long=location.getLongitude();

        LatLng latLng=new LatLng(cur_lat, cur_long);

        markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        currentLocationUserMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));

        intent=getIntent();
        if (intent!=null)
        {
            double get_lat,get_long;
            get_lat=intent.getDoubleExtra("lat_position",cur_lat);
            get_long=intent.getDoubleExtra("long_position",cur_long);
            if (get_lat!=0 && get_long!=0)
            {
                cur_lat=get_lat;
                cur_long=get_long;
            }
        }

        current_location_image(getAddress(cur_lat,cur_long));

        if (googleApiClient !=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}