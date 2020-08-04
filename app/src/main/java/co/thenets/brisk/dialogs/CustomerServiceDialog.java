package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import co.thenets.brisk.R;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.ContentManager;

/**
 * Created by DAVID on 16/02/2016.
 */
public class CustomerServiceDialog extends DialogFragment implements View.OnClickListener
{
    private View mRootView;
    private LinearLayout mCallUsLayout;
    private LinearLayout mEmailUsLayout;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        mRootView = inflater.inflate(R.layout.dialog_customer_service, null);
        setViews();

        builder.setView(mRootView);
        return builder.create();
    }

    public static CustomerServiceDialog newInstance()
    {
        CustomerServiceDialog dialogFragment = new CustomerServiceDialog();
        return dialogFragment;
    }

    private void setViews()
    {
        mCallUsLayout = (LinearLayout) mRootView.findViewById(R.id.callUsLayout);
        mEmailUsLayout = (LinearLayout) mRootView.findViewById(R.id.emailUsLayout);
        mCallUsLayout.setOnClickListener(this);
        mEmailUsLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.callUsLayout:
                Utils.openDialer(getActivity(), Params.CUSTOMER_SERVICE_PHONE);
                break;
            case R.id.emailUsLayout:
                if(ContentManager.getInstance().isUserRegistered())
                {
                    Utils.sendMail(getActivity(), Params.SUPPORT_EMAIL, "CustomerID: " + ContentManager.getInstance().getUser().getCustomerID(), "");
                }
                else
                {
                    Utils.sendMail(getActivity(), Params.SUPPORT_EMAIL, "", "");
                }
                break;
        }
        dismiss();
    }
}
