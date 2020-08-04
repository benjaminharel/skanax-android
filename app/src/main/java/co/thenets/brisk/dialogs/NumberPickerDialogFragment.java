package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import co.thenets.brisk.R;
import co.thenets.brisk.enums.Day;
import co.thenets.brisk.enums.TimeDirection;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.ContentManager;

/**
 * Created by Roei on 07-Dec-15.
 */
public class NumberPickerDialogFragment extends DialogFragment
{
    private Bundle mBundle;
    private NumberPicker mNumberPicker;
    private TimeDirection mTimeDirection;
    Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        mContext = getActivity().getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        final View view = layoutInflater.inflate(R.layout.number_picker_dialog, null);

        mBundle = getArguments();
        builder.setView(view);
        mTimeDirection = (TimeDirection) mBundle.getSerializable(Params.FROM_OR_TO);

        mNumberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(Params.AVAILABILITY_START_HOUR);
        mNumberPicker.setMaxValue(Params.AVAILABILITY_END_HOUR);

        if (mTimeDirection.equals(TimeDirection.FROM))
        {
            mNumberPicker.setValue(Params.DEFAULT_AVAILABILITY_START_HOUR);
        }
        else
        {
            mNumberPicker.setValue(Params.DEFAULT_AVAILABILITY_END_HOUR);
        }
        mNumberPicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPicker.setClickable(true);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mBundle = getArguments();
                Day day = (Day) mBundle.getSerializable(Params.SELECTED_DAY);
                TimeDirection timeDirection = (TimeDirection) mBundle.getSerializable(Params.FROM_OR_TO);
                ContentManager.getInstance().updateWorkingDay(day, timeDirection, mNumberPicker.getValue());
                dismiss();
            }
        });
        return builder.create();
    }
}
