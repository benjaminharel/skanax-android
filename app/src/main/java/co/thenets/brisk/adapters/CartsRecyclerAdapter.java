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
import co.thenets.brisk.custom.CartItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.interfaces.OnCartItemClickListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Cart;
import co.thenets.brisk.models.Store;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class CartsRecyclerAdapter extends RecyclerView.Adapter<CartItemViewHolder>
{
    private Context mContext;
    private final ArrayList<Cart> mCartList;

    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    public CartsRecyclerAdapter(Context context)
    {
        mContext = context;
        mCartList = ContentManager.getInstance().getCartList();
    }

    @Override
    public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartItemViewHolder(v, new OnCartItemClickListener()
        {
            @Override
            public void onCartSelected(int position)
            {
                // TODO: Check this!
//                ContentManager.getInstance().setSelectedCart(mCartList.get(position));
            }

            @Override
            public void onStorePressed(int position)
            {
                Store store = mCartList.get(position).getStore();
                // TODO: Handle this!
//                ContentManager.getInstance().setCurrentStore(store);
                Intent intent = new Intent(mContext, StoreActivity.class);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public void onBindViewHolder(CartItemViewHolder holder, final int position)
    {
        Cart cart = mCartList.get(position);
        holder.storeNameTextView.setText(cart.getStore().getName());
        holder.storeEtaTextView.setText(String.format(mContext.getString(R.string.avg_eta), cart.getStore().getETA()));
        holder.storeDeliveryPriceTextView.setText(String.format(mContext.getString(R.string.delivery_price_with_value), String.valueOf((int)cart.getStore().getDeliveryPrice())));
        holder.totalPriceTextView.setText(String.format(mContext.getString(R.string.total_price), String.valueOf(cart.getTotalPrice())));
        holder.ratingBar.setRating(cart.getStore().getRating());
        UIManager.getInstance().loadImage(mContext, cart.getStore().getPhotoGallery().getMedium(), holder.storeImageView, ImageType.STORE);
        setRatingReviews(cart, holder);

        setAvailableItemsTextView(holder, cart);
        holder.itemView.setTag(cart);

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);
    }

    private void setRatingReviews(Cart cart,CartItemViewHolder holder)
    {
        if(cart.getStore().getReviewsCounter() > 0)
        {
            holder.reviewsTextView.setText(String.format(mContext.getString(R.string.number_of_reviews), cart.getStore().getReviewsCounter()));
        }
        else
        {
            holder.reviewsTextView.setVisibility(View.GONE);
        }
    }

    private void setAvailableItemsTextView(CartItemViewHolder holder, Cart cart)
    {
        if(cart.areAllProductsAvailable())
        {
            holder.availableItemsTextView.setText(mContext.getString(R.string.all_products_available));
            holder.availableItemsTextView.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        }
        else
        {
            int availableItemsCounter = cart.getAvailableItemsCounter();
            int itemCounter = cart.getCartItems().size();
            holder.availableItemsTextView.setText(String.format(mContext.getString(R.string.available_items_counter), String.valueOf(availableItemsCounter), String.valueOf(itemCounter)));
            holder.availableItemsTextView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }
    }


    @Override
    public int getItemCount()
    {
        return mCartList.size();
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
