package com.huhukun.tickteeforandroid.UILibrary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by kun on 21/08/2014.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static interface IDatePickerFragmentCallBack {
        void setDate(long targetId, int year, int month, int day);
    }

    private IDatePickerFragmentCallBack callBack;
    private long targetId;

    public void setCallBack(IDatePickerFragmentCallBack callBack){
        this.callBack = callBack;
    }
    public void setTargetId(long targetId) {this.targetId = targetId;}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if(this.callBack != null){
            callBack.setDate(targetId, year,month,day);
        }
    }
}