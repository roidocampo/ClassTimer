package xyz.docampo.roi.classtimer;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimePreference extends DialogPreference {
    private TimePicker picker = null;

    public static final long DEFAULT_TIME = 2*60;
    private long minutes = DEFAULT_TIME;

    public TimePreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setCurrentHour((int) minutes/60);
        picker.setCurrentMinute((int) minutes%60);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            minutes = picker.getCurrentHour() * 60 + picker.getCurrentMinute();
            setSummary(getSummary());
            if (callChangeListener(minutes)) {
                persistLong(minutes);
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        if (restoreValue) {
            if (defaultValue == null) {
                minutes = getPersistedLong(DEFAULT_TIME);
            } else {
                minutes = Long.parseLong(getPersistedString((String) defaultValue));
            }
        } else {
            if (defaultValue == null) {
                minutes = DEFAULT_TIME;
            } else {
                minutes = Long.parseLong((String) defaultValue);
            }
        }
        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        if (minutes >= 12*60) {
            if (minutes / 60 == 12)
                return String.format("%d:%02d PM", 12, minutes % 60);
            else
                return String.format("%d:%02d PM", minutes / 60 - 12, minutes % 60);
        } else {
            if (minutes / 60 == 0)
                return String.format("%d:%02d AM", 12, minutes % 60);
            else
                return String.format("%d:%02d AM", minutes / 60, minutes % 60);
        }
    }
}