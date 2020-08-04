package co.thenets.brisk.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.dialogs.CustomerServiceDialog;
import co.thenets.brisk.enums.FragmentType;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.UiState;
import co.thenets.brisk.events.FragmentResumedEvent;
import co.thenets.brisk.events.StoreUpdatedEvent;
import co.thenets.brisk.fragments.AppInfoFragment;
import co.thenets.brisk.fragments.DashboardFragment;
import co.thenets.brisk.fragments.MyInventoryFragment;
import co.thenets.brisk.fragments.OrdersFragment;
import co.thenets.brisk.fragments.StoreAvailabilityFragment;
import co.thenets.brisk.fragments.StoreGeoDataFragment;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.interfaces.NavigationActivity;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StoreMainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationActivity
{
    private android.support.v4.app.FragmentTransaction mTransaction;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private Context mContext;
    private ImageView mHeaderImageView;
    private TextView mStoreNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_main);
        mContext = this;
        setDrawer();
        setHeader();
        loadDashboard();
        getDataFromServer();
        openOrderActivityIfNeeded();
        EventsManager.getInstance().register(this);
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    private void openOrderActivityIfNeeded()
    {
        String orderID = getIntent().getStringExtra(Params.ORDER_ID);
        if (!TextUtils.isEmpty(orderID))
        {
            Intent intent = new Intent(this, StoreOrderActivity.class);
            intent.putExtra(Params.ORDER_ID, orderID);
            startActivity(intent);
        }
    }

    private void setDrawer()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    private void setHeader()
    {
        View headerView = getLayoutInflater().inflate(R.layout.nav_header, mNavigationView, false);
        mNavigationView.addHeaderView(headerView);

        Button headerActionButton = (Button) headerView.findViewById(R.id.headerActionButton);
        headerActionButton.setVisibility(View.GONE);

        mHeaderImageView = (ImageView) headerView.findViewById(R.id.headerImageView);
        if(ContentManager.getInstance().getStore() != null)
        {
            UIManager.getInstance().loadImage(this, ContentManager.getInstance().getStore().getPhotoGallery().getMedium(), mHeaderImageView, ImageType.STORE);
        }

        mStoreNameTextView = (TextView) headerView.findViewById(R.id.itemNameTextView);
        mStoreNameTextView.setVisibility(View.VISIBLE);
        if(ContentManager.getInstance().getStore() != null)
        {
            mStoreNameTextView.setText(ContentManager.getInstance().getStore().getName());
        }

        headerView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(mContext, StoreProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        // Used for the Calligraphy git
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void setupNavigationDrawer(Toolbar toolbar)
    {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        mTransaction = getSupportFragmentManager().beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_seller_dashboard)
        {
            loadDashboard();
        }
        else if (id == R.id.nav_orders)
        {
            // Load orders fragment
            OrdersFragment ordersFragment = new OrdersFragment();
            mTransaction.replace(R.id.fragmentContainer, ordersFragment);
            mTransaction.addToBackStack(null);
            mTransaction.commit();
            item.setChecked(true);
        }
        else if (id == R.id.nav_inventory)
        {
            MyInventoryFragment myInventoryFragment = new MyInventoryFragment();
            mTransaction.replace(R.id.fragmentContainer, myInventoryFragment);
            mTransaction.addToBackStack(null);
            mTransaction.commit();
            item.setChecked(true);
        }
        else if (id == R.id.nav_working_radius)
        {
            StoreGeoDataFragment storeGeoDataFragment = new StoreGeoDataFragment();
            mTransaction.replace(R.id.fragmentContainer, storeGeoDataFragment);
            mTransaction.addToBackStack(null);
            mTransaction.commit();
            item.setChecked(true);
        }
        else if (id == R.id.nav_working_hours)
        {
            StoreAvailabilityFragment storeAvailabilityFragment = new StoreAvailabilityFragment();
            mTransaction.replace(R.id.fragmentContainer, storeAvailabilityFragment);
            mTransaction.addToBackStack(null);
            mTransaction.commit();
            item.setChecked(true);
        }

        else if (id == R.id.nav_info)
        {
            // Load orders fragment
            AppInfoFragment appInfoFragment = new AppInfoFragment();
            mTransaction = getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            mTransaction.replace(R.id.fragmentContainer, appInfoFragment);
            mTransaction.addToBackStack(null);
            // Commit the transaction
            mTransaction.commit();

            setInfoNavigationItemSelected(item);

        }

        else if (id == R.id.nav_customer_service)
        {
            CustomerServiceDialog customerServiceDialog = CustomerServiceDialog.newInstance();
            customerServiceDialog.show(getSupportFragmentManager(), CustomerServiceDialog.class.getSimpleName());
        }
        else if (id == R.id.nav_switch_to_customer)
        {
            UIManager.getInstance().moveToState(this, UiState.CUSTOMER);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setInfoNavigationItemSelected(MenuItem item)
    {
        item.setChecked(true);
        mNavigationView.getMenu().getItem(FragmentType.DASHBOARD.getValue()).setChecked(false);
        mNavigationView.getMenu().getItem(FragmentType.ORDERS.getValue()).setChecked(false);
        mNavigationView.getMenu().getItem(FragmentType.INVENTORY.getValue()).setChecked(false);
        mNavigationView.getMenu().getItem(FragmentType.WORKING_HOURS.getValue()).setChecked(false);
        mNavigationView.getMenu().getItem(FragmentType.WORKING_RADIUS.getValue()).setChecked(false);
    }

    private void loadDashboard()
    {
        mTransaction = getSupportFragmentManager().beginTransaction();
        DashboardFragment dashboardFragment = new DashboardFragment();
        mTransaction.replace(R.id.fragmentContainer, dashboardFragment);
        mTransaction.commit();
        mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    public void fabClick(View view)
    {
        switch (view.getId())
        {
            case R.id.my_inventory_fab:
                // TODO: Uncomment this!
//                mTransaction = getSupportFragmentManager().beginTransaction();
//                InventoryFragment inventoryFragment = new InventoryFragment();
//                mTransaction.replace(R.id.fragmentContainer, inventoryFragment);
//                mTransaction.addToBackStack(null);
//                mTransaction.commit();
                Intent intent2 = new Intent(this, AddNewProductActivity.class);
                startActivity(intent2);
                break;
            case R.id.inventory_fab:
                Intent intent = new Intent(this, BarcodeActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void getDataFromServer()
    {
        getCategories();
    }

    public void getCategories()
    {
        RestClientManager.getInstance().getCategories(new RequestListener()
        {
            @Override
            public void onSuccess()
            {

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

    public void moveToSetWorkingAreaFragment()
    {
        mTransaction = getSupportFragmentManager().beginTransaction();
        StoreGeoDataFragment storeGeoDataFragment = new StoreGeoDataFragment();
        mTransaction.replace(R.id.fragmentContainer, storeGeoDataFragment);
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }

    public void moveToOrderFragment()
    {
        mTransaction = getSupportFragmentManager().beginTransaction();
        OrdersFragment ordersFragment = new OrdersFragment();
        mTransaction.replace(R.id.fragmentContainer, ordersFragment);
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }

    @Subscribe
    public void onStoreObjectUpdated(StoreUpdatedEvent storeUpdatedEvent)
    {
        // Refresh header image
        UIManager.getInstance().loadImage(this, ContentManager.getInstance().getStore().getPhotoGallery().getSmall(), mHeaderImageView, ImageType.STORE);
        // Refresh header store name
        mStoreNameTextView.setText(ContentManager.getInstance().getStore().getName());

        if(storeUpdatedEvent.isForceStoreRebuild())
        {
            finish();
            Intent intent = new Intent(this, StoreMainActivity.class);
            startActivity(intent);
        }
    }

    @Subscribe
    public void onFragmentResumed(FragmentResumedEvent fragmentResumedEvent)
    {
        if (fragmentResumedEvent.getFragmentType() == FragmentType.APP_INFO)
        {
            mNavigationView.getMenu().getItem(fragmentResumedEvent.getFragmentType().getValue()).setChecked(true);
        }
        else
        {
            mNavigationView.getMenu().getItem(FragmentType.APP_INFO.getValue()).setChecked(false);
            mNavigationView.getMenu().getItem(fragmentResumedEvent.getFragmentType().getValue()).setChecked(true);
        }
    }
}
