package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import co.thenets.brisk.R;
import co.thenets.brisk.interfaces.ConfirmationListener;

/**
 * Created by DAVID-WORK on 07/03/2016.
 */
public class ConfirmationDialog extends DialogFragment
{
    public static final String CONFIRMATION_LISTENER = "confirmationListener";
    public static final String DIALOG_TITLE = "dialogTitle";
    public static final String DIALOG_MESSAGE = "dialogMessage";
    private ConfirmationListener mConfirmationListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String dialogTitle = getArguments().getString(DIALOG_TITLE);
        String dialogMessage = getArguments().getString(DIALOG_MESSAGE);
        mConfirmationListener = (ConfirmationListener) getArguments().getSerializable(CONFIRMATION_LISTENER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mConfirmationListener.onApprove();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mConfirmationListener.onCancel();
                    }
                });
        return builder.create();
    }

    public static ConfirmationDialog newInstance(String title, String message, ConfirmationListener confirmationListener)
    {
        ConfirmationDialog dialogFragment = new ConfirmationDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRMATION_LISTENER, confirmationListener);
        bundle.putString(DIALOG_TITLE, title);
        bundle.putString(DIALOG_MESSAGE, message);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }
}
