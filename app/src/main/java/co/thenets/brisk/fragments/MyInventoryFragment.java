package co.thenets.brisk.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.MyInventoryRecyclerAdapter;
import co.thenets.brisk.custom.LinearLayoutManager;
import co.thenets.brisk.enums.FragmentType;
import co.thenets.brisk.events.FragmentResumedEvent;
import co.thenets.brisk.events.ItemAddedMyInventoryEvent;
import co.thenets.brisk.events.ItemDeletedFromMyInventoryEvent;
import co.thenets.brisk.events.ItemEditedMyInventoryEvent;
import co.thenets.brisk.events.MyInventoryCategoriesChangedEvent;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.StoreProduct;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.responses.GetStoreProductsResponse;
import co.thenets.brisk.rest.service.RequestGetStoreProductsListener;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyInventoryFragment extends BasicFragment
{
    private static final String LOG_TAG = MyInventoryFragment.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private TextView mNoItemsTextView;
    private LinearLayout mProgressBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
        EventsManager.getInstance().post(new FragmentResumedEvent(FragmentType.INVENTORY));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_my_inventory, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
        getItems();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_inventory, menu);

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
                final String storeID = ContentManager.getInstance().getStore().getID();
                RestClientManager.getInstance().getStoreProducts(storeID, query, new RequestGetStoreProductsListener()
                {
                    @Override
                    public void onSuccess(GetStoreProductsResponse getStoreProductsResponse)
                    {
                        MyInventoryRecyclerAdapter adapter = new MyInventoryRecyclerAdapter(getActivity());
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

    private void getItems()
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        final String storeID = ContentManager.getInstance().getStore().getID();
        RestClientManager.getInstance().getStoreProducts(storeID, "",new RequestGetStoreProductsListener()
        {
            @Override
            public void onSuccess(GetStoreProductsResponse getStoreProductsResponse)
            {
                MyInventoryRecyclerAdapter adapter = new MyInventoryRecyclerAdapter(getActivity());
                mRecyclerView.setAdapter(adapter);
                setViewsVisibility();
                mProgressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mNoItemsTextView, getString(R.string.error_in_getting_items_for_sale), Snackbar.LENGTH_LONG);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mNoItemsTextView, getString(R.string.error_in_getting_items_for_sale), Snackbar.LENGTH_LONG);
            }
        });
    }

    private void setViews()
    {
        setToolBar();
        setDrawerLayout();
        setOtherViews();
        setFab();
    }

    private void setToolBar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.my_inventory_toolbar);
        mToolbar.setTitle(getString(R.string.shop_items));
        if (getActivity() instanceof AppCompatActivity)
        {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
    }

    private void setDrawerLayout()
    {
        mDrawerLayout = (DrawerLayout) mRootView.findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_flipped, GravityCompat.END);
    }

    private void setOtherViews()
    {
        mProgressBarLayout = (LinearLayout) mRootView.findViewById(R.id.progressBarLayout);
        mNoItemsTextView = (TextView) mRootView.findViewById(R.id.noItemsTextView);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setFab()
    {
        mFAB = (FloatingActionButton) mRootView.findViewById(R.id.my_inventory_fab);
    }


    private void setViewsVisibility()
    {
        if (ContentManager.getInstance().getStoreProductList() == null
                || ContentManager.getInstance().getStoreProductList().isEmpty())
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

    public boolean isFilterDrawerClosed()
    {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
        {
            mDrawerLayout.closeDrawer(GravityCompat.END);
            return false;
        }

        return true;
    }

    @Subscribe
    public void refreshUIAfterItemDeleted(ItemDeletedFromMyInventoryEvent itemDeletedEvent)
    {
        setViewsVisibility();
    }

    @Subscribe
    public void refreshUIAfterCategoryChanged(MyInventoryCategoriesChangedEvent myInventoryCategoriesChangedEvent)
    {
        getItems();
    }

    @Subscribe
    public void refreshUIAfterItemAdded(ItemAddedMyInventoryEvent itemAddedMyInventoryEvent)
    {
        setOtherViews();
        getItems();
    }

    @Subscribe
    public void refreshUIAfterItemEdited(ItemEditedMyInventoryEvent itemEditedMyInventoryEvent)
    {
        ArrayList<StoreProduct> storeProductList = ContentManager.getInstance().getStoreProductList();
        for (int i = 0; i < storeProductList.size(); i++)
        {
            if(storeProductList.get(i).getID().equals(itemEditedMyInventoryEvent.getStoreProductID()))
            {
                mRecyclerView.getAdapter().notifyItemChanged(i);
                break;
            }
        }
    }
}
