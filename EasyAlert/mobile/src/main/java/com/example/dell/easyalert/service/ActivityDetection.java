package com.example.dell.easyalert.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.dell.easyalert.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by DELL on 14-04-2016.
 */
public class ActivityDetection extends IntentService {

    public ActivityDetection()
    { super("Hello!");  }
    public static final String TAG="ActivityService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ActivityRecognitionResult result=ActivityRecognitionResult.extractResult(intent);
        ArrayList<DetectedActivity> detectedActivities=(ArrayList)result.getProbableActivities();

        Intent localIntent= new Intent(Constants.INTENT_FILTER);
        localIntent.putParcelableArrayListExtra(Constants.ReceiverIntentExtra, detectedActivities);
        Log.i(TAG, "Sending Broadcast!");
        LocalBroadcastManager local=LocalBroadcastManager.getInstance(this);
        if(local==null)
            Log.i(TAG, "Local Broadcast Manager is NUll");

        local.sendBroadcast(localIntent);
        Log.i(TAG, "Broadcast Sent!");
    }


}
