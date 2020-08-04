package co.thenets.brisk.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.SubCategoriesRecyclerAdapter;
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

public class SubcategoriesActivity extends NoStatusBarActivity
{
    private RecyclerView mRecyclerView;
    private ActiveCategory mActiveCategory;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        hideStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategories);
        mContext = this;

        mActiveCategory = (ActiveCategory) getIntent().getSerializableExtra(Params.ACTIVE_CATEGORY);

        setViews();
        EventsManager.getInstance().register(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventsManager.getInstance().unregister(this);
    }

    private void setViews()
    {
        setToolbar();
        setRecyclerView();
    }

    private void setRecyclerView()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        SubCategoriesRecyclerAdapter subCategoriesRecyclerAdapter = new SubCategoriesRecyclerAdapter(this, mActiveCategory.getSubCategories());
        mRecyclerView.setAdapter(subCategoriesRecyclerAdapter);
    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mActiveCategory.getName());
        toolbar.setBackgroundColor(mActiveCategory.getColor());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

    @Subscribe
    public void onDisplayMainEvent(DisplayMainEvent displayMainEvent)
    {
        finish();
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
        finish();
    }
}
