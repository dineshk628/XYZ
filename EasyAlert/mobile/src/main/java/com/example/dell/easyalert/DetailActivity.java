package com.example.dell.easyalert;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.easyalert.database.CreatorTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by DELL on 15-04-2016.
 */
public class DetailActivity extends AppCompatActivity {

    int REQUEST_CODE_EDIT = 16;
    Cursor c;
    GoogleMap map;
    TextView distanceView;
    String doneStatus;
    String ID;
    Utility utility = new Utility();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        LinearLayout baseLayout = (LinearLayout) findViewById(R.id.detail_base_layout);
        final TextView taskNameView = (TextView) findViewById(R.id.detail_task_name);
        TextView locationView = (TextView) findViewById(R.id.detail_location_name);
        distanceView = (TextView) findViewById(R.id.detail_distance);
        TextView alarmView = (TextView) findViewById(R.id.detail_alarm);
        final Button markDone = (Button) findViewById(R.id.btnMarkDone);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.detail_map)).getMap();
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

        final Location currentLocation = utility.getCurrentLocation(this);
        int distance = 0;

        Intent intent = this.getIntent();
        ID = intent.getStringExtra(Constants.TaskID);
        Uri uri = CreatorTask.TaskInput.contentURI;

        c = this.getContentResolver().query(uri,
                Constants.PROJECTION_TASKS,
                CreatorTask.TaskInput._ID + "=?",
                new String[]{ID}, null);

        if (c.moveToFirst()) {
            taskNameView.setText(c.getString(Constants.COL_TASK_NAME));
            locationView.setText(c.getString(Constants.COL_LOCATION_NAME));
            doneStatus = c.getString(Constants.COL_DONE);
            if (doneStatus.equals("true"))
                markDone.setText("Mark Not Done");

            distance = utility.getDistanceByPlaceName(c.getString(Constants.COL_LOCATION_NAME), currentLocation, this);
            if (distance == 0)
                distance = c.getInt(Constants.COL_MIN_DISTANCE);
            distanceView.setText(utility.getDistanceDisplayString(this, distance));

            if (c.getString(Constants.COL_ALARM).equals("true"))
                alarmView.setText("On");
            else
                alarmView.setText("Off");

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            utility.getLatLngByPlaceName(this,
                                    c.getString(Constants.COL_LOCATION_NAME)), 15)
            );
            map.clear();
            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1))
                    .anchor(0.5f, 1.0f)
                    .position(utility.getLatLngByPlaceName(this, c.getString(Constants.COL_LOCATION_NAME))));
        }

        assert markDone != null;
        markDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doneStatus.equals("false"))                      //Task Not Done Yet
                    markButtonClicked("true");
                else
                    markButtonClicked("false");
            }
        });
        Log.e("TAG", "Reading from Database...Remind distance c.getString:" + c.getString(Constants.COL_REMIND_DIS) + " c.getInt:" + c.getInt(Constants.COL_REMIND_DIS));
    }

    public void markButtonClicked(String done)
    {
        ContentValues taskValues = new ContentValues();

        taskValues.put(CreatorTask.TaskInput.taskNAME, c.getString(Constants.COL_TASK_NAME));
        taskValues.put(CreatorTask.TaskInput.locationNAME, c.getString(Constants.COL_LOCATION_NAME));
        taskValues.put(CreatorTask.TaskInput.locationALARM, c.getString(Constants.COL_ALARM));
        taskValues.put(CreatorTask.TaskInput.minDISTANCE, c.getInt(Constants.COL_MIN_DISTANCE));
        taskValues.put(CreatorTask.TaskInput.doneSTATUS, done);
        taskValues.put(CreatorTask.TaskInput.snoozeTIME, c.getString(Constants.COL_SNOOZE));
        taskValues.put(CreatorTask.TaskInput.remindDISTANCE, c.getString(Constants.COL_REMIND_DIS));

        DetailActivity.this.getContentResolver().update(
                CreatorTask.TaskInput.contentURI,
                taskValues, CreatorTask.TaskInput._ID + "=?",
                new String[]{c.getString(Constants.COL_TASK_ID)}
        );
        c.close();
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_delete) {
            deleteTask(c.getString(Constants.COL_TASK_ID), true);
            return true;
        } else if (id == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String m = "Task Name: " + c.getString(Constants.COL_TASK_NAME) +
                    "\nTask Location: " + c.getString(Constants.COL_LOCATION_NAME) +
                    "\nDistance From Current Location: " + distanceView.getText().toString() + "\n#Easy Alert";
            intent.setType("text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intent.EXTRA_TEXT, m);
            if (intent.resolveActivity(this.getPackageManager()) != null)
                startActivity(intent);
            else
                Toast.makeText(this, "No app found to share the Details", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_edit) {
            editTask();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public int deleteTask(final String task_ID, boolean showDialog) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Delete !");
        alertDialog.setMessage("Delete this Task?");

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DetailActivity.this.getContentResolver().delete(CreatorTask.TaskInput.contentURI, CreatorTask.TaskInput._ID + "=?",
                                new String[]{task_ID});
                        c.close();
                        finish();
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        if (showDialog)
            alertDialog.show();
        else {
            getContentResolver().delete(CreatorTask.TaskInput.contentURI, CreatorTask.TaskInput._ID + "=?",
                    new String[]{task_ID});
            c.close();
            finish();
        }

        return 0;
    }

    public void editTask() {
        Intent editIntent = new Intent(this, AddNewTaskActivity.class);
        editIntent.putExtra(Constants.tName, c.getString(Constants.COL_TASK_NAME));
        editIntent.putExtra(Constants.tLocation, c.getString(Constants.COL_LOCATION_NAME));
        editIntent.putExtra(Constants.tAlarm, c.getString(Constants.COL_ALARM));
        editIntent.putExtra(Constants.tRemDis, c.getInt(Constants.COL_REMIND_DIS));

        startActivityForResult(editIntent, REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT) {
            if (resultCode == RESULT_OK)
                deleteTask(ID, false);
        }
    }
}
