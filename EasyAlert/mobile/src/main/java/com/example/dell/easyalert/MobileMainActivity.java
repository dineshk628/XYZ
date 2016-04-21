package com.example.dell.easyalert;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.dell.easyalert.service.LocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.example.dell.easyalert.R;

public class MobileMainActivity extends AppCompatActivity {

    public static boolean isServiceOn = false;
    private final String taskFragmentTAG = "TFTAG";
    ToggleButton toggle;
    Utility utility=new Utility();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TasksFragment(), taskFragmentTAG)
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT<23)
            continueNormalWorking();
        else
        {
            checkPermissions();
        }

    }


    public void checkPermissions()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            //TODO:Good to go
            continueNormalWorking();
        }
        else
        {
            requestPermission();
        }
    }

    void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                } else {
                    Toast.makeText(this,"No permissions Granted hence exiting!",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }


    public void continueNormalWorking()
    {
        toggle = (ToggleButton) this.findViewById(R.id.toggle);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            utility.showGpsOffDialog(this);
        }

        if (getAppStatus() && checkPlayServices()) {
            startServ();
            toggle.setChecked(true);
            setToggleBg(true);
        } else setToggleBg(false);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MobileMainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                if (toggle.isChecked()) {
                    if (!isServiceOn)              //If service is not running then start it!
                        startServ();
                    setToggleBg(true);                  //Background Set to Green
                    editor.putString(MobileMainActivity.this.getString(R.string.pref_status_key), "enabled");
                }
                else {
                    if (isServiceOn)
                        stopServ();
                    setToggleBg(false);
                    editor.putString(MobileMainActivity.this.getString(R.string.pref_status_key), "disabled");
                }
                editor.commit();
            }
        });

    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }

    void startServ() {
        startService(new Intent(this, LocationService.class));
        isServiceOn = true;
    }

    void stopServ() {
        stopService(new Intent(this, LocationService.class));
        isServiceOn = false;
    }

    void setToggleBg(boolean green) {
        Drawable bg = toggle.getBackground();
        int color = ContextCompat.getColor(this, R.color.Tomato);
        if (green)
            color = ContextCompat.getColor(this,R.color.Green);

        if (bg instanceof ShapeDrawable)
            ((ShapeDrawable) bg).getPaint().setColor(color);
        else if (bg instanceof GradientDrawable)
            ((GradientDrawable) bg).setColor(color);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //TODO: Change this to SnackBar
            Toast.makeText(this, "Task Added!", Toast.LENGTH_SHORT).show();
            TextView tv = (TextView) this.findViewById(R.id.textView);
            tv.setVisibility(View.INVISIBLE);
        }
    }

    public boolean getAppStatus() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String m = prefs.getString(this.getString(R.string.pref_status_key),
                this.getString(R.string.pref_status_default));
        if (m.equals("enabled"))
            return true;
        else
            return false;
    }
}
