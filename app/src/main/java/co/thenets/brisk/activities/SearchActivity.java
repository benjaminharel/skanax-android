package co.thenets.brisk.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.SearchResultsRecyclerAdapter;
import co.thenets.brisk.custom.LinearLayoutManager;
import co.thenets.brisk.events.StoresRefreshedEvent;
import co.thenets.brisk.events.CartItemDeletedWithSwipeEvent;
import co.thenets.brisk.events.CartItemsRemovedEvent;
import co.thenets.brisk.events.CartUpdatedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.responses.GetStoresResponse;
import co.thenets.brisk.rest.service.RequestSearchListener;
import retrofit.RetrofitError;

public class SearchActivity extends BaseActivity
{
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private LinearLayout mProgressBarLayout;
    private Context mContext;
    private LinearLayout mDescriptionLayout;
    private TextView mNotFoundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        ContentManager.getInstance().clearCategoriesSelection();

        setViews();
        EventsManager.getInstance().register(this);
    }

    @Override
    protected void onResume()
    {
        EventsManager.getInstance().register(this);
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause()
    {
        EventsManager.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search_activity, menu);

        setCartView(menu);
        setSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setSearchView(Menu menu)
    {
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        if (searchMenuItem != null)
        {
            searchMenuItem.expandActionView();
            MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener()
            {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item)
                {
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item)
                {
                    finish();
                    return false;
                }
            });
        }

        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query)
            {
                runSearch();
                return false;
            }
        });
    }

    private void setViews()
    {
        setToolbar();
        setFab();
        setRecyclerView();
        mDescriptionLayout = (LinearLayout) findViewById(R.id.descriptionLayout);
        mNotFoundTextView = (TextView) findViewById(R.id.notFoundTextView);
        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);

        mRecyclerView.setVisibility(View.INVISIBLE);
        mDescriptionLayout.setVisibility(View.VISIBLE);
    }

    private void setRecyclerView()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                {
                    // Scrolling up
                    Utils.hideKeyboard(recyclerView, (Activity) mContext);
                }
                else
                {
                    // Scrolling down
                }
            }
        });
    }

    private void setFab()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void runSearch()
    {
        String searchQuery = (mSearchView == null) ? "" : mSearchView.getQuery().toString();
        if (!TextUtils.isEmpty(mSearchView.getQuery().toString()) && mSearchView.getQuery().toString().length() > Params.SEARCH_LENGTH_THRESHOLD)
        {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mProgressBarLayout.setVisibility(View.VISIBLE);
            mNotFoundTextView.setVisibility(View.INVISIBLE);

            RestClientManager.getInstance().searchStoreProduct(searchQuery, new RequestSearchListener()
            {
                @Override
                public void onSuccess(GetStoresResponse getStoresResponse)
                {

                    if (!getStoresResponse.getStoreList().isEmpty())
                    {
                        mProgressBarLayout.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mDescriptionLayout.setVisibility(View.INVISIBLE);
                        mNotFoundTextView.setVisibility(View.INVISIBLE);

                        SearchResultsRecyclerAdapter adapter = new SearchResultsRecyclerAdapter(mContext, getStoresResponse.getStoreList());
                        mRecyclerView.setAdapter(adapter);
                    }

                    else
                    {
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        mDescriptionLayout.setVisibility(View.VISIBLE);
                        mNotFoundTextView.setVisibility(View.VISIBLE);
                    }
                }


                @Override
                public void onInternalServerFailure(ErrorResponse error)
                {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mProgressBarLayout.setVisibility(View.INVISIBLE);
                    mDescriptionLayout.setVisibility(View.VISIBLE);
                    mNotFoundTextView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNetworkFailure(RetrofitError error)
                {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mProgressBarLayout.setVisibility(View.INVISIBLE);
                    mDescriptionLayout.setVisibility(View.VISIBLE);
                    mNotFoundTextView.setVisibility(View.VISIBLE);
                }
            });

            AnalyticsManager.getInstance().searchEvent(searchQuery);
        }
        else
        {
            mProgressBarLayout.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mDescriptionLayout.setVisibility(View.VISIBLE);
            mNotFoundTextView.setVisibility(View.INVISIBLE);
        }
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

    @Subscribe
    public void cartUpdated(CartUpdatedEvent cartUpdatedEvent)
    {
//        invalidateOptionsMenu();
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
        finish();
    }
}
