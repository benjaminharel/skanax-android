package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.events.NetworkStateEvent;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;

/**
 * Created by DAVID on 29/02/2016.
 */
public class NoNetworkDialog extends DialogFragment
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
        final View rootView = inflater.inflate(R.layout.dialog_no_network, null);

        //Set to null. We override the onclick
        builder.setView(rootView).setPositiveButton(R.string.retry, null);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (Utils.isNetworkAvailable(getActivity()))
                        {
                            dismiss();
                        }
                        else
                        {
                            UIManager.getInstance().displaySnackBarError(rootView, getString(R.string.no_internet_connection_error), Snackbar.LENGTH_SHORT);
                        }
                    }
                });
            }
        });

        return alertDialog;
    }

    public static NoNetworkDialog newInstance()
    {
        NoNetworkDialog dialogFragment = new NoNetworkDialog();
        return dialogFragment;
    }

    @Subscribe
    public void onNetworkStateChanged(NetworkStateEvent networkStateEvent)
    {
        if (networkStateEvent.isOn())
        {
            dismiss();
        }
    }
}
