package co.thenets.brisk.fragments;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zooz.api.external.control.ControllerConfiguration;
import com.zooz.api.external.requests.AddPaymentMethod;
import com.zooz.api.internal.exceptions.ZoozException;
import com.zooz.common.client.ecomm.beans.client.beans.ClientPaymentMethodDetails;
import com.zooz.common.client.ecomm.beans.responses.AddPaymentMethodResponse;
import com.zooz.common.client.ecomm.beans.server.response.ZoozServerResponse;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.CCFormActivity;
import co.thenets.brisk.enums.PaymentMethodType;
import co.thenets.brisk.events.AddressAndCCAddedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.BriskCreditCard;
import co.thenets.brisk.models.PaymentMethod;
import co.thenets.brisk.rest.AdvancedConfiguration;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID-WORK on 10/03/2016.
 */
public class SetPaymentMethodFragment extends Fragment implements View.OnClickListener
{
    private static final String LOG_TAG = SetPaymentMethodFragment.class.getSimpleName();
    private int CC_USING_FORM_REQUEST_CODE = 901; // arbitrary int

    private View mRootView;
    private LinearLayout mProgressBarLayout;
    private CardView mCashCardView;
    private CardView mCreditCardView;
    private CardView mPaypalCardView;
    private ColorDrawable mTransparentWhite;
    private TextView mCCNameTextView;
    private TextView mPPNameTextView;
    private ArrayList<PaymentMethod> mPaymentMethodList;

