package com.example.dell.easyalert;

import com.example.dell.easyalert.database.CreatorTask;
/**
 * Created by DELL on 15-04-2016.
 */
public class Constants {


    public static String savedLocation="loc";
    public static String TaskID="TaskID";
    public  static  String LATITUDE = "lat", LONGITUDE = "lon";


    /*
    * Service's CONSTANTS
    */
    public static long ActDetectionInterval_ms = 2000;
    public static long UPDATE_INTERVAL = 5000;
    public static long FATEST_INTERVAL = 3000;
    public static int SMALLEST_DISPLACEMENT = 1;
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static String ReceiverIntentExtra="detectedActivities";
    public static String INTENT_FILTER="com.commando.taskNearby"+ReceiverIntentExtra;

    /*
    * Location Table's PROJECTIONS
    */
    public static String PROJECTION_LOC[] = {
            CreatorTask.LocationInput.tableNAME + "." + CreatorTask.LocationInput._ID,
            CreatorTask.LocationInput.placeNAME,
            CreatorTask.LocationInput.coordLat,
            CreatorTask.LocationInput.coordLong,
            CreatorTask.LocationInput.count,
            CreatorTask.LocationInput.hidden
    };

    static final int COL_PLACE_NAME = 1;
    static final int COL_LAT = 2;
    static final int COL_LON = 3;
    static final int COL_COUNT = 4;
    static final int COL_HIDDEN = 5;



    /*
    * Tasks Table's PROJECTIONS
    */
    public static String PROJECTION_TASKS[] = {
            CreatorTask.TaskInput.tableNAME + "." + CreatorTask.TaskInput._ID,
            CreatorTask.TaskInput.taskNAME,
            CreatorTask.TaskInput.locationNAME,
            CreatorTask.TaskInput.doneSTATUS,
            CreatorTask.TaskInput.minDISTANCE,
            CreatorTask.TaskInput.locationALARM,
            CreatorTask.TaskInput.remindDISTANCE,
            CreatorTask.TaskInput.snoozeTIME,
    };

    public static final int COL_TASK_ID = 0;
    public static final int COL_TASK_NAME = 1;
    public static final int COL_LOCATION_NAME = 2;
    public static final int COL_DONE = 3;
    public static final int COL_MIN_DISTANCE = 4;
    public static final int COL_ALARM = 5;
    public static final int COL_REMIND_DIS = 6;
    public static final int COL_SNOOZE=7;


    /*
    * Edit Constants
    */
    public static final String tName="tName";
    public static final String tLocation="tLoc";
    public static final String tAlarm="tAlarm";
    public static final String tRemDis="tRemDis";


    public final static int SNOOZE_TIME_DURATION = 1*60*1000;


}
