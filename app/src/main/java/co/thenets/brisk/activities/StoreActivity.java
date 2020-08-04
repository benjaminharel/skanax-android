package co.thenets.brisk.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.StoreProductsRecyclerAdapter;
import co.thenets.brisk.adapters.StoreSearchResultsRecyclerAdapter;
import co.thenets.brisk.custom.LinearLayoutManager;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.events.CartUpdatedEvent;
import co.thenets.brisk.events.DisplayShoppingListEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Store;
import co.thenets.brisk.models.StoreProduct;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.responses.GetStoreProductsResponse;
import co.thenets.brisk.rest.service.RequestGetStoreProductsListener;
import co.thenets.brisk.rest.service.RequestStoreProductsListener;
import retrofit.RetrofitError;

public class StoreActivity extends BaseActivity implements View.OnClickListener
{
    private SearchView mSearchView;
    private TextView mNameTextView;
    private TextView mEtaTextView;
    private TextView mDeliveryPriceTextView;
    private TextView mReviewsTextView;
    private RatingBar mRatingBar;
    private RecyclerView mRecyclerView;
    private Store mStore;
    private Activity mActivityContext;
    private ImageView mStoreImageView;
    private ImageButton mCallButton;
    private ImageButton mSmsButton;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mLoadingMoreItemRightNow = false;
    private LinearLayout mProgressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        mActivityContext = this;
        String storeID = getIntent().getStringExtra(Params.STORE_ID);
        mStore = ContentManager.getInstance().getStore(storeID);
        setViews();

        EventsManager.getInstance().register(this);
        AnalyticsManager.getInstance().storeScreen();
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
        menuInflater.inflate(R.menu.menu_store, menu);
        setCartView(menu);
        setSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setViews()
    {
        setToolbar();
        setRecyclerView();
        setOtherViews();
    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.store));
        setSupportActionBar(toolbar);
    }

    private void setRecyclerView()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                {
                    // Scrolling up
                    Utils.hideKeyboard(recyclerView, mActivityContext);
                    loadingMoreItemsLogic(dy);
                }
                else
                {
                    // Scrolling down
                }
            }
        });


        StoreProductsRecyclerAdapter adapter = new StoreProductsRecyclerAdapter(mActivityContext, mStore);
        mRecyclerView.setAdapter(adapter);
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
                if ((visibleItemCounter + pastVisibleItems) >= totalItemCounter && (totalItemCounter % Params.PAGINATION_LIMIT_SMALL) == 0)
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
        mProgressBarLayout.setVisibility(View.VISIBLE);
        RestClientManager.getInstance().getStoreProducts(mStore.getID(), "", skipCounter, new RequestStoreProductsListener()
        {
            @Override
            public void onSuccess(GetStoreProductsResponse getStoreProductsResponse)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                ArrayList<StoreProduct> storeProductList = getStoreProductsResponse.getStoreProductList();
                ((StoreProductsRecyclerAdapter) mRecyclerView.getAdapter()).addItems(storeProductList);
                mLoadingMoreItemRightNow = false;
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

    private void setOtherViews()
    {
        mStoreImageView = (ImageView) findViewById(R.id.imageView);
        mNameTextView = (TextView) findViewById(R.id.itemNameTextView);
        mEtaTextView = (TextView) findViewById(R.id.storeEtaTextView);
        mReviewsTextView = (TextView) findViewById(R.id.reviewsCounterTextView);
        mDeliveryPriceTextView = (TextView) findViewById(R.id.deliveryPriceEditText);
        mRatingBar = (RatingBar) findViewById(R.id.storeRatingBar);
        mCallButton = (ImageButton) findViewById(R.id.callButton);
        mSmsButton = (ImageButton) findViewById(R.id.smsButton);
        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);

        mCallButton.setOnClickListener(this);
        mSmsButton.setOnClickListener(this);

        UIManager.getInstance().loadImage(this, mStore.getPhotoGallery().getMedium(), mStoreImageView, ImageType.STORE);
        mNameTextView.setText(mStore.getName());
        mEtaTextView.setText(String.format(getString(R.string.avg_eta), mStore.getETA()));
        mDeliveryPriceTextView.setText(String.format(mActivityContext.getString(R.string.delivery_price_with_value), String.valueOf((int) mStore.getDeliveryPrice())));
        mRatingBar.setRating(mStore.getRating());
        setRatingReviews();
    }

    private void setRatingReviews()
    {
        if (mStore.getReviewsCounter() > 0)
        {
            mReviewsTextView.setText(String.format(getString(R.string.number_of_reviews), mStore.getReviewsCounter()));
        }
        else
        {
            mReviewsTextView.setVisibility(View.GONE);
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
                // Tell the listeners (The cart activity if it's open to move to shopping list)
                EventsManager.getInstance().post(new DisplayShoppingListEvent());
                Intent intent = new Intent(mActivityContext, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    @Subscribe
    public void cartUpdated(CartUpdatedEvent cartUpdatedEvent)
    {
        invalidateOptionsMenu();
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.callButton:
                Utils.openDialer(this, mStore.getPhone());
                break;
            case R.id.smsButton:
                Utils.sendSMS(this, mStore.getPhone(), "");
                break;
        }
    }

    public void setSearchView(Menu menu)
    {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
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
                refreshItems(query);
                return false;
            }
        });
    }

    private void refreshItems(String query)
    {
        if (query.length() > Params.SEARCH_LENGTH_THRESHOLD)
        {
            mProgressBarLayout.setVisibility(View.VISIBLE);
            RestClientManager.getInstance().getStoreProducts(mStore.getID(), query, new RequestGetStoreProductsListener()
            {
                @Override
                public void onSuccess(GetStoreProductsResponse getStoreProductsResponse)
                {
                    mProgressBarLayout.setVisibility(View.INVISIBLE);
                    StoreSearchResultsRecyclerAdapter adapter = new StoreSearchResultsRecyclerAdapter(mActivityContext, mStore.getID(), getStoreProductsResponse.getStoreProductList());
                    mRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onInternalServerFailure(ErrorResponse error)
                {
                    mProgressBarLayout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onNetworkFailure(RetrofitError error)
                {
                    mProgressBarLayout.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}
