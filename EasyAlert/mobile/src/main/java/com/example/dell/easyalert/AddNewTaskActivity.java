package com.example.dell.easyalert;

import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.easyalert.database.CreatorTask;
/**
 * Created by DELL on 15-04-2016.
 */
public class AddNewTaskActivity extends AppCompatActivity {

    int REQUEST_CODE_GET_FROM_MAP = 3;
    int REQUEST_CODE_SAVED_PLACES = 6;

    String mTaskLocation = null;
    int remindDistance, distance;
    TextView selectedLocationDisplayView, remindDistanceView;
    LinearLayout baseLayout;
    ImageButton LocationSelector, selFromMap;
    Button CreateNewTask;
    EditText taskName;
    CheckBox alarmStatus;

    Utility utility = new Utility();

    public void initialize() {
        baseLayout = (LinearLayout) this.findViewById(R.id.newTaskBaseLayout);
        CreateNewTask = (Button) this.findViewById(R.id.createNewTask);
        taskName = (EditText) this.findViewById(R.id.enter_task_name);
        alarmStatus = (CheckBox) this.findViewById(R.id.alarm_switch);
        LocationSelector = (ImageButton) this.findViewById(R.id.locationSelectorButton);
        selFromMap = (ImageButton) this.findViewById(R.id.selectFromMap);
        remindDistanceView = (TextView) this.findViewById(R.id.remind_distanceView);
        selectedLocationDisplayView = (TextView) this.findViewById(R.id.selected_loc_dip_TView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_FROM_MAP) {        //LOCATION SELECTED FROM MAP
            if (resultCode == RESULT_OK) {
                double lat = data.getDoubleExtra(Constants.LATITUDE, 0);
                double lon = data.getDoubleExtra(Constants.LONGITUDE, 0);
                savePlaceDialog(this, lat, lon);
            }
        } else if (requestCode == REQUEST_CODE_SAVED_PLACES)                         //LOCATION SELECTED FROM SAVED PLACES
        {
            if (resultCode == RESULT_OK) {
                mTaskLocation = data.getStringExtra(Constants.savedLocation);
                selectedLocationDisplayView.setText(mTaskLocation);
            }
        }
    }

