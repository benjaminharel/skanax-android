package co.thenets.brisk.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.enums.AcceptedPaymentType;
import co.thenets.brisk.fragments.InventoryFragment;
import co.thenets.brisk.fragments.StoreAvailabilityFragment;
import co.thenets.brisk.fragments.StoreDetailsFragment;
import co.thenets.brisk.fragments.StoreGeoDataFragment;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Store;
import co.thenets.brisk.models.StoreBasicDetails;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateOrUpdateStoreRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

public class BecomeBriskerActivity extends BaseActivity
{
    private android.support.v4.app.FragmentTransaction mTransaction;
    private EditText mStoreNameEditText;
    private CheckBox mCashCheckBox;
    private CheckBox mBriskCheckBox;
    ArrayList<AcceptedPaymentType> mAcceptedPaymentTypeList = new ArrayList<>();
    private EditText mDeliveryPriceEditText;
    private Context mContext;
    private FloatingActionButton mFab;
    private LinearLayout mProgressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_brisker);

        mContext = this;
        setContent(savedInstanceState);
        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
    }

    public void fabClick(View view)
    {
        mTransaction = getSupportFragmentManager().beginTransaction();

        switch (view.getId())
        {
            case R.id.store_details_fab:
                if (basicDetailsValid())
                {
                    saveBasicDetails();
                    UIManager.getInstance().hideKeyboard(mStoreNameEditText, this);
                    UIManager.getInstance().hideKeyboard(mDeliveryPriceEditText, this);
                    StoreGeoDataFragment storeGeoDataFragment = new StoreGeoDataFragment();
                    mTransaction.add(R.id.fragmentContainer, storeGeoDataFragment);
                    mTransaction.addToBackStack(null);
                    mTransaction.commit();
                }
                break;
            case R.id.store_geo_data_fab:
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                if (fragment instanceof StoreGeoDataFragment)
                {
                    boolean geoDataReady = ((StoreGeoDataFragment) fragment).isGeoDataReady();
                    if (geoDataReady)
                    {
                        StoreAvailabilityFragment setStoreAvailabilityFragment = new StoreAvailabilityFragment();
                        mTransaction.add(R.id.fragmentContainer, setStoreAvailabilityFragment);
                        mTransaction.addToBackStack(null);
                        mTransaction.commit();
                    }
                    else
                    {
                        UIManager.getInstance().displaySnackBarError(view, getString(R.string.empty_store_geo_location), Snackbar.LENGTH_LONG);
                    }
                }
                break;
            case R.id.store_availability_fab:
                // Create a store
                createStoreOnServer();
                break;
            case R.id.inventory_fab:
                Intent intent = new Intent(this, BarcodeActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void createStoreOnServer()
    {
        // Get the combined store details
        Store storeForUpload = ContentManager.getInstance().getStoreForUpload();
        mProgressBarLayout.setVisibility(View.VISIBLE);
        RestClientManager.getInstance().createStore(new CreateOrUpdateStoreRequest(storeForUpload), new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                Intent intent = new Intent(getApplicationContext(), AddNewProductActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                UIManager.getInstance().displaySnackBarError(mProgressBarLayout, getString(R.string.error_in_creating_store), Snackbar.LENGTH_LONG);
                mProgressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                UIManager.getInstance().displaySnackBarError(mProgressBarLayout, getString(R.string.error_in_creating_store), Snackbar.LENGTH_LONG);
                mProgressBarLayout.setVisibility(View.GONE);
            }
        });
    }

    private void saveBasicDetails()
    {
        String storeName = mStoreNameEditText.getText().toString();
        float deliveryPrice = Float.parseFloat(mDeliveryPriceEditText.getText().toString());
        if(mCashCheckBox.isChecked())
        {
            mAcceptedPaymentTypeList.add(AcceptedPaymentType.CASH);
        }
        if(mBriskCheckBox.isChecked())
        {
            mAcceptedPaymentTypeList.add(AcceptedPaymentType.BRISK_PAYMENT);
        }

        ContentManager.getInstance().setStoreBasicDetails(new StoreBasicDetails(storeName, mAcceptedPaymentTypeList, deliveryPrice));
    }

    private boolean basicDetailsValid()
    {
        mFab = (FloatingActionButton) findViewById(R.id.store_details_fab);
        mStoreNameEditText = (EditText) findViewById(R.id.storeNameEditText);
        mCashCheckBox = (CheckBox) findViewById(R.id.cashCheckBox);
        mBriskCheckBox = (CheckBox) findViewById(R.id.briskCheckBox);
        mDeliveryPriceEditText = (EditText) findViewById(R.id.deliveryPriceEditText);

        if (TextUtils.isEmpty(mStoreNameEditText.getText().toString()))
        {
            UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.empty_store_name_error), Snackbar.LENGTH_LONG);
            return false;
        }
        else if (!mBriskCheckBox.isChecked() && !mCashCheckBox.isChecked())
        {
            UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.select_preferred_payment_method), Snackbar.LENGTH_LONG);
            return false;
        }
        else if (TextUtils.isEmpty(mDeliveryPriceEditText.getText().toString()))
        {
            UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.empty_store_delivery_price_error), Snackbar.LENGTH_LONG);
            return false;
        }

        return true;
    }

    private void setContent(Bundle savedInstanceState)
    {
        setContentFragment(savedInstanceState);
    }

    private void setContentFragment(Bundle savedInstanceState)
    {
        // Check that the activity is using the layout version with the fragment_container FrameLayout
        if (findViewById(R.id.fragmentContainer) != null)
        {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null)
            {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            StoreDetailsFragment setStoreProfileFragment = new StoreDetailsFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            setStoreProfileFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, setStoreProfileFragment).commit();
        }
    }

    @Override
    public void onBackPressed()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (!(fragment instanceof InventoryFragment))
        {
            if (getFragmentManager().getBackStackEntryCount() > 0)
            {
                getFragmentManager().popBackStack();
            }
            else
            {
                super.onBackPressed();
            }
        }
    }
}
