package co.thenets.brisk.dialogs;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import co.thenets.brisk.R;
import co.thenets.brisk.general.Params;

public class RateUsOnStoreDialog extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.thank_you));
        builder.setMessage(getString(R.string.just_another_minute_of_your_time_please_rate_us_on_the_app_store))
                .setPositiveButton(R.string.lets_do_it, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Params.APP_LINK_ON_PLAY)));
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}