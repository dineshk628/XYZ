package com.example.dell.easyalert.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;

/**
 * Created by DELL on 14-04-2016.
 */
public class DBSqlHelperTask extends SQLiteOpenHelper {
static public DBSqlHelperTask nInst=null;
    static public final int db_version =2;
    static public String db_name ="task_db.db";

    public  DBSqlHelperTask(Context context){super(context, db_name, null, db_version);}

    public  static  DBSqlHelperTask getInst(Context context)
    {
        if(nInst==null)
            nInst=new DBSqlHelperTask(context.getApplicationContext());
        return nInst;
    }


@Override
public void onCreate(SQLiteDatabase sqLiteDatabase){
    final String CREATE_LOCATION_TABLE = " CREATE TABLE " + CreatorTask.LocationInput.tableNAME + "(" +
            CreatorTask.LocationInput._ID + " INTEGER PRIMARY KEY, " +
            CreatorTask.LocationInput.placeNAME + " TEXT UNIQUE NOT NULL, " +
            CreatorTask.LocationInput.coordLat + " REAL NOT NULL, " +
            CreatorTask.LocationInput.coordLong + " REAL NOT NULL, " +
            CreatorTask.LocationInput.count + " INTEGER DEFAULT 1 ,"+
            CreatorTask.LocationInput.hidden + " INTEGER DEFAULT 0 "+");";

    sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE);



   final String CREATE_TASKS_TABLE = " CREATE TABLE " + CreatorTask.TaskInput.tableNAME + "(" +
            CreatorTask.TaskInput._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CreatorTask.TaskInput.taskNAME + " TEXT NOT NULL, " +
            CreatorTask.TaskInput.locationNAME + " TEXT NOT NULL, " +
            CreatorTask.TaskInput.locationALARM + " TEXT NOT NULL, " +
            CreatorTask.TaskInput.minDISTANCE + " INTEGER NOT NULL, " +
            CreatorTask.TaskInput.doneSTATUS + " TEXT NOT NULL DEFAULT 'false', "+
            CreatorTask.TaskInput.remindDISTANCE + " INTEGER NOT NULL, "+
            CreatorTask.TaskInput.snoozeTIME + " TEXT NOT NULL, "+

            " FOREIGN KEY (" + CreatorTask.TaskInput.locationNAME + ") REFERENCES " +
            CreatorTask.LocationInput.tableNAME + "(" + CreatorTask.LocationInput.placeNAME + ")" +
            ");";

    sqLiteDatabase.execSQL(CREATE_TASKS_TABLE);
}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {

        switch (oldV)
        {
            case 1:
                Log.e("Db Upgrading....","");

                String addCountHidden="ALTER TABLE "
                        + CreatorTask.LocationInput.tableNAME
                        +" ADD COLUMN "+ CreatorTask.LocationInput.count+" INTEGER DEFAULT 1"
                        +";";
                sqLiteDatabase.execSQL(addCountHidden);

                addCountHidden="ALTER TABLE "
                        + CreatorTask.LocationInput.tableNAME
                        +" ADD COLUMN "+CreatorTask.LocationInput.hidden+" INTEGER DEFAULT 0"
                        +";";
                sqLiteDatabase.execSQL(addCountHidden);

                break;
            default:
                Log.e("DbUpgrade","No match");
        }
    }
}