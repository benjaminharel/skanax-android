package co.thenets.brisk.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.StoreActivity;
import co.thenets.brisk.custom.LinearLayoutManager;
import co.thenets.brisk.custom.StoreItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Store;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class StoresAdapter extends RecyclerView.Adapter<StoreItemViewHolder>
{
    private ArrayList<Store> mItems;
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    public StoresAdapter(Context context)
    {
        mContext = context;
        mItems = ContentManager.getInstance().getStoreList();
    }

    @Override
    public StoreItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.availiable_store_item_layout, parent, false);
        return new StoreItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StoreItemViewHolder holder, final int position)
    {
        final Store store = mItems.get(position);
        UIManager.getInstance().loadImage(mContext, store.getPhotoGallery().getMedium(), holder.profileImageView, ImageType.STORE);
        holder.nameTextView.setText(store.getName());
        holder.ratingBar.setRating((int) store.getRating());
        holder.reviewsCounterTextView.setText(String.format(mContext.getString(R.string.number_of_reviews), store.getReviewsCounter()));
        if(store.isActive())
        {
            holder.etaTextView.setText(String.format(mContext.getString(R.string.avg_eta), store.getETA()));
            holder.etaImageView.setVisibility(View.VISIBLE);
            holder.closedNowView.setVisibility(View.GONE);
            holder.etaTextView.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray));
        }
        else
        {
            holder.closedNowView.setVisibility(View.VISIBLE);
            holder.closedNowView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // Do nothing!
                }
            });
            holder.etaImageView.setVisibility(View.GONE);
            holder.etaTextView.setText(mContext.getString(R.string.closed_now));
            holder.etaTextView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

        setTopProductsRecyclerView(holder, store);
        holder.itemView.setTag(store);

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);

        holder.itemContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, StoreActivity.class);
                intent.putExtra(Params.STORE_ID, store.getID());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return mItems.size();
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

    private void setTopProductsRecyclerView(StoreItemViewHolder holder, Store store)
    {
        holder.topProductsRecyclerView.setHasFixedSize(true);
        holder.topProductsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        TopStoreProductsRecyclerAdapter adapter = new TopStoreProductsRecyclerAdapter(mContext, holder.itemContainer.getWidth(), store);
        holder.topProductsRecyclerView.setAdapter(adapter);
    }
}
