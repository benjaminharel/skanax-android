package co.thenets.brisk.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.StoreActivity;
import co.thenets.brisk.custom.SearchResultItemViewHolder2;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.interfaces.SearchActionsListener;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Store;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class SearchResultsRecyclerAdapter extends RecyclerView.Adapter<SearchResultItemViewHolder2>
{
    private ArrayList<Store> mItems;
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    public SearchResultsRecyclerAdapter(Context context, ArrayList<Store> storeList)
    {
        mContext = context;
        mItems = storeList;
    }

    @Override
    public SearchResultItemViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        return new SearchResultItemViewHolder2(v, new SearchActionsListener()
        {
            @Override
            public void onStorePressed(int position)
            {
                Store store = mItems.get(position);
                Intent intent = new Intent(mContext, StoreActivity.class);
                intent.putExtra(Params.STORE_ID, store.getID());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(SearchResultItemViewHolder2 holder, final int position)
    {
        Store store = mItems.get(position);

        UIManager.getInstance().loadImage(mContext, store.getPhotoGallery().getMedium(), holder.imageView, ImageType.STORE);
        holder.storeNameTextView.setText(store.getName());
        holder.ratingBar.setRating(store.getRating());
        holder.reviewsCounter.setText(String.format(mContext.getString(R.string.number_of_reviews), store.getReviewsCounter()));
        setProductsRecyclerView(holder, store);

        holder.itemView.setTag(store);

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);
    }

    private void setProductsRecyclerView(final SearchResultItemViewHolder2 holder, Store store)
    {
        holder.productsRecyclerView.setHasFixedSize(true);
        holder.productsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        holder.productsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        StoreProductsRecyclerAdapter adapter = new StoreProductsRecyclerAdapter(mContext, store);
        holder.productsRecyclerView.setAdapter(adapter);
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > mLastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount()
    {
        return mItems.size();
    }
}
