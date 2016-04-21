package com.example.dell.easyalert;

import android.support.v7.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.easyalert.database.CreatorTask;
/**
 * Created by DELL on 15-04-2016.
 */
public class AlarmActivity extends AppCompatActivity {


    int alarmTone = R.raw.alarm;
    MediaPlayer mMediaPlayer = new MediaPlayer();
    Cursor c;
    Vibrator vibrator;
    Utility utility=new Utility();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Button stopAlarm = (Button) this.findViewById(R.id.btnStop);
        Button Snooze = (Button) this.findViewById(R.id.snooze);
        TextView taskNameView = (TextView) this.findViewById(R.id.alarm_taskName);
        TextView taskLocView = (TextView) this.findViewById(R.id.alarm_taskLoc);
        TextView taskDisView = (TextView) this.findViewById(R.id.alarmTaskDis);
        LinearLayout baseLayout = (LinearLayout) this.findViewById(R.id.alarmBaseLayout);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        Intent intent = this.getIntent();
        String ID = intent.getStringExtra(Constants.TaskID);

        Uri uri = CreatorTask.TaskInput.contentURI;
        c = this.getContentResolver().query(uri, Constants.PROJECTION_TASKS,
                CreatorTask.TaskInput._ID + "=?",
                new String[]{ID}, null);

        if (c.moveToFirst()) {
            taskNameView.setText(c.getString(Constants.COL_TASK_NAME));
            taskDisView.setText(utility.getDistanceDisplayString(this, c.getInt(Constants.COL_MIN_DISTANCE)));
            taskLocView.setText(c.getString(Constants.COL_LOCATION_NAME));
        }

        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();

                ContentValues taskValues = new ContentValues();
                taskValues.put(CreatorTask.TaskInput.taskNAME, c.getString(Constants.COL_TASK_NAME));
                taskValues.put(CreatorTask.TaskInput.locationNAME,c.getString(Constants.COL_LOCATION_NAME));
                taskValues.put(CreatorTask.TaskInput.locationALARM,c.getString(Constants.COL_ALARM));
                taskValues.put(CreatorTask.TaskInput.minDISTANCE,c.getInt(Constants.COL_MIN_DISTANCE));
                taskValues.put(CreatorTask.TaskInput.doneSTATUS,"true");
                taskValues.put(CreatorTask.TaskInput.snoozeTIME,c.getString(Constants.COL_SNOOZE));
                taskValues.put(CreatorTask.TaskInput.remindDISTANCE,c.getString(Constants.COL_REMIND_DIS));

                AlarmActivity.this.getContentResolver().update(
                        CreatorTask.TaskInput.contentURI,
                        taskValues, CreatorTask.TaskInput._ID + "=?",
                        new String[]{c.getString(Constants.COL_TASK_ID)}
                );
                c.close();
                finish();
            }
        });

        Snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();

                ContentValues taskValues = new ContentValues();

                taskValues.put(CreatorTask.TaskInput.taskNAME, c.getString(Constants.COL_TASK_NAME));
                taskValues.put(CreatorTask.TaskInput.locationNAME, c.getString(Constants.COL_LOCATION_NAME));
                taskValues.put(CreatorTask.TaskInput.locationALARM,c.getString(Constants.COL_ALARM));
                taskValues.put(CreatorTask.TaskInput.minDISTANCE, c.getInt(Constants.COL_MIN_DISTANCE));
                taskValues.put(CreatorTask.TaskInput.doneSTATUS, c.getString(Constants.COL_DONE));
                taskValues.put(CreatorTask.TaskInput.snoozeTIME,System.currentTimeMillis() + Constants.SNOOZE_TIME_DURATION);
                taskValues.put(CreatorTask.TaskInput.remindDISTANCE, c.getString(Constants.COL_REMIND_DIS));

                AlarmActivity.this.getContentResolver().update(
                        CreatorTask.TaskInput.contentURI,
                        taskValues, CreatorTask.TaskInput._ID + "=?",
                        new String[]{c.getString(Constants.COL_TASK_ID)}
                );
                c.close();
                finish();
            }
        });
    }


    public class PlaySoundTask extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AlarmActivity.this);
            String temp = prefs.getString(getString(R.string.pref_tone_key), null);
            Uri uri;

            if (temp == null)
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            else
                uri = Uri.parse(temp);

            try {
                if (uri != null)
                    mMediaPlayer.setDataSource(AlarmActivity.this, uri);
                else
                    mMediaPlayer = MediaPlayer.create(AlarmActivity.this, alarmTone);

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (Exception e) {
                Log.e("TAG", "Exception encounered while playing Alarm!");
            }
            return null;
        }
    }

    @Override
    protected void onResume() {

        SharedPreferences sp = getSharedPreferences("ACTIVITYINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.commit();

        super.onResume();
    }

    @Override
    protected void onPause() {
        vibrator.cancel();
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();

        c.close();
        finish();
        SharedPreferences sp = getSharedPreferences("ACTIVITYINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.commit();

        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        vibrator.vibrate(1000);
        PlaySoundTask m = new PlaySoundTask();
        m.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!c.isClosed()) c.close();
    }

    @Override
    protected void onDestroy() {
        vibrator.cancel();
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        super.onDestroy();
    }


}