    private boolean mIsCCMethodExists = false;
    private boolean mIsPPMethodExists = false;
    private boolean mIsStoreAcceptedBriskPayment;
    private boolean mIsStoreAcceptedCashPayment;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventsManager.getInstance().register(this);
        mTransparentWhite = new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.transparent_white_1));

        AnalyticsManager.getInstance().addPaymentMethodScreen();
    }

    @Override
    public void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_set_payment_method, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
        getPaymentsMethodsFromServer();
    }

    private void getPaymentsMethodsFromServer()
    {
        RestClientManager.getInstance().getPaymentMethods(new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                mPaymentMethodList = ContentManager.getInstance().getPaymentMethodList();
                setPaymentsMethodsViews();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {

            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {

            }
        });
    }

    private void setPaymentsMethodsViews()
    {
        if (!mPaymentMethodList.isEmpty())
        {
            // Get & Set CC method
            for (PaymentMethod paymentMethod : mPaymentMethodList)
            {
                if (paymentMethod.getMethodType() == PaymentMethodType.CREDIT_CARD)
                {
                    mIsCCMethodExists = true;
                    mCCNameTextView.setText(paymentMethod.getName());
                }
            }
            // Get & Set PP method
            for (PaymentMethod paymentMethod : mPaymentMethodList)
            {
                if (paymentMethod.getMethodType() == PaymentMethodType.PAYPAL)
                {
                    mIsPPMethodExists = true;
                    mCCNameTextView.setText(paymentMethod.getName());
                }
            }
        }

        setViewsVisibility();
    }

    @Override
    public void onResume()
    {
        getActivity().setTitle(getString(R.string.set_payment_method));
        super.onResume();
    }

    private void setViews()
    {
        mCCNameTextView = (TextView) mRootView.findViewById(R.id.ccNameTextView);
        mPPNameTextView = (TextView) mRootView.findViewById(R.id.ppNameTextView);

        mProgressBarLayout = (LinearLayout) mRootView.findViewById(R.id.progressBarLayout);

        mCashCardView = (CardView) mRootView.findViewById(R.id.cashCardView);
        mCreditCardView = (CardView) mRootView.findViewById(R.id.creditCardView);
        mPaypalCardView = (CardView) mRootView.findViewById(R.id.paypalCardView);

        mCreditCardView.setOnClickListener(this);
        mPaypalCardView.setOnClickListener(this);

        handleStoreAcceptedPaymentsOptions();
    }

    private void handleStoreAcceptedPaymentsOptions()
    {
        mIsStoreAcceptedCashPayment = ContentManager.getInstance().getSelectedStore().isCashPaymentAccepted();
        mIsStoreAcceptedBriskPayment = ContentManager.getInstance().getSelectedStore().isBriskPaymentAccepted();

        if (mIsStoreAcceptedCashPayment)
        {
            // Handle the case while the store accepts cash
            mCashCardView.setOnClickListener(this);
        }
        else
        {
            // Handle the case while the store doesn't accepts cash
            mCashCardView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    UIManager.getInstance().displaySnackBarError(mCashCardView, getString(R.string.store_dosent_supported_this_payment_option), Snackbar.LENGTH_LONG);
                }
            });
            mCashCardView.setForeground(mTransparentWhite);
        }

        if (mIsStoreAcceptedBriskPayment)
        {
            // Handle the case while the store accepts Brisk Payment
            mCreditCardView.setOnClickListener(this);

        }
        else
        {
            // Handle the case while the store doesn't accepts Brisk Payment
            mCreditCardView.setForeground(mTransparentWhite);
            mCreditCardView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    UIManager.getInstance().displaySnackBarError(mCreditCardView, getString(R.string.store_dosent_supported_this_payment_option), Snackbar.LENGTH_LONG);
                }
            });
        }
    }

    private void setViewsVisibility()
    {
        if (mIsCCMethodExists)
        {
            mCCNameTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            mCCNameTextView.setVisibility(View.GONE);
        }


        if (mIsPPMethodExists)
        {
            mPPNameTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            mPPNameTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.cashCardView:
                ContentManager.getInstance().setPaymentMethodForCurrentOrder(PaymentMethodType.CASH);
                EventsManager.getInstance().post(new AddressAndCCAddedEvent());
                getActivity().finish();
                break;
            case R.id.creditCardView:
                if (mIsCCMethodExists)
                {
                    ContentManager.getInstance().setPaymentMethodForCurrentOrder(PaymentMethodType.CREDIT_CARD);
                    EventsManager.getInstance().post(new AddressAndCCAddedEvent());
                    getActivity().finish();
                }
                else
                {
                    getCCUsingForm();
                }
                break;
            case R.id.paypalCardView:
                UIManager.getInstance().displaySnackBarError(view, getString(R.string.coming_soon), Snackbar.LENGTH_SHORT);
                break;
        }
    }

    private void getCCUsingForm()
    {
        Intent intent = new Intent(getActivity(), CCFormActivity.class);
        startActivityForResult(intent, CC_USING_FORM_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CC_USING_FORM_REQUEST_CODE)
        {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK)
            {
                if (data != null && data.hasExtra(Params.CC_FORM_EXTRA_DATA))
                {
                    BriskCreditCard creditCardResult = (BriskCreditCard) data.getSerializableExtra(Params.CC_FORM_EXTRA_DATA);
                    Log.d(LOG_TAG, "Got CC details");

                    mCCNameTextView.setVisibility(View.VISIBLE);
                    mCCNameTextView.setText(creditCardResult.getRedactedCardNumber());
                    startTokenizationViaZoozServer(creditCardResult);
                }
            }
        }
    }

    private void startTokenizationViaZoozServer(final BriskCreditCard creditCard)
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        // Get Payment Token from Our server
        RestClientManager.getInstance().getPaymentToken(new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                Log.d(LOG_TAG, "Got PaymentToken for our server");
                getPaymentMethodToken(creditCard);
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mCashCardView, getString(R.string.error_in_payment_details), Snackbar.LENGTH_LONG);

            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mCashCardView, getString(R.string.error_in_payment_details), Snackbar.LENGTH_LONG);
            }
        });
    }


    private ClientPaymentMethodDetails getClientPaymentMethodDetails(BriskCreditCard creditCard)
    {
        ClientPaymentMethodDetails clientPaymentMethodDetails = new ClientPaymentMethodDetails();
        clientPaymentMethodDetails.setCardHolderName("");
        clientPaymentMethodDetails.setCardNumber(creditCard.getNumber());
        clientPaymentMethodDetails.setCvvNumber(creditCard.getCvv());
        clientPaymentMethodDetails.setExpirationDate(creditCard.getExpireMonth() + "/" + creditCard.getExpireYear());
        return clientPaymentMethodDetails;
    }

    private void getPaymentMethodToken(BriskCreditCard creditCard)
    {
        String paymentToken = ContentManager.getInstance().getPaymentToken();

        // Get payment method token from Zooz
        ControllerConfiguration controllerConfiguration = new ControllerConfiguration(AdvancedConfiguration.ZOOZ_SERVER_URL, AdvancedConfiguration.ZOOZ_PROGRAM_ID, paymentToken, getActivity());
        ClientPaymentMethodDetails clientPaymentMethodDetails = getClientPaymentMethodDetails(creditCard);
        final AddPaymentMethod addPaymentMethod = new AddPaymentMethod(paymentToken, ContentManager.getInstance().getUser().getEmail(), null, clientPaymentMethodDetails, true, controllerConfiguration);

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ZoozServerResponse<AddPaymentMethodResponse> addPaymentMethodResponseZoozServerResponse = null;
                try
                {
                    addPaymentMethodResponseZoozServerResponse = addPaymentMethod.postToZooz();
                    String paymentMethodToken = addPaymentMethodResponseZoozServerResponse.getResponseObject().getPaymentMethodToken();
                    Log.d(LOG_TAG, "Got paymentMethodToken for Zooz server");
                    if(!TextUtils.isEmpty(paymentMethodToken))
                    {
                        updateServerWithZoozPaymentMethodToken(paymentMethodToken);
                    }
                    else
                    {
                        mProgressBarLayout.setVisibility(View.GONE);
                        UIManager.getInstance().displaySnackBarError(mCashCardView, getString(R.string.error_in_payment_details), Snackbar.LENGTH_LONG);
                    }
                }
                catch (ZoozException e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void updateServerWithZoozPaymentMethodToken(String paymentMethodToken)
    {
        RestClientManager.getInstance().addPaymentMethod(PaymentMethodType.CREDIT_CARD, paymentMethodToken, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                Log.d(LOG_TAG, "Updated zooz paymentMethodToken on our server");
                mProgressBarLayout.setVisibility(View.GONE);

                // Set views
                mCashCardView.setForeground(mTransparentWhite);
                mCreditCardView.setForeground(null);
                mPaypalCardView.setForeground(mTransparentWhite);

                // Set logic
                mIsCCMethodExists = true;
                ContentManager.getInstance().setPaymentMethodForCurrentOrder(PaymentMethodType.CREDIT_CARD);

                AnalyticsManager.getInstance().addPaymentInfo(PaymentMethodType.CREDIT_CARD);
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mCashCardView, getString(R.string.error_in_payment_details), Snackbar.LENGTH_LONG);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mCashCardView, getString(R.string.error_in_payment_details), Snackbar.LENGTH_LONG);
            }
        });
    }
}
