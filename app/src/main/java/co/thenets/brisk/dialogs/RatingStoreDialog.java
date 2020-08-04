package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import co.thenets.brisk.R;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Order;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestCreateOrderListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID on 11/01/2016.
 */
public class RatingStoreDialog extends DialogFragment
{
    private View mRootView;
    private TextView mTitleTextView;
    private RatingBar mRatingBar;
    private Order mOrder;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOrder = (Order) getArguments().getSerializable(Params.ORDER);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        mRootView = inflater.inflate(R.layout.dialog_rating_layout, null);
        setViews();

        //Set to null. We override the onclick
        builder.setView(mRootView).setPositiveButton(R.string.send_rating, null);

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
                        sendRatingToServer(view);
                    }
                });
            }
        });

        return alertDialog;
    }

    public static RatingStoreDialog newInstance(Order order)
    {
        RatingStoreDialog dialogFragment = new RatingStoreDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Params.ORDER, order);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    private void setViews()
    {
        mTitleTextView = (TextView) mRootView.findViewById(R.id.titleTextView);
        mTitleTextView.setText(mOrder.getStore().getName());
        mRatingBar = (RatingBar) mRootView.findViewById(R.id.ratingBar);
    }

    private void sendRatingToServer(View v)
    {
        if (mRatingBar.getRating() == 0)
        {
            UIManager.getInstance().displaySnackBarError(v, getString(R.string.enter_your_rating), Snackbar.LENGTH_SHORT);
        }
        else
        {
            // Convert rating from 5 scale to 100 scale
            int rating = (int) (20 * mRatingBar.getRating());
            RestClientManager.getInstance().rateOrder(mOrder.getID(), rating, new RequestCreateOrderListener()
            {
                @Override
                public void onSuccess(Order order)
                {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.rating_succeeded), Toast.LENGTH_LONG).show();
                    dismiss();
                }

                @Override
                public void onInternalServerFailure(ErrorResponse error)
                {
                    dismiss();
                }

                @Override
                public void onNetworkFailure(RetrofitError error)
                {
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        getActivity().finish();
    }
}
