package co.thenets.brisk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.events.StoresRefreshedEvent;
import co.thenets.brisk.events.AddressAndCCAddedEvent;
import co.thenets.brisk.events.CartSelectedEvent;
import co.thenets.brisk.events.DisplayShoppingListEvent;
import co.thenets.brisk.fragments.SelectedCartFragment;
import co.thenets.brisk.fragments.ShoppingListFragment;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.models.Order;
import co.thenets.brisk.models.OrderForUpload;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateOrderRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestCreateOrderListener;
import retrofit.RetrofitError;

public class CartActivity extends BaseActivity
{
    private android.support.v4.app.FragmentTransaction mTransaction;
    private FrameLayout mLoadingLayout;
    public static final String SHOPPING_LIST_FRAGMENT = "shopping_list_fragment";
    public static final String COMPARE_CARTS_FRAGMENT = "compare_carts_fragment";
    public static final String SELECTED_CART_FRAGMENT = "selected_cart_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setToolbar();
        setViews(savedInstanceState);
        EventsManager.getInstance().register(this);
    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_shopping_bag));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    private void setViews(Bundle savedInstanceState)
    {
        setContentFragment(savedInstanceState);
        mLoadingLayout = (FrameLayout) findViewById(R.id.loadingLayout);
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
            ShoppingListFragment shoppingListFragment = new ShoppingListFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            shoppingListFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, shoppingListFragment).commitAllowingStateLoss();
        }
    }

    @Subscribe
    public void cartSelected(CartSelectedEvent cartSelectedEvent)
    {
        android.support.v4.app.FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        SelectedCartFragment selectedCartFragment = new SelectedCartFragment();
        transaction.add(R.id.fragmentContainer, selectedCartFragment);
        transaction.addToBackStack(COMPARE_CARTS_FRAGMENT);
        transaction.commitAllowingStateLoss();
    }

    @Subscribe
    public void showShoppingList(DisplayShoppingListEvent displayShoppingListEvent)
    {
        ShoppingListFragment cartFragment = new ShoppingListFragment();
        cartFragment.setArguments(getIntent().getExtras());
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, cartFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof ShoppingListFragment)
        {
            finish();
        }
        super.onBackPressed();
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
    public void onAddressAndCCAdded(AddressAndCCAddedEvent addressAndCCAddedEvent)
    {
        orderThisCart();
    }

    private void orderThisCart()
    {
        mLoadingLayout.setVisibility(View.VISIBLE);
        OrderForUpload orderForUpload = ContentManager.getInstance().getOrderForUpload();
        RestClientManager.getInstance().createOrder(new CreateOrderRequest(orderForUpload), new RequestCreateOrderListener()
        {
            @Override
            public void onSuccess(Order order)
            {
                Toast.makeText(getApplicationContext(), getString(R.string.order_completed), Toast.LENGTH_LONG);

                // Move to CustomerOrderActivity
                Intent intent = new Intent(getApplicationContext(), CustomerOrderActivity.class);
                intent.putExtra(Params.ORDER_ID, order.getID());
                startActivity(intent);

                // Finish CartActivity
                finish();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mLoadingLayout.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getString(R.string.order_failed), Toast.LENGTH_LONG);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mLoadingLayout.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getString(R.string.order_failed), Toast.LENGTH_LONG);
            }
        });
    }

    @Subscribe
    public void activeProductsRefreshed(StoresRefreshedEvent storesRefreshedEvent)
    {
        finish();
    }
}
