package co.thenets.brisk.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.LinearLayoutManager;
import co.thenets.brisk.events.StoresRefreshedEvent;
import co.thenets.brisk.events.CartItemDeletedWithSwipeEvent;
import co.thenets.brisk.events.CartItemsRemovedEvent;
import co.thenets.brisk.events.CartUpdatedEvent;
import co.thenets.brisk.events.DisplayMainEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.ActiveCategory;

public class ActiveProductsActivity extends NoStatusBarActivity
{
    private RecyclerView mRecyclerView;
    private ActiveCategory mActiveCategory;
    private ActiveCategory mActiveSubCategory;
    private LinearLayout mProgressBarLayout;
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mLoadingMoreItemRightNow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        hideStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        mContext = this;
        mActiveCategory = ContentManager.getInstance().getSelectedActiveCategory();
        mActiveSubCategory = ContentManager.getInstance().getSelectedActiveSubCategory();
        setViews();
        loadData();

        EventsManager.getInstance().register(this);
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_with_cart, menu);
        setCartView(menu);
        return super.onCreateOptionsMenu(menu);
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

    private void setViews()
    {
        setToolbar();
        setRecyclerView();
        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mActiveSubCategory != null)
        {
            toolbar.setTitle(mActiveSubCategory.getName());
        }
        else
        {
            toolbar.setTitle(getString(R.string.recommended_products));
        }

        if (mActiveCategory != null)
        {
            toolbar.setBackgroundColor(mActiveCategory.getColor());
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setRecyclerView()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                loadingMoreItemsLogic(dy);
            }
        });
    }

    private void loadData()
    {
//        mProgressBarLayout.setVisibility(View.VISIBLE);
//        RestClientManager.getInstance().getActiveProducts("", false, new RequestListener()
//        {
//            @Override
//            public void onSuccess()
//            {
//                mProgressBarLayout.setVisibility(View.GONE);
//                StoreSearchResultsRecyclerAdapter searchResultsRecyclerAdapter = new StoreSearchResultsRecyclerAdapter(mContext);
//                mRecyclerView.setAdapter(searchResultsRecyclerAdapter);
//            }
//
//            @Override
//            public void onInternalServerFailure(ErrorResponse error)
//            {
//                mProgressBarLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onNetworkFailure(RetrofitError error)
//            {
//                mProgressBarLayout.setVisibility(View.GONE);
//            }
//        });
    }

    private void loadingMoreItemsLogic(int dy)
    {
        int pastVisibleItems, visibleItemCounter, totalItemCounter;
        // if scroll down
        if (dy > 0)
        {
            visibleItemCounter = mLinearLayoutManager.getChildCount();
            totalItemCounter = mLinearLayoutManager.getItemCount();
            pastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (!mLoadingMoreItemRightNow)
            {
                if ((visibleItemCounter + pastVisibleItems) >= totalItemCounter
                        && (totalItemCounter % Params.PAGINATION_LIMIT_NORMAL) == 0)
                {
                    mLoadingMoreItemRightNow = true;
                    // Do pagination!
                    loadMoreItems(totalItemCounter);
                }
            }
        }
    }

    private void loadMoreItems(int skipCounter)
    {
//        mProgressBarLayout.setVisibility(View.VISIBLE);
//        RestClientManager.getInstance().getMoreAvailableProducts("", skipCounter, new RequestMoreAvailableProductsListener()
//        {
//            @Override
//            public void onSuccess(GetAvailableProductsResponse getAvailableProductsResponse)
//            {
//                mProgressBarLayout.setVisibility(View.GONE);
//                ArrayList<ActiveProduct> moreAvailableProductList = getAvailableProductsResponse.getAvailableProductList();
//                ((StoreSearchResultsRecyclerAdapter) mRecyclerView.getAdapter()).addItems(moreAvailableProductList);
//                mLoadingMoreItemRightNow = false;
//            }
//
//            @Override
//            public void onInternalServerFailure(ErrorResponse error)
//            {
//                mProgressBarLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onNetworkFailure(RetrofitError error)
//            {
//                mProgressBarLayout.setVisibility(View.GONE);
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home)
        {
            EventsManager.getInstance().post(new DisplayMainEvent());
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

    // In case the app was in background more than REFRESH_STORES_TIMEOUT
    @Subscribe
    public void activeProductsRefreshed(StoresRefreshedEvent storesRefreshedEvent)
    {
        finish();
    }
}
