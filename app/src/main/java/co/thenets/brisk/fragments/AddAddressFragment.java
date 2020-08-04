package co.thenets.brisk.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import co.thenets.brisk.R;
import co.thenets.brisk.events.AddressAddedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.BriskAddress;
import co.thenets.brisk.services.FetchAddressIntentService;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddAddressFragment extends Fragment implements View.OnClickListener
{
    private static final String LOG_TAG = AddAddressFragment.class.getSimpleName();
    private AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());
    private View mRootView;
    private EditText mCityEditText;
    private EditText mStreetEditText;
    private EditText mHouseNumberEditText;
    private EditText mFloorEditText;
    private EditText mApartmentEditText;
    private EditText mNotesEditText;
    private ImageView mMotorCycleImageView;
    private ImageView mMotorCycleImageViewRe;
    private TranslateAnimation mMovingForwardAnimation;
    private TranslateAnimation mMovingBackwardAnimation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        AnalyticsManager.getInstance().addAddressScreen();
        mRootView = inflater.inflate(R.layout.fragment_add_address, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        getActivity().setTitle(getString(R.string.set_delivery_address));
        super.onViewCreated(view, savedInstanceState);
        setViews();
        startFetchAddressService();
    }

    private void setViews()
    {
        mCityEditText = (EditText) mRootView.findViewById(R.id.cityEditText);
        mStreetEditText = (EditText) mRootView.findViewById(R.id.streetEditText);
        mHouseNumberEditText = (EditText) mRootView.findViewById(R.id.houseNumberEditText);
        mFloorEditText = (EditText) mRootView.findViewById(R.id.floorEditText);
        mApartmentEditText = (EditText) mRootView.findViewById(R.id.apartmentEditText);
        mNotesEditText = (EditText) mRootView.findViewById(R.id.notesEditText);
        Button addAddressButton = (Button) mRootView.findViewById(R.id.addAddressButton);
        Button clearButton = (Button) mRootView.findViewById(R.id.clearButton);
        addAddressButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        mMotorCycleImageView = (ImageView) mRootView.findViewById(R.id.motorCycleImageView);
        mMotorCycleImageViewRe = (ImageView) mRootView.findViewById(R.id.motorCycleImageViewRe);
        startMotorCycleAnimation();
    }

    private void startFetchAddressService()
    {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Params.INTENT_RECEIVER, mResultReceiver);
        intent.putExtra(Params.LOCATION_DATA, ContentManager.getInstance().getCurrentLocation());
        getActivity().startService(intent);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.addAddressButton:
                Utils.hideKeyboard(view, getActivity());

                if (!TextUtils.isEmpty(mCityEditText.getText().toString()) &&
                        !TextUtils.isEmpty(mStreetEditText.getText().toString()) &&
                        !TextUtils.isEmpty(mHouseNumberEditText.getText().toString()))
                {
                    BriskAddress address = new BriskAddress();
                    address.setCity(mCityEditText.getText().toString());
                    address.setStreet(mStreetEditText.getText().toString());
                    address.setHouseNumber(mHouseNumberEditText.getText().toString());
                    address.setFloorNumber(mFloorEditText.getText().toString());
                    address.setApartmentNumber(mApartmentEditText.getText().toString());
                    address.setNotes(mNotesEditText.getText().toString());

                    // Add address to Order!
                    ContentManager.getInstance().getOrderForUpload().setBriskAddress(address);
                    ContentManager.getInstance().setLastOrderAddress(address);
                    EventsManager.getInstance().post(new AddressAddedEvent());
                }
                else
                {
                    UIManager.getInstance().displaySnackBarError(view, getString(R.string.missing_fields_error), Snackbar.LENGTH_LONG);
                }

                break;
            case R.id.clearButton:
                mStreetEditText.setText("");
                mHouseNumberEditText.setText("");
                mFloorEditText.setText("");
                mApartmentEditText.setText("");
                mNotesEditText.setText("");
                ContentManager.getInstance().setLastOrderAddress(null);
                break;

        }
    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver
    {
        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            // Display the address string
            // or an error message sent from the intent service.
            if (resultCode == Params.SUCCESS_RESULT)
            {
                Address address = resultData.getParcelable(Params.ADDRESS_DATA);
                String cityName = address.getLocality();
                String streetName = address.getThoroughfare();
                String houseNumber = address.getFeatureName();

                // Check if to take the current location or load form the last one
//                BriskAddress lastOrderAddress = ContentManager.getInstance().getLastOrderAddress();
//                if(lastOrderAddress != null)
//                {
//                    mCityEditText.setText(lastOrderAddress.getCity());
//                    mStreetEditText.setText(lastOrderAddress.getStreet());
//                    mHouseNumberEditText.setText(lastOrderAddress.getHouseNumber());
//                    mFloorEditText.setText(lastOrderAddress.getFloorNumber());
//                    mApartmentEditText.setText(lastOrderAddress.getApartmentNumber());
//                    mNotesEditText.setText(lastOrderAddress.getNotes());
//                }
//                else
//                {
                    mCityEditText.setText(cityName);
                    mStreetEditText.setText(streetName);
                    mHouseNumberEditText.setText(houseNumber);
//                }

                Log.d(LOG_TAG, "Street: " + streetName + ". Building number: " + houseNumber + ". City: " + cityName);
            }
        }
    }

    public void startMotorCycleAnimation()
    {
        mMovingForwardAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, - 120,
                Animation.ABSOLUTE, Utils.getScreenWidth(getActivity()) + 240,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0
        );

        mMovingForwardAnimation.setDuration(8500);
        mMovingForwardAnimation.setStartOffset(2000);
        mMovingForwardAnimation.setFillAfter(true);
        mMovingForwardAnimation.setInterpolator(new AccelerateInterpolator());
        mMovingForwardAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mMotorCycleImageViewRe.startAnimation(mMovingBackwardAnimation);
                mMotorCycleImageViewRe.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        mMovingBackwardAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, 120,
                Animation.ABSOLUTE, - Utils.getScreenWidth(getActivity()) - 240,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0
        );

        mMovingBackwardAnimation.setDuration(8000);
        mMovingBackwardAnimation.setStartOffset(3000);
        mMovingBackwardAnimation.setFillAfter(true);
        mMovingBackwardAnimation.setInterpolator(new AccelerateInterpolator());
        mMovingBackwardAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mMotorCycleImageView.startAnimation(mMovingForwardAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        mMotorCycleImageView.startAnimation(mMovingForwardAnimation);
    }
}
