package co.thenets.brisk.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.events.AddressAddedEvent;
import co.thenets.brisk.fragments.AddAddressFragment;
import co.thenets.brisk.fragments.SetPaymentMethodFragment;
import co.thenets.brisk.managers.EventsManager;

public class GetAddressAndPaymentActivity extends BaseActivity
{
    private static final String LOG_TAG = GetAddressAndPaymentActivity.class.getSimpleName();
    private android.support.v4.app.FragmentTransaction mTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_and_payment);

        setToolbar();
        loadAddressFragment(savedInstanceState);
        EventsManager.getInstance().register(this);
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    private void loadAddressFragment(Bundle savedInstanceState)
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
            AddAddressFragment addAddressFragment = new AddAddressFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            addAddressFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, addAddressFragment).commitAllowingStateLoss();
        }
    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void addAddressButtonClicked(AddressAddedEvent addressAdded)
    {
        // Move Select Payment Method Fragment
        moveToSelectPaymentMethodFragment();
    }

    private void moveToSelectPaymentMethodFragment()
    {
        mTransaction = getSupportFragmentManager().beginTransaction();
        SetPaymentMethodFragment setPaymentMethodFragment = new SetPaymentMethodFragment();
        mTransaction.add(R.id.fragmentContainer, setPaymentMethodFragment);
        mTransaction.addToBackStack(AddAddressFragment.class.getSimpleName());
        mTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed()
    {
        setTitle(getString(R.string.set_delivery_address));
        super.onBackPressed();
    }
}
