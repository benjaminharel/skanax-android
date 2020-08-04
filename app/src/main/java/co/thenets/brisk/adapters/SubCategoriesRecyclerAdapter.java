package co.thenets.brisk.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.squareup.picasso.Callback;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.ActiveProductsActivity;
import co.thenets.brisk.custom.ActiveCategoryItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.ActiveCategory;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class SubCategoriesRecyclerAdapter extends RecyclerView.Adapter<ActiveCategoryItemViewHolder>
{
    private ArrayList<ActiveCategory> mItems;
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    public SubCategoriesRecyclerAdapter(Context context, ArrayList<ActiveCategory> items)
    {
        mContext = context;
        mItems = items;
    }

    @Override
    public ActiveCategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_subcategory_item_layout, parent, false);
        return new ActiveCategoryItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ActiveCategoryItemViewHolder holder, final int position)
    {
        final ActiveCategory activeCategory = mItems.get(position);
        holder.nameTextView.setText(activeCategory.getName());
        UIManager.getInstance().loadImage(mContext, activeCategory.getIconLink(), holder.itemImageView, new Callback()
        {
            @Override
            public void onSuccess()
            {
                if (activeCategory.getCounter() == 0)
                {
                    Bitmap originalBitmap = ((BitmapDrawable)holder.itemImageView.getDrawable()).getBitmap();
                    holder.itemImageView.setImageBitmap(Utils.convertBitmapToGrayScale(originalBitmap));
                }
            }

            @Override
            public void onError()
            {

            }
        }, ImageType.GENERAL);
        holder.itemView.setTag(activeCategory);

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);

        holder.itemContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (activeCategory.getCounter() > 0)
                {
                    // Save the selected sub category to ContentManager
                    ContentManager.getInstance().setSelectedActiveSubCategory(activeCategory);

                    Intent intent = new Intent(mContext, ActiveProductsActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext,
                            new Pair<View, String>(holder.itemImageView, mContext.getString(R.string.tr_category)));
                    ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());

                    AnalyticsManager.getInstance().subCategorySelected(activeCategory.getName());
                }
                else
                {
                    UIManager.getInstance().displaySnackBar(v, mContext.getString(R.string.there_are_no_items_in_this_subcategory_right_now), Snackbar.LENGTH_LONG);
                }
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
}
