package cc.pnq.spark.home;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.text.format.DateFormat;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.Dialog;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
                            implements TimePickerDialog.OnTimeSetListener {
    
    public int defaultHour   = -1;
    public int defaultMinute = -1;

    public void setDefaultTime( int hour, int minute ) {
        defaultHour   = hour;
        defaultMinute = minute;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        int hour;
        int minute;

        if ( defaultHour == -1 || defaultMinute == -1 ) {
            hour   = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } else {
            hour   = defaultHour;
            minute = defaultMinute;
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(
            getActivity(),
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(getActivity())
        );
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
    }
}

