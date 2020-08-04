package co.thenets.brisk.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.StoresAdapter;
import co.thenets.brisk.custom.LinearLayoutManager;
import co.thenets.brisk.dialogs.CustomerServiceDialog;
import co.thenets.brisk.dialogs.RateUsDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.UiState;
import co.thenets.brisk.events.StoresRefreshedEvent;
import co.thenets.brisk.events.CartItemDeletedWithSwipeEvent;
import co.thenets.brisk.events.CartItemsRemovedEvent;
import co.thenets.brisk.events.CartUpdatedEvent;
import co.thenets.brisk.events.CustomerProfileDetailEvent;
import co.thenets.brisk.events.LocationPermissionDenialEvent;
import co.thenets.brisk.events.SignOutProfileDetailEvent;
import co.thenets.brisk.events.StoreUpdatedEvent;
import co.thenets.brisk.events.SwitchStateEvent;
import co.thenets.brisk.fragments.AppInfoFragment;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.NavigationActivity;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Store;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CustomerMainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NavigationActivity, View.OnClickListener
{
    private Context mContext;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private static final String LOG_TAG = CustomerMainActivity.class.getSimpleName();
    private ImageView mHeaderImageView;
    private TextView mNameTextView;
    private Button mHeaderActionButton;

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private LinearLayout mProgressBarLayout;
    private LinearLayout mRecommendedLinearLayout;
    private LinearLayout mNoContentLayout;
    private Button mOpenAStoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        mContext = this;
        UIManager.getInstance().setActivityContext(this);

        setViews();
        loadContent();
        EventsManager.getInstance().register(this);
        AnalyticsManager.getInstance().setScreen(CustomerMainActivity.class.getSimpleName());
        handlePushIfNeeded();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        UIManager.getInstance().setActivityContext(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_customer_main, menu);
        setCartView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_search:
                openSearchActivity();
                break;
            case R.id.action_cart:
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCartView(Menu menu)
    {
        MenuItem item = menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(item, R.layout.cart_with_counter_layout);
        FrameLayout itemCounterFrameLayout = (FrameLayout) MenuItemCompat.getActionView(item);

        FrameLayout badgeFrame = (FrameLayout) itemCounterFrameLayout.findViewById(R.id.badgeFrame);
        UIManager.getInstance().applyBounceAnimation(badgeFrame);

        TextView itemsCounterTextView = (TextView) itemCounterFrameLayout.findViewById(R.id.cartItemsCounter);
        int cartItemsCounter = ContentManager.getInstance().getCartItemsCounter();
        if (cartItemsCounter == 0)
        {
            itemsCounterTextView.setVisibility(View.GONE);
        }
        else
        {
            itemsCounterTextView.setVisibility(View.VISIBLE);
            itemsCounterTextView.setText(String.valueOf(cartItemsCounter));
        }

        itemCounterFrameLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handlePushIfNeeded()
    {
        String orderID = getIntent().getStringExtra(Params.ORDER_ID);
        if (!TextUtils.isEmpty(orderID))
        {
            Intent intent = new Intent(this, CustomerOrderActivity.class);
            intent.putExtra(Params.ORDER_ID, orderID);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        ContentManager.getInstance().clearCart("");
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        // Used for the Calligraphy git
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setViews()
    {
        setDrawer();
        setDrawerHeader();
        setToolBar();
        setOtherViews();
    }

    private void setToolBar()
    {
        mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);
        setupNavigationDrawer(mToolbar);
    }

    private void setOtherViews()
    {
        mNoContentLayout = (LinearLayout) findViewById(R.id.noContentLayout);
        mOpenAStoreButton = (Button) findViewById(R.id.openAStoreButton);
        mNoContentLayout.setOnClickListener(this);
        mOpenAStoreButton.setOnClickListener(this);

        mRecommendedLinearLayout = (LinearLayout) findViewById(R.id.recommendedLinearLayout);
        mRecommendedLinearLayout.setOnClickListener(this);

        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(false);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setDrawerHeader()
    {
        View headerView = getLayoutInflater().inflate(R.layout.nav_header, mNavigationView, false);
        mNavigationView.addHeaderView(headerView);
        mHeaderActionButton = (Button) headerView.findViewById(R.id.headerActionButton);
        mHeaderImageView = (ImageView) headerView.findViewById(R.id.headerImageView);
        mNameTextView = (TextView) headerView.findViewById(R.id.itemNameTextView);

        setHeaderItemsVisibility();

        mHeaderActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ContentManager.getInstance().isUserCreated())
                {
                    if (!ContentManager.getInstance().isUserRegistered())
                    {
                        Intent mIntent = new Intent(mContext, RegisterActivity.class);
                        startActivity(mIntent);
                    }
                    else
                    {
                        if (!ContentManager.getInstance().isUserCompletelyRegistered())
                        {
                            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    private void setHeaderItemsVisibility()
    {
        if (ContentManager.getInstance().isUserRegistered())
        {
            if (ContentManager.getInstance().isUserCompletelyRegistered())
            {
                mNameTextView.setText(ContentManager.getInstance().getUser().getFullName());
                if(ContentManager.getInstance().getUser().getPhotoGallery() != null)
                {
                    UIManager.getInstance().loadImage(mContext, ContentManager.getInstance().getUser().getPhotoGallery().getMedium(), mHeaderImageView, ImageType.GENERAL);
                }

                mHeaderActionButton.setVisibility(View.GONE);
                mNameTextView.setVisibility(View.VISIBLE);
                mHeaderImageView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                        startActivity(intent);
                    }
                });
            }
            else
            {
                mHeaderActionButton.setText(getString(R.string.complete_registration));
            }

        }
        else
        {
            mHeaderActionButton.setText(getString(R.string.register));
        }
    }

    private void setDrawer()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        if (ContentManager.getInstance().isSeller())
        {
            MenuItem itemAction = mNavigationView.getMenu().findItem(R.id.nav_customer_action);
            itemAction.setTitle(getString(R.string.switch_to_store));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        Intent intent = null;
        ActivityOptionsCompat activityOptionsCompat = null;
        switch (item.getItemId())
        {
            case R.id.nav_stores:
                // Do nothing
                item.setChecked(true);
                break;
            case R.id.nav_orders:
                intent = new Intent(this, OrdersActivity.class);
                activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext,
                        new Pair<View, String>(mToolbar, mContext.getString(R.string.tr_toolbar)));
                ActivityCompat.startActivity((Activity) mContext, intent, activityOptionsCompat.toBundle());
                break;
            case R.id.nav_share_us:
                Utils.shareMessage(this, getString(R.string.check_out_brisk) + Params.APP_DOWNLOAD_LINK);
                break;
            case R.id.nav_rate_us:
                RateUsDialog rateUsDialog = new RateUsDialog();
                rateUsDialog.show(getFragmentManager(), RateUsDialog.class.getSimpleName());
                break;
            case R.id.nav_info:
                intent = new Intent(this, AppInfoActivity.class);
                activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext,
                        new Pair<View, String>(mToolbar, mContext.getString(R.string.tr_toolbar)));
                ActivityCompat.startActivity((Activity) mContext, intent, activityOptionsCompat.toBundle());
                break;
            case R.id.nav_customer_action:
                openAStoreOrNavigateToStore();
                break;
            case R.id.nav_customer_service:
                CustomerServiceDialog customerServiceDialog = CustomerServiceDialog.newInstance();
                customerServiceDialog.show(getSupportFragmentManager(), CustomerServiceDialog.class.getSimpleName());
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openAStoreOrNavigateToStore()
    {
        if (ContentManager.getInstance().isSeller())
        {
            UIManager.getInstance().moveToState(this, UiState.STORE);
        }
        else
        {
            startActivity(new Intent(this, StoreIntroActivity.class));
        }
    }

    @Override
    public void onBackPressed()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof AppInfoFragment)
        {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            else
            {
                super.onBackPressed();
            }
        }
        else
        {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            else
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


    @Subscribe
    public void moveToStore(SwitchStateEvent switchStateEvent)
    {
        UIManager.getInstance().moveToState(this, UiState.STORE);
    }

    @Subscribe
    public void locationPermissionDenial(LocationPermissionDenialEvent locationPermissionDenialEvent)
    {
        UIManager.getInstance().displaySnackBarError(mToolbar, getString(R.string.cant_open_a_store_without_location_permission_error), Snackbar.LENGTH_LONG);
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

    @Subscribe
    public void handleCustomerProfileDetailEvent(CustomerProfileDetailEvent customerProfileDetailEvent)
    {
        setHeaderItemsVisibility();
    }

    @Subscribe
    public void handleSignOutProfileDetailEvent(SignOutProfileDetailEvent signOutProfileDetailEvent)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void onGettingStoreObject(StoreUpdatedEvent storeUpdatedEvent)
    {
        setDrawer();
    }

    private void replaceFragmentContainer(Fragment fragment)
    {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped)
        {
            //fragment not in back stack, create it.
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.recommendedLinearLayout:
                displayActiveProducts();
                AnalyticsManager.getInstance().recommendedPressed();
                break;
            case R.id.openAStoreButton:
                openAStoreOrNavigateToStore();
                break;
        }
    }

    private void displayActiveProducts()
    {
        // Save the selected sub category to ContentManager
        ContentManager.getInstance().clearCategoriesSelection();

        Intent intent = new Intent(this, ActiveProductsActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<View, String>(mRecommendedLinearLayout, getString(R.string.tr_category)));
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    private void openSearchActivity()
    {
        Intent intent = new Intent(this, SearchActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<View, String>(mToolbar, this.getString(R.string.tr_toolbar)));
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    private void loadContent()
    {
        ArrayList<Store> storeList = ContentManager.getInstance().getStoreList();
        if(!storeList.isEmpty())
        {
            mNoContentLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            StoresAdapter storesAdapter = new StoresAdapter(mContext);
            mRecyclerView.setAdapter(storesAdapter);
        }
        else
        {
            mNoContentLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void cartUpdated(CartUpdatedEvent cartUpdatedEvent)
    {
        invalidateOptionsMenu();
    }

    @Subscribe
    public void cartUpdated(CartItemDeletedWithSwipeEvent cartItemDeletedWithSwipeEvent)
    {
        invalidateOptionsMenu();
    }

    @Subscribe
    public void cartUpdated(CartItemsRemovedEvent cartItemsRemovedEvent)
    {
        invalidateOptionsMenu();
    }

    @Subscribe
    public void activeProductsRefreshed(StoresRefreshedEvent storesRefreshedEvent)
    {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}
