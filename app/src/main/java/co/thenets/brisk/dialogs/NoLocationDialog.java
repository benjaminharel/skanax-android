package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.events.LocationChangedEvent;
import co.thenets.brisk.events.LocationDialogEvent;
import co.thenets.brisk.managers.EventsManager;

/**
 * Created by DAVID on 29/02/2016.
 */
public class NoLocationDialog extends DialogFragment
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventsManager.getInstance().register(this);
    }

    @Override
    public void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View rootView = inflater.inflate(R.layout.dialog_no_location, null);

        //Set to null. We override the onclick
        builder.setView(rootView).setPositiveButton(R.string.retry, null);
        builder.setView(rootView).setNegativeButton(R.string.exit_app, null);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        EventsManager.getInstance().post(new LocationDialogEvent(true));
                        dismiss();
                    }
                });

                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        EventsManager.getInstance().post(new LocationDialogEvent(false));
                        dismiss();
                    }
                });
            }
        });
        return alertDialog;
    }

    public static NoLocationDialog newInstance()
    {
        NoLocationDialog dialogFragment = new NoLocationDialog();
        return dialogFragment;
    }

    @Subscribe
    public void onLocationStateChanged(LocationChangedEvent locationChangedEvent)
    {
        if(locationChangedEvent.isDeviceLocationTurnsOn())
        {
            dismiss();
        }
    }
}
