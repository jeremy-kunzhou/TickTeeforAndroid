package com.huhukun.tickteeforandroid;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.utils.BooleanUtils;
import com.huhukun.utils.FormatHelper;
import com.huhukun.utils.MyDateUtils;
import com.huhukun.utils.NumberUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by kun on 18/08/2014.
 */
public class ProjectCursorAdapter extends CursorAdapter {

    private static final String TAG = App_Constants.APP_TAG + "ProjectCursorAdapter";
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
        TextView tvStatus = (TextView) view.findViewById(R.id.row_project_progress);
        TextView tvPercentage = (TextView) view.findViewById(R.id.row_project_percentage);

        BigDecimal current = new BigDecimal(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS)));
        BigDecimal target = new BigDecimal(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_TARGET)));

        tvPercentage.setText(String.format("%d%%", NumberUtils.getPercentage(current, target)));
        TextView tvEndDate = (TextView) view.findViewById(R.id.row_project_category);
        String endDate = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_END_AT));
        if(TextUtils.isEmpty(endDate)) {
            tvEndDate.setText(R.string.no_time_limit);
        }else{
            try {
                endDate = FormatHelper.fromUTCStringToLocalString(endDate);
            } catch (ParseException e) {
                Log.e(TAG, "unable to parse endDate in utc format");
            }
            tvEndDate.setText(endDate);
        }

        int status = 0;
        if (current.compareTo(target) >=0)
            status = 2;
        else {
            String start_date_string = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_START_AT));
            String end_date_string = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_END_AT));
            boolean isConsumed = BooleanUtils.parse(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_IS_CONSUMED)));

            if (start_date_string != null && end_date_string != null && !TextUtils.isEmpty(start_date_string) && !TextUtils.isEmpty(end_date_string)) {
                try {
                    Date start = FormatHelper.toLocalDateFromUTCString(start_date_string);
                    Date end = FormatHelper.toLocalDateFromUTCString(end_date_string);
                    Date now = new Date();

                    if(!isConsumed && NumberUtils.getPercentage(current, target) < NumberUtils.getPercentage(start, end, now))
                    {
                        status = -1;
                    }else if (isConsumed && NumberUtils.getPercentage(current, target) > NumberUtils.getPercentage(start, end, now))
                    {
                        status = -1;
                    }
                }catch (ParseException e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
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
