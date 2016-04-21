package com.example.dell.easyalert;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.jar.Manifest;

import com.example.dell.easyalert.database.CreatorTask;

/**
 * Created by DELL on 15-04-2016.
 */
public class Utility {


/*
    public String color;

    public int getColorCodeFromString(Context context, String colorName) {
        int cId;
        if (colorName.equals("Tomato"))
            cId=R.color.Tomato;
        else if (colorName.equals("Tangerine"))
            cId=R.color.Tangerine;
        else if (colorName.equals("Lavender"))
            cId=R.color.Lavender;
        else if (colorName.equals("Peacock"))
            cId=R.color.Peacock;
        else if (colorName.equals("Pink"))
            cId=R.color.Pink;
        else
            cId=R.color.Grape;
        return ContextCompat.getColor(context,cId);
    } */

    public void addLocation(Context context, String placeName, Double latitude, Double longitude) {
        Cursor c = context.getContentResolver().query(CreatorTask.LocationInput.CONTENT_URI,
                new String[]{CreatorTask.LocationInput._ID},
                CreatorTask.LocationInput.placeNAME + "=?",
                new String[]{placeName}, null);

        if (!c.moveToFirst())            //Location doesn't exists in the database; hence insert the Location
        {
            ContentValues locationValues = new ContentValues();
            locationValues.put(CreatorTask.LocationInput.placeNAME, placeName);
            locationValues.put(CreatorTask.LocationInput.coordLat, latitude);
            locationValues.put(CreatorTask.LocationInput.coordLong, longitude);

            Uri insertedUri = context.getContentResolver().insert(
                    CreatorTask.LocationInput.CONTENT_URI,
                    locationValues
            );
        }
        c.close();
    }

    public void showGpsOffDialog(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("No Location Available");
        alertDialog.setMessage("Location is turned Off!\nPlease click Ok to switch it On");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public int getDistanceByPlaceName(String placeName, Location currentLocation, Context context) {
        float distance = 0;
        Location location = new Location("Dummy");

        Cursor c = context.getContentResolver().query(CreatorTask.LocationInput.CONTENT_URI,
                new String[]{CreatorTask.LocationInput.coordLat, CreatorTask.LocationInput.coordLong, CreatorTask.LocationInput.placeNAME},
                CreatorTask.LocationInput.placeNAME + "=?",
                new String[]{placeName}, null);

        if (c.moveToNext()) {
            location.setLatitude(c.getDouble(0));
            location.setLongitude(c.getDouble(1));
        }
        else {
            location.setLatitude(currentLocation.getLatitude());
            location.setLongitude(currentLocation.getLongitude());
        }
        c.close();
        if (currentLocation != null) {
            distance = currentLocation.distanceTo(location);
            int dist = Math.round(distance);

            return dist;
        } else {
            return 0;
        }
    }

    public Location getCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //FIXME

        int permissionCheck1=ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck2=ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);

        //   if(permissionCheck1== PackageManager.PERMISSION_GRANTED && permissionCheck2==PackageManager.PERMISSION_GRANTED)
        {Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return currentLocation;}
        //  return null;
    }

    public LatLng getLatLngByPlaceName(Context context, String placeName) {
        LatLng latLng = null;
        Cursor c = context.getContentResolver().query(CreatorTask.LocationInput.CONTENT_URI,
                new String[]{CreatorTask.LocationInput.coordLat, CreatorTask.LocationInput.coordLong, CreatorTask.LocationInput.placeNAME},
                CreatorTask.LocationInput.placeNAME + "=?",
                new String[]{placeName}, null);
        if (c.moveToNext()) {
            latLng = new LatLng(c.getDouble(0), c.getDouble(1));
        }
        c.close();
        return latLng;
    }

    public boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public String getDistanceDisplayString(Context context, int distance) {
        if (isMetric(context)) {
            if (distance < 1000)
                return (distance + "m");
            else
                return ((float) distance / 1000 + "km");
        } else {
            Double d = distance * 1.09361;
            distance = d.intValue();
            if (distance < 1760)
                return (distance + "yd");
            else
                return ((float) distance / 1760 + "mi");
        }
    }

    public String getSelectedAccuracy(Context context)
    {
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_accuracy_key),context.getString(R.string.pref_accuracy_default));
    }

}
