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
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.CartsRecyclerAdapter;
import co.thenets.brisk.events.CartUpdatedEvent;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.models.CartItemForUpload;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateCartListRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID-WORK on 05/11/2015.
 */
public class CompareCartsFragment extends BasicFragment
{
    private RecyclerView mRecyclerView;
    private TextView mNoItemsTextView;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventsManager.getInstance().register(this);

        AnalyticsManager.getInstance().compareCarts();
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
        mRootView = inflater.inflate(R.layout.fragment_compare_carts, container, false);
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
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        setCarts();
        setToolBar();
    }

    private void setToolBar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.compare_carts_toolbar);
        mToolbar.setTitle(getString(R.string.title_activity_cart));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setCarts()
    {
        CartsRecyclerAdapter cartRecyclerAdapter = new CartsRecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(cartRecyclerAdapter);
    }

    @Subscribe
    public void refreshViews(CartUpdatedEvent cartUpdatedEvent)
    {
        // TODO: handle this!
//        ArrayList<CartItemForUpload> shoppingList = ContentManager.getInstance().getShoppingListForUpload();
        ArrayList<CartItemForUpload> shoppingList = null;
        RestClientManager.getInstance().compareShoppingListItems(new CreateCartListRequest(shoppingList), new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                setCarts();
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
}
