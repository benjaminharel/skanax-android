package co.thenets.brisk.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.CartAdapter;
import co.thenets.brisk.events.CartItemDeletedWithSwipeEvent;
import co.thenets.brisk.events.CartItemsRemovedEvent;
import co.thenets.brisk.events.CartUpdatedEvent;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;

/**
 * Created by DAVID-WORK on 05/11/2015.
 */
public class ShoppingListFragment extends BasicFragment
{
    private RecyclerView mRecyclerView;
    private TextView mNoItemsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventsManager.getInstance().register(this);

        AnalyticsManager.getInstance().cartScreen();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventsManager.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
    }

    private void setViews()
    {
        mNoItemsTextView = (TextView) mRootView.findViewById(R.id.noItemsTextView);
        setShoppingItemList();
        setViewsVisibility();
    }

    private void setViewsVisibility()
    {
        if (ContentManager.getInstance().getCart().isEmpty())
        {
            mNoItemsTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
        else
        {
            mNoItemsTextView.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setShoppingItemList()
    {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        CartAdapter cartAdapter = new CartAdapter(getActivity());
        mRecyclerView.setAdapter(cartAdapter);
    }

    @Subscribe
    public void refreshData(CartUpdatedEvent cartUpdatedEvent)
    {
        ((CartAdapter) mRecyclerView.getAdapter()).itemUpdated(cartUpdatedEvent);
        setViewsVisibility();
    }

    @Subscribe
    public void refreshData(CartItemsRemovedEvent cartItemsRemovedEvent)
    {
        ((CartAdapter) mRecyclerView.getAdapter()).itemRemoved(cartItemsRemovedEvent);
        setViewsVisibility();
    }

    @Subscribe
    public void refreshData(CartItemDeletedWithSwipeEvent cartItemDeletedWithSwipeEvent)
    {
        setViewsVisibility();
    }
}
