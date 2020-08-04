package co.thenets.brisk.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.HashMap;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.BecomeBriskerActivity;
import co.thenets.brisk.adapters.InventoryRecyclerAdapter;
import co.thenets.brisk.events.RefreshInventoryEvent;
import co.thenets.brisk.events.SwitchStateEvent;
import co.thenets.brisk.interfaces.NavigationActivity;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.AdvancedConfiguration;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class InventoryFragment extends BasicFragment
{
    private static final String LOG_TAG = InventoryFragment.class.getSimpleName();
    private View mRootView;
    private RecyclerView mRecyclerView;
    private TextView mNoItemsTextView;
    private Toolbar mToolbar;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_inventory, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
        setViewsVisibility();
        getItems();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        if(getActivity() instanceof NavigationActivity)
        {
            menuInflater.inflate(R.menu.menu_add_inventory, menu);
        }
        else
        {
            menuInflater.inflate(R.menu.menu_set_inventory, menu);
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query)
            {
                HashMap<String, String> params = new HashMap<>();
                params.put(AdvancedConfiguration.QUERY_KEY, query);
                RestClientManager.getInstance().getProducts(params, new RequestListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        InventoryRecyclerAdapter adapter = new InventoryRecyclerAdapter(getActivity());
                        mRecyclerView.setAdapter(adapter);
                        setViewsVisibility();
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
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_done:
                if(getActivity() instanceof BecomeBriskerActivity)
                {
                    getActivity().finish();
                    EventsManager.getInstance().post(new SwitchStateEvent());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getItems()
    {
        HashMap<String, String> params = new HashMap<>();
        params.put(AdvancedConfiguration.STORE_ID_KEY, ContentManager.getInstance().getStore().getID());
        RestClientManager.getInstance().getProducts(params, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                InventoryRecyclerAdapter adapter = new InventoryRecyclerAdapter(getActivity());
                mRecyclerView.setAdapter(adapter);
                setViewsVisibility();
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

    private void setViews()
    {
        setToolBar();
        setOtherViews();
        setFab();
    }

    private void setOtherViews()
    {
        mNoItemsTextView = (TextView) mRootView.findViewById(R.id.noItemsTextView);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setFab()
    {
        mFAB = (FloatingActionButton) mRootView.findViewById(R.id.inventory_fab);
    }

    private void setToolBar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.inventory_toolbar);
        mToolbar.setTitle(getString(R.string.add_items_for_sale));
        if (getActivity() instanceof AppCompatActivity)
        {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
    }

    private void setViewsVisibility()
    {
        if (ContentManager.getInstance().getProductList() == null)
        {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mNoItemsTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoItemsTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe
    public void refreshAdapter(RefreshInventoryEvent refreshInventoryEvent)
    {
        getItems();
    }

}
