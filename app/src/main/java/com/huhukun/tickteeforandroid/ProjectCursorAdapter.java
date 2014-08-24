package com.huhukun.tickteeforandroid;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huhukun.tickteeforandroid.model.SqlOpenHelper;

import java.math.BigDecimal;

/**
 * Created by kun on 18/08/2014.
 */
public class ProjectCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private int overdueColor;
    private int inProgressColor;
    private int completeColor;

    public ProjectCursorAdapter(Context context, Cursor cursor, int flag)
    {
        super(context, cursor, flag);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        overdueColor = context.getResources().getColor(R.color.overdue_status);
        inProgressColor = context.getResources().getColor(R.color.in_progress_status);
        completeColor = context.getResources().getColor(R.color.complete_status);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_project_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvName = (TextView) view.findViewById(R.id.row_project_title);
        tvName.setText(cursor.getString((cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_NAME))));
        TextView tvEndDate = (TextView) view.findViewById(R.id.row_project_category);
        tvEndDate.setText(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_END_AT)));
        TextView tvStatus = (TextView) view.findViewById(R.id.row_project_progress);
        BigDecimal expectedProgress = new BigDecimal(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_EXPECTED_PROGRESS)));
        BigDecimal currentProgress = new BigDecimal(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS)));
        int status = currentProgress.compareTo(expectedProgress);
        if (currentProgress.compareTo(new BigDecimal("100")) >=0)
            status = 2;
        switch (status){
            case -1:
                tvStatus.setText(R.string.overdue);
                tvStatus.setTextColor(overdueColor);
                break;
            case 0:
            case 1:
                tvStatus.setText(R.string.in_progress);
                tvStatus.setTextColor(inProgressColor);
                break;
            case 2:
                tvStatus.setText(R.string.complete);
                tvStatus.setTextColor(completeColor);
                break;
            default:
                tvStatus.setText(R.string.unknown);
        }

    }
}
