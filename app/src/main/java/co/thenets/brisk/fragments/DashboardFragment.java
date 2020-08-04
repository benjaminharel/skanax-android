package co.thenets.brisk.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.StoreMainActivity;
import co.thenets.brisk.dialogs.DeliveryPriceDialog;
import co.thenets.brisk.enums.FragmentType;
import co.thenets.brisk.events.FragmentResumedEvent;
import co.thenets.brisk.events.StoreUpdatedEvent;
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
 * A placeholder fragment containing a simple view.
 */
public class DashboardFragment extends BasicFragment implements View.OnClickListener
{
    private static final String LOG_TAG = DashboardFragment.class.getSimpleName();
    private LinearLayout mProgressBarLayout;
    private TextView mStoreStateTextView;
    private Switch mStoreActiveNotActiveSwitch;
    private Dashboard mDashboard;
    private RatingBar mRatingBar;
    private TextView mRatingCounter;
    private TextView mDailyIncomeTextView;
    private TextView mMonthlyIncomeTextView;
    private TextView mNewOrdersTextView;
    private TextView mInProgressOrdersTextView;
    private TextView mFinishedOrdersTextView;
    private TextView mTotalOrdersTextView;
    private TextView mDeliveryPriceTextView;
    private TextView mWorkingRadiusTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
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
    public void onResume()
    {
        super.onResume();
        setContent();
        EventsManager.getInstance().register(this);
        EventsManager.getInstance().post(new FragmentResumedEvent(FragmentType.DASHBOARD));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_store_dashboard, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
    }

