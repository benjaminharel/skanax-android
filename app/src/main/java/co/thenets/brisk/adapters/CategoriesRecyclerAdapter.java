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
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.squareup.picasso.Callback;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.SubcategoriesActivity;
import co.thenets.brisk.custom.ActiveCategoryItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.ActiveCategory;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<ActiveCategoryItemViewHolder>
{
    private ArrayList<ActiveCategory> mItems;
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;
    private int mParentViewHeight;

    public CategoriesRecyclerAdapter(Context context, int parentHeight)
    {
        mContext = context;
        mParentViewHeight = parentHeight;
        mItems = ContentManager.getInstance().getActiveCategoryList();
    }

    @Override
    public ActiveCategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_category_item_layout, parent, false);
        return new ActiveCategoryItemViewHolder(view);
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
                    Bitmap originalBitmap = ((BitmapDrawable) holder.itemImageView.getDrawable()).getBitmap();
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


        //set height in proportion to screen size
        setItemHeight(holder.itemContainer);

        holder.itemContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (activeCategory.getCounter() > 0)
                {
                    // Save the selected category to ContentManager
                    ContentManager.getInstance().setSelectedActiveCategory(activeCategory);

                    Intent intent = new Intent(mContext, SubcategoriesActivity.class);
                    intent.putExtra(Params.ACTIVE_CATEGORY, activeCategory);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext,
                            // For each shared element, add to this method a new Pair item,
                            // which contains the reference of the view we are transitioning *from*,
                            // and the value of the transitionName attribute
                            new Pair<View, String>(holder.itemImageView, mContext.getString(R.string.tr_category)));
                    ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());

                    AnalyticsManager.getInstance().categorySelected(activeCategory.getName());
                }
                else
                {
                    UIManager.getInstance().displaySnackBar(v, mContext.getString(R.string.there_are_no_items_in_this_category_right_now), Snackbar.LENGTH_LONG);
                }
            }
        });
    }

    private void setItemHeight(CardView itemContainer)
    {
        ViewGroup.MarginLayoutParams marginLayoutParams = ((ViewGroup.MarginLayoutParams) itemContainer.getLayoutParams());
        int itemHeight = mParentViewHeight / 3 - (marginLayoutParams.topMargin + marginLayoutParams.bottomMargin);
        CardView.LayoutParams params = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, itemHeight);
        params.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
        itemContainer.setLayoutParams(params);
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
