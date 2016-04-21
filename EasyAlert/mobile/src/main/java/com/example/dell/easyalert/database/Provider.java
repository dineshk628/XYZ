package com.example.dell.easyalert.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by DELL on 14-04-2016.
 */
public class Provider extends ContentProvider{

    public static DBSqlHelperTask mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int TASKS = 100;
    static final int LOCATION = 300;

    // get access to the database helper
    @Override
    public boolean onCreate() {
        Log.e("ContentProvider", "getting Instance");
        mOpenHelper = DBSqlHelperTask.getInst(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                Log.e("Cursor Query","Inside task case");
                retCursor = mOpenHelper.getReadableDatabase().query(CreatorTask.TaskInput.tableNAME, projection, selection, selArgs, null, null, sortOrder);
                Log.e("Cursor Query","Inside task case"+retCursor);
                break;
            case LOCATION:
                retCursor = mOpenHelper.getReadableDatabase().query(CreatorTask.LocationInput.tableNAME, projection, selection, selArgs, null, null, sortOrder);
                Log.e("Cursor Query","Inside loca case"+retCursor);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    static UriMatcher buildUriMatcher() {

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        Log.v("Provider","in buildUriMatcher");
        String authority = CreatorTask.contentAUTHORITY;
        matcher.addURI(authority, CreatorTask.pathTASKS, TASKS);
        matcher.addURI(authority, CreatorTask.pathLOCATION, LOCATION);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                return CreatorTask.TaskInput.CONTENT_TYPE;
            case LOCATION:
                return CreatorTask.LocationInput.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;
        Log.v("++LOG++", "=====================IN INsErt......URI matcher resulted in  " + sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                _id = db.insert(CreatorTask.TaskInput.tableNAME, null, contentValues);
                Log.v("tasks", "Case Tasks  " + _id);
                Log.v("Task", "Values=" + contentValues);
                if (_id > 0) {
                    returnUri = CreatorTask.TaskInput.buildTaskUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);

                }
                break;

            case LOCATION:
                _id = db.insert(CreatorTask.LocationInput.tableNAME, null, contentValues);
                Log.v("location", "Case Location  " + _id);
                Log.v("Location","Values=="+contentValues);
                if (_id > 0) {
                    returnUri = CreatorTask.LocationInput.buildLocationUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selArgs) {
        int rowsDeleted = 0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                rowsDeleted = db.delete(CreatorTask.TaskInput.tableNAME, selection, selArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(CreatorTask.LocationInput.tableNAME, selection, selArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        db.close();

        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {

            case TASKS:
                rowsUpdated = db.update(CreatorTask.TaskInput.tableNAME, contentValues, selection, selectionArgs);
                break;

            case LOCATION:
                rowsUpdated = db.update(CreatorTask.LocationInput.tableNAME, contentValues, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri :" + uri);

        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return rowsUpdated;
    }


}