    private void setContent()
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        RestClientManager.getInstance().getDashboard(new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                refreshViews();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
            }
        });
    }

    private void refreshViews()
    {
        try
        {
            mDashboard = ContentManager.getInstance().getDashboard();
            setActiveNoActiveSwitch();
            setIncomeViews();
            setOrdersViews();
            mDeliveryPriceTextView.setText(String.format(getString(R.string.price_value), (int) mDashboard.getDeliveryPrice()));
            mWorkingRadiusTextView.setText(String.format(getString(R.string.radius_distance), String.valueOf((float) mDashboard.getRadius() / 1000)));
            setRatingViews();
            mProgressBarLayout.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {

        }
    }

    private void setIncomeViews()
    {
        mDailyIncomeTextView.setText(String.format(getString(R.string.price_value), mDashboard.getDailyIncome()));
        mMonthlyIncomeTextView.setText(String.format(getString(R.string.price_value), mDashboard.getMonthlyIncome()));
    }

    private void setOrdersViews()
    {
        mNewOrdersTextView.setText(String.valueOf(mDashboard.getNewOrdersCounter()));
        mInProgressOrdersTextView.setText(String.valueOf(mDashboard.getInProcessOrdersCounter()));
        mFinishedOrdersTextView.setText(String.valueOf(mDashboard.getFinishedOrderCounter()));
        mTotalOrdersTextView.setText(String.valueOf(mDashboard.getAllOrdersCounter()));
    }

    private void setRatingViews()
    {
        mRatingBar.setRating(mDashboard.getRatingValue());
        mRatingCounter.setText(String.format(getString(R.string.number_of_reviews), mDashboard.getRatingCounter()));
    }

    private void setActiveNoActiveSwitch()
    {
        if (mDashboard.isStoreActive())
        {
            mStoreActiveNotActiveSwitch.setOnCheckedChangeListener(null);
            mStoreActiveNotActiveSwitch.setChecked(true);
            mStoreActiveNotActiveSwitch.setOnCheckedChangeListener(mOnSwitchCheckedChangeListener);
            mStoreStateTextView.setText(getString(R.string.store_open));
        }
        else
        {
            mStoreActiveNotActiveSwitch.setOnCheckedChangeListener(null);
            mStoreActiveNotActiveSwitch.setChecked(false);
            mStoreActiveNotActiveSwitch.setOnCheckedChangeListener(mOnSwitchCheckedChangeListener);
            mStoreStateTextView.setText(getString(R.string.store_closed));
        }
    }

    protected void setViews()
    {
        setToolbar();
        setOtherViews();
    }

    private void setOtherViews()
    {
        mProgressBarLayout = (LinearLayout) mRootView.findViewById(R.id.progressBarLayout);
        mStoreStateTextView = (TextView) mRootView.findViewById(R.id.storeStateTextView);
        mStoreActiveNotActiveSwitch = (Switch) mRootView.findViewById(R.id.storeActiveNotActiveSwitch);

        mRatingBar = (RatingBar) mRootView.findViewById(R.id.ratingBar);
        mRatingCounter = (TextView) mRootView.findViewById(R.id.reviewsCounterTextView);

        mDailyIncomeTextView = (TextView) mRootView.findViewById(R.id.dailyIncomeTextView);
        mMonthlyIncomeTextView = (TextView) mRootView.findViewById(R.id.monthlyIncomeTextView);

        mNewOrdersTextView = (TextView) mRootView.findViewById(R.id.newOrdersTextView);
        mInProgressOrdersTextView = (TextView) mRootView.findViewById(R.id.inProgressOrdersTextView);
        mFinishedOrdersTextView = (TextView) mRootView.findViewById(R.id.finishedOrdersTextView);
        mTotalOrdersTextView = (TextView) mRootView.findViewById(R.id.totalOrdersTextView);
        mDeliveryPriceTextView = (TextView) mRootView.findViewById(R.id.deliveryPriceTextView);
        mWorkingRadiusTextView = (TextView) mRootView.findViewById(R.id.workingRadiusTextView);

        LinearLayout workingRadiusLayout = (LinearLayout) mRootView.findViewById(R.id.workingRadiusLayout);
        LinearLayout deliveryPriceLayout = (LinearLayout) mRootView.findViewById(R.id.deliveryPriceLayout);
        LinearLayout orderLayout = (LinearLayout) mRootView.findViewById(R.id.orderLayout);
        workingRadiusLayout.setOnClickListener(this);
        deliveryPriceLayout.setOnClickListener(this);
        orderLayout.setOnClickListener(this);
    }

    private void setToolbar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.dashboard));
        if (getActivity() instanceof AppCompatActivity)
        {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.workingRadiusLayout:
                ((StoreMainActivity) getActivity()).moveToSetWorkingAreaFragment();
                break;
            case R.id.deliveryPriceLayout:
                DeliveryPriceDialog deliveryPriceDialog = DeliveryPriceDialog.newInstance();
                deliveryPriceDialog.show(getActivity().getFragmentManager(), DeliveryPriceDialog.class.getSimpleName());
                break;
            case R.id.orderLayout:
                ((StoreMainActivity) getActivity()).moveToOrderFragment();
                break;
        }
    }

    @Subscribe
    public void onStoreUpdated(StoreUpdatedEvent storeUpdatedEvent)
    {
        setContent();
    }

    CompoundButton.OnCheckedChangeListener mOnSwitchCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked)
        {
            Store store = ContentManager.getInstance().getStore();
            store.setIsActive(isChecked);

            // Update UI elements
            if (isChecked)
            {
                mStoreStateTextView.setText(getString(R.string.store_open));
            }
            else
            {
                mStoreStateTextView.setText(getString(R.string.store_closed));
            }

            // Update server
            RestClientManager.getInstance().updateStore(new CreateOrUpdateStoreRequest(store), new RequestListener()
            {
                @Override
                public void onSuccess()
                {
                }

                @Override
                public void onInternalServerFailure(ErrorResponse error)
                {
                    UIManager.getInstance().displaySnackBarError(mStoreActiveNotActiveSwitch, getString(R.string.error_in_update_store_details), Snackbar.LENGTH_SHORT);
                    mStoreActiveNotActiveSwitch.setChecked(!isChecked);
                }

                @Override
                public void onNetworkFailure(RetrofitError error)
                {
                    UIManager.getInstance().displaySnackBarError(mStoreActiveNotActiveSwitch, getString(R.string.error_in_update_store_details), Snackbar.LENGTH_SHORT);
                    mStoreActiveNotActiveSwitch.setChecked(!isChecked);
                }
            });
        }
    };
}
