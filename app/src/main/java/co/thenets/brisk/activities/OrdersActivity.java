package co.thenets.brisk.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.OrdersRecyclerAdapter;
import co.thenets.brisk.events.OrdersStateChangedEvent;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

public class OrdersActivity extends BaseActivity
{
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mNoItemsTextView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        mContext = this;
        setViews();
        setContent();
        EventsManager.getInstance().register(this);
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    private void setViews()
    {
        setToolbar();
        setOtherViews();
    }

    private void setOtherViews()
    {
        mNoItemsTextView = (TextView) findViewById(R.id.noItemsTextView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.my_orders);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setContent()
    {
        RestClientManager.getInstance().getOrders(new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                if (ContentManager.getInstance().getOrderList().isEmpty())
                {
                    mNoItemsTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    OrdersRecyclerAdapter ordersRecyclerAdapter = new OrdersRecyclerAdapter(mContext);
                    mRecyclerView.setAdapter(ordersRecyclerAdapter);
                }

                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBar.setVisibility(View.INVISIBLE);
                mNoItemsTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBar.setVisibility(View.INVISIBLE);
                mNoItemsTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Subscribe
    public void refreshOrders(OrdersStateChangedEvent ordersStateChangedEvent)
    {
        setContent();
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



}
