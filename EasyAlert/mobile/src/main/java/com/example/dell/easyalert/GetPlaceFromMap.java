package com.example.dell.easyalert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by DELL on 15-04-2016.
 */
public class GetPlaceFromMap extends AppCompatActivity {


    GoogleMap map;
    LatLng finalPoint = null;
    Utility utility = new Utility();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_place_from_map);

        final Button selectPlace = (Button) findViewById(R.id.selectPlace);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(true);
        selectPlace.setEnabled(false);



        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
            utility.showGpsOffDialog(this);

        Location location = utility.getCurrentLocation(this);
        if (location != null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.clear();
                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1))
                        .anchor(0.5f, 1.0f) // Anchors the marker on the sharp point
                        .position(point));
                finalPoint = point;
                selectPlace.setEnabled(true);
            }
        });

        selectPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalPoint == null) {
                    Toast.makeText(GetPlaceFromMap.this, "Select a Location First!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = GetPlaceFromMap.this.getIntent();
                    intent.putExtra(Constants.LATITUDE, finalPoint.latitude).putExtra(Constants.LONGITUDE, finalPoint.longitude);
                    GetPlaceFromMap.this.setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home)
        {   finish();
            return true;}

        return super.onOptionsItemSelected(item);
    }


}
