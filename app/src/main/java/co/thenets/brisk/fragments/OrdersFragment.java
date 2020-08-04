package co.thenets.brisk.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.OrdersRecyclerAdapter;
import co.thenets.brisk.enums.FragmentType;
import co.thenets.brisk.events.FragmentResumedEvent;
import co.thenets.brisk.events.OrdersStateChangedEvent;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID-WORK on 05/11/2015.
 */
public class OrdersFragment extends BasicFragment
{
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mNoItemsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventsManager.getInstance().register(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventsManager.getInstance().unregister(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EventsManager.getInstance().post(new FragmentResumedEvent(FragmentType.ORDERS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_orders, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
        setContent();
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
                    OrdersRecyclerAdapter ordersRecyclerAdapter = new OrdersRecyclerAdapter(getActivity());
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

    private void setViews()
    {
        setToolBar();
        setOtherViews();
    }

    private void setToolBar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.orders_toolbar);
        mToolbar.setTitle(getString(R.string.my_orders));
        if (getActivity() instanceof AppCompatActivity)
        {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
    }

    private void setOtherViews()
    {
        mNoItemsTextView = (TextView) mRootView.findViewById(R.id.noItemsTextView);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Subscribe
    public void refreshOrders(OrdersStateChangedEvent ordersStateChangedEvent)
    {
        setContent();
    }
}
