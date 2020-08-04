package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import co.thenets.brisk.R;
import co.thenets.brisk.enums.OrderState;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Order;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.UpdateOrderStateRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID on 11/01/2016.
 */
public class ReportDialog extends DialogFragment implements View.OnClickListener
{
    private View mRootView;
    private Button mSendButton;
    private TextView mTitleTextView;
    private Order mOrder;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButtonOptionA;
    private RadioButton mRadioButtonOptionB;
    private RadioButton mRadioButtonOptionC;
    private EditText mReasonEditText;

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
        mRootView = inflater.inflate(R.layout.dialog_report_layout, null);
        setViews();

        builder.setView(mRootView);
        return builder.create();
    }

    public static ReportDialog newInstance(Order order)
    {
        ReportDialog dialogFragment = new ReportDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Params.ORDER, order);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    private void setViews()
    {
        mTitleTextView = (TextView) mRootView.findViewById(R.id.titleTextView);
        mTitleTextView.setText(mOrder.getCustomer().getName());
        mSendButton = (Button) mRootView.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(this);

        mReasonEditText = (EditText) mRootView.findViewById(R.id.reasonEditText);
        mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.radioGroup);
        mRadioButtonOptionA = (RadioButton) mRootView.findViewById(R.id.radioButtonOptionA);
        mRadioButtonOptionB = (RadioButton) mRootView.findViewById(R.id.radioButtonOptionB);
        mRadioButtonOptionC = (RadioButton) mRootView.findViewById(R.id.radioButtonOptionC);
        mRadioButtonOptionC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    mReasonEditText.setVisibility(View.VISIBLE);
                }
                else
                {
                    mReasonEditText.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.sendButton:
                    // If any child selected
                    if(mRadioGroup.getCheckedRadioButtonId() > 0)
                    {
                        if(mRadioButtonOptionC.isChecked())
                        {
                            if(TextUtils.isEmpty(mReasonEditText.getText()))
                            {
                                UIManager.getInstance().displaySnackBarError(v, getString(R.string.description_missing), Snackbar.LENGTH_SHORT);
                            }
                            else
                            {
                                report(v);
                            }
                        }
                        else
                        {
                            report(v);
                        }
                    }
                    else
                    {
                        UIManager.getInstance().displaySnackBarError(v, getString(R.string.select_option), Snackbar.LENGTH_SHORT);
                    }
                break;
        }
    }

    private void report(View v)
    {
        // Get the selected option, and set the reason field
        String reason = "";
        int radioButtonID = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) mRadioGroup.findViewById(radioButtonID);
        int indexOfChild = mRadioGroup.indexOfChild(radioButton);
        switch (indexOfChild)
        {
            case 1:
            case 2:
                reason = radioButton.getText().toString();
                break;
            case 3:
                reason = mReasonEditText.getText().toString();
                break;
        }

        UpdateOrderStateRequest updateOrderStateRequest = new UpdateOrderStateRequest(OrderState.DISPUTE.getRelatedAction());
        updateOrderStateRequest.setData(reason);
        RestClientManager.getInstance().updateOrderState(mOrder.getID(), updateOrderStateRequest, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                getActivity().finish();
                dismiss();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                UIManager.getInstance().displaySnackBarError(mSendButton, getString(R.string.error_in_reporting_customer_please_call_support), Snackbar.LENGTH_LONG);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                UIManager.getInstance().displaySnackBarError(mSendButton, getString(R.string.error_in_reporting_customer_please_call_support), Snackbar.LENGTH_LONG);
            }
        });
    }
}