    public void savePlaceDialog(final Context context, final Double latitude, final Double longitude) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Save the new Place");
        final EditText input = new EditText(context);
        alertDialog.setView(input);
        input.setHint("Place's Name");
        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String place = input.getText().toString();
                        mTaskLocation = place;
                        selectedLocationDisplayView.setText(place);
                        utility.addLocation(context, place, latitude, longitude);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(    //To Hide The Keyboard
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        Toast.makeText(context, "Place Saved", Toast.LENGTH_SHORT).show();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        initialize();

        mTaskLocation = null;
        remindDistance = 50;


        String baseText = "Remind when closer than ";
        remindDistanceView.setText(baseText + utility.getDistanceDisplayString(this, remindDistance));
        selectedLocationDisplayView.setText(null);

        Intent intent1 = this.getIntent();
        if (intent1.hasExtra(Constants.tName))
            setScreenAsEdit(intent1);

        LocationSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SavedLocationListActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SAVED_PLACES);
            }
        });

        selFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GetPlaceFromMap.class);
                startActivityForResult(intent, REQUEST_CODE_GET_FROM_MAP);
            }
        });


        remindDistanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNewTaskActivity.this);
                alertDialog.setTitle("Reminding Range");
                final EditText input = new EditText(AddNewTaskActivity.this);

                if (utility.isMetric(AddNewTaskActivity.this))
                    input.setHint("Enter the distance in m");
                else
                    input.setHint("Enter the distance in yd");

                alertDialog.setMessage("Please Enter the closest distance to the Task Location for reminder");
                alertDialog.setView(input);

                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {
                            int dis = Integer.parseInt(input.getText().toString());
                            int dis_show = dis;
                            if (!utility.isMetric(AddNewTaskActivity.this)) {
                                Double d = dis / 1.09361;
                                dis = d.intValue();
                            }
                            remindDistance = dis;
                            remindDistanceView.setText("Remind when closer than "
                                    + utility.getDistanceDisplayString(AddNewTaskActivity.this, dis_show));

                            InputMethodManager imm = (InputMethodManager) AddNewTaskActivity.this.getSystemService(    //To Hide The Keyboard
                                    AddNewTaskActivity.this.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        } catch (NumberFormatException excep) {
                            Toast.makeText(AddNewTaskActivity.this, "Please enter the distance correctly!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                alertDialog.show();
            }
        });

        CreateNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTaskLocation == null) {
                    Toast.makeText(AddNewTaskActivity.this, "Please select a Location first!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (taskName.getText().toString().equals("")) {
                    Toast.makeText(AddNewTaskActivity.this, "Please Enter the Task Name !", Toast.LENGTH_SHORT).show();
                    return;
                }

                distance = utility.getDistanceByPlaceName(mTaskLocation,
                        utility.getCurrentLocation(AddNewTaskActivity.this),AddNewTaskActivity.this);

                ContentValues taskValues = new ContentValues();

                taskValues.put(CreatorTask.TaskInput.taskNAME, taskName.getText().toString());
                taskValues.put(CreatorTask.TaskInput.locationNAME, mTaskLocation);
                taskValues.put(CreatorTask.TaskInput.locationALARM, String.valueOf(alarmStatus.isChecked()));
                taskValues.put(CreatorTask.TaskInput.minDISTANCE, distance);
                taskValues.put(CreatorTask.TaskInput.doneSTATUS, "false");
                taskValues.put(CreatorTask.TaskInput.snoozeTIME, "0");
                taskValues.put(CreatorTask.TaskInput.remindDISTANCE, remindDistance);

                Uri insertedUri = AddNewTaskActivity.this.getContentResolver()
                        .insert(CreatorTask.TaskInput.contentURI, taskValues);

                if (distance <= remindDistance && distance != 0)
                    Toast.makeText(AddNewTaskActivity.this, getString(R.string.already_in_region), Toast.LENGTH_LONG).show();


                incrementSelectedLocCount(mTaskLocation);

                Intent intent = AddNewTaskActivity.this.getIntent();
                AddNewTaskActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void setScreenAsEdit(Intent intent) {
        CreateNewTask.setText("SAVE EDITS");
        android.support.v7.app.ActionBar aBar = getSupportActionBar();
        if (aBar != null)
            aBar.setTitle("Edit Task");

        String tName = intent.getStringExtra(Constants.tName);
        String tLoc = intent.getStringExtra(Constants.tLocation);
        String tAlarm = intent.getStringExtra(Constants.tAlarm);
        int tRemDis = intent.getIntExtra(Constants.tRemDis, 50);


        mTaskLocation = tLoc;
        remindDistance = tRemDis;

        taskName.setText(tName);
        selectedLocationDisplayView.setText(tLoc);
        if (tAlarm.equals("true"))
            alarmStatus.setChecked(true);
        else
            alarmStatus.setChecked(false);


        String dispString = "Remind when closer than " + remindDistance;
        if (utility.isMetric(this))
            dispString += "m";
        else
            dispString += "yd";
        remindDistanceView.setText(dispString);
    }

    public void incrementSelectedLocCount(String locationName)
    {
        Cursor cc=this.getContentResolver().query(CreatorTask.LocationInput.CONTENT_URI,
                new String[]{CreatorTask.LocationInput.count},
                CreatorTask.LocationInput.placeNAME+"=?",
                new String[]{locationName},
                null);

        if(cc.moveToFirst())
        {
            int count=cc.getInt(0);
            cc.close();
            ContentValues values=new ContentValues();
            values.put(CreatorTask.LocationInput.count,++count);
            this.getContentResolver().update(CreatorTask.LocationInput.CONTENT_URI,
                    values,
                    CreatorTask.LocationInput.placeNAME+"=?",
                    new String[]{locationName});
        }
        if(!cc.isClosed())
            cc.close();
    }


}
