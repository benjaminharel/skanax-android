package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.thenets.brisk.R;
import co.thenets.brisk.events.StoreUpdatedEvent;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Dashboard;
import co.thenets.brisk.models.Store;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateOrUpdateStoreRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID on 11/01/2016.
 */
public class DeliveryPriceDialog extends DialogFragment
{
    private View mRootView;
    private EditText mDeliveryPriceEditText;
    private Dashboard mDashboard;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDashboard = ContentManager.getInstance().getDashboard();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        mRootView = inflater.inflate(R.layout.dialog_set_delivery_price_layout, null);
        setViews();

        //Set to null. We override the onclick
        builder.setView(mRootView).setPositiveButton(R.string.save, null);

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
                        updateDeliveryPriceOnServer(view);
                    }
                });
            }
        });

        return alertDialog;
    }

    public static DeliveryPriceDialog newInstance()
    {
        DeliveryPriceDialog dialogFragment = new DeliveryPriceDialog();
        return dialogFragment;
    }

    private void setViews()
    {
        mDeliveryPriceEditText = (EditText) mRootView.findViewById(R.id.deliveryPriceEditText);
        mDeliveryPriceEditText.setText(String.valueOf((int)mDashboard.getDeliveryPrice()));
        mDeliveryPriceEditText.setSelection(mDeliveryPriceEditText.getText().length());
        Utils.hideKeyboard(mDeliveryPriceEditText, getActivity());
    }

    private void updateDeliveryPriceOnServer(final View view)
    {
        if(!TextUtils.isEmpty(mDeliveryPriceEditText.getText().toString()))
        {
            Store store = ContentManager.getInstance().getStore();
            store.setDeliveryPrice(Float.valueOf(mDeliveryPriceEditText.getText().toString()));

            RestClientManager.getInstance().updateStore(new CreateOrUpdateStoreRequest(store), new RequestListener()
            {
                @Override
                public void onSuccess()
                {
                    Toast.makeText(getActivity(), getString(R.string.delivery_price_updated), Toast.LENGTH_LONG).show();
                    EventsManager.getInstance().post(new StoreUpdatedEvent(false));
                    dismiss();
                }

                @Override
                public void onInternalServerFailure(ErrorResponse error)
                {
                    UIManager.getInstance().displaySnackBarError(view, getString(R.string.error_in_update_store_details), Snackbar.LENGTH_SHORT);
                }

                @Override
                public void onNetworkFailure(RetrofitError error)
                {
                    UIManager.getInstance().displaySnackBarError(view, getString(R.string.error_in_update_store_details), Snackbar.LENGTH_SHORT);
                }
            });
        }
        else
        {
            UIManager.getInstance().displaySnackBarError(view, getString(R.string.missing_fields_error), Snackbar.LENGTH_SHORT);
        }
    }
}
