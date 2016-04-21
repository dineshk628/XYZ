package com.example.dell.easyalert;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by DELL on 15-04-2016.
 */
public class TasksAdapter extends CursorAdapter {

    Utility utility=new Utility();
    public TasksAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_task, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView taskDistance = (TextView) view.findViewById(R.id.task_dist_textView);
        TextView taskNameView = (TextView) view.findViewById(R.id.task_name_textView);
        TextView taskLoc = (TextView) view.findViewById(R.id.task_location_textView);
        LinearLayout listItemLayout = (LinearLayout) view.findViewById(R.id.list_item_layout);

        //TODO: what is this?
        TasksFragment.distance = cursor.getInt(Constants.COL_MIN_DISTANCE);
        String task = cursor.getString(Constants.COL_TASK_NAME);
        String taskLocation = cursor.getString(Constants.COL_LOCATION_NAME);

        taskNameView.setText(task);
        taskLoc.setText(taskLocation);
        taskDistance.setText(utility.getDistanceDisplayString(context,TasksFragment.distance));

        //If task is marked as Done, then strikethrough the text.
        if(cursor.getString(Constants.COL_DONE).equals("true"))
        {
            taskNameView.setPaintFlags(taskNameView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            taskDistance.setVisibility(View.INVISIBLE);
        }
        else
        {
            taskNameView.setPaintFlags(taskNameView.getPaintFlags()&(~Paint.STRIKE_THRU_TEXT_FLAG));
            taskDistance.setVisibility(View.VISIBLE);
        }
    }
}
