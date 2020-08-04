package co.thenets.brisk.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.SelectedCartItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.CartItem;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class SelectedCartRecyclerAdapter extends RecyclerView.Adapter<SelectedCartItemViewHolder>
{
    private ArrayList<CartItem> mCartItemsList;
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    public SelectedCartRecyclerAdapter(Context context)
    {
        mContext = context;
        // TODO: check this
//        mCartItemsList = ContentManager.getInstance().getSelectedCart().getCartItems();
    }

    @Override
    public SelectedCartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item_layout, parent, false);
        return new SelectedCartItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SelectedCartItemViewHolder holder, final int position)
    {
        CartItem cartItem = mCartItemsList.get(position);
        holder.nameTextView.setText(cartItem.getStoreProduct().getName());
        holder.brandTextView.setText(cartItem.getStoreProduct().getBrand());
        holder.quantityTextView.setText(String.valueOf(cartItem.getAmount()));

        setPrice(holder, cartItem);
        setContent(holder, cartItem);
        setNotAvailableCoverVisibility(holder, cartItem);
        UIManager.getInstance().loadImage(mContext, cartItem.getStoreProduct().getPhotoGallery().getSmall(), holder.itemImageView, ImageType.STORE);
        holder.itemView.setTag(cartItem);
        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);
    }

    private void setContent(SelectedCartItemViewHolder holder, CartItem cartItem)
    {
        if(TextUtils.isEmpty(cartItem.getStoreProduct().getContent()))
        {
            holder.contentTextView.setVisibility(View.GONE);
        }
        else
        {
            holder.contentTextView.setText(cartItem.getStoreProduct().getContent());
            holder.contentTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setNotAvailableCoverVisibility(SelectedCartItemViewHolder holder, CartItem cartItem)
    {
        if(cartItem.isAvailable())
        {
            holder.notAvailableCover.setVisibility(View.GONE);
        }
        else
        {
            holder.notAvailableCover.setVisibility(View.VISIBLE);
        }
    }

    private void setPrice(SelectedCartItemViewHolder holder, CartItem cartItem)
    {
        if(cartItem.isAvailable())
        {
            holder.priceTextView.setText(String.format(mContext.getString(R.string.price_value), Utils.formatDouble(cartItem.getStoreProduct().getPrice())));
        }
        else
        {
            holder.priceTextView.setText(mContext.getString(R.string.out_of_stock));
        }
    }


    @Override
    public int getItemCount()
    {
        return mCartItemsList.size();
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
