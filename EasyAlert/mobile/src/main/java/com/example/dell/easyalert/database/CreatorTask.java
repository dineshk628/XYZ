package com.example.dell.easyalert.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by DELL on 14-04-2016.
 */
public class CreatorTask {
    public static final String contentAUTHORITY = "com.example.dell.easyalert";
    public static final Uri baseCONTENT_URI = Uri.parse("content://" + contentAUTHORITY);
    public static String pathTASKS = "tasks";
    public static String pathLOCATION = "location";

    public static final class LocationInput implements BaseColumns {

        public static Uri CONTENT_URI = baseCONTENT_URI.buildUpon().appendPath(pathLOCATION).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + '/' + contentAUTHORITY + '/' + pathLOCATION;

        //columns for location input
        public static final String tableNAME = "location";
        public static final String placeNAME = "location_name";
        public static final String coordLat = "coord_Lat";
        public static final String coordLong = "coord_Lon";
        public static final String count = "use_count";
        public static final String hidden = "hidden";



        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class TaskInput implements BaseColumns {
        public static Uri contentURI = baseCONTENT_URI.buildUpon().appendPath(pathTASKS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + '/' + contentAUTHORITY + '/' + pathTASKS;


        //column fields for task inputs
        public static final String tableNAME = "tasks";
        public static final String taskNAME = "task_name";
        public static final String locationNAME = "place";
        public static final String locationALARM = "alarm";
        public static final String minDISTANCE = "min_dist";
        public static final String doneSTATUS="done";
        public static final String remindDISTANCE="remind_distance";
        public static final String snoozeTIME="snooze_time";



        public static Uri buildTaskUri(long id) {
            return ContentUris.withAppendedId(contentURI, id);

        }


    }

}
