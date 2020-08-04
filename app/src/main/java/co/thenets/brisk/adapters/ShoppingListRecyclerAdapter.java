package co.thenets.brisk.adapters;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.HashMap;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.ShoppingListItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.events.CartItemDeletedWithSwipeEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.OnShoppingListItemClickListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.ShoppingItem;
import co.thenets.brisk.models.StoreProduct;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListItemViewHolder>
{
    private HashMap<String, ShoppingItem> mItemsMap;
    private ArrayList<ShoppingItem> mShoppingItemList = new ArrayList<>();
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    public ShoppingListRecyclerAdapter(Context context)
    {
        mContext = context;
        mItemsMap = ContentManager.getInstance().getShoppingListMap();
        mShoppingItemList = new ArrayList<>(mItemsMap.values());
    }

    @Override
    public ShoppingListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item_layout, parent, false);
        return new ShoppingListItemViewHolder(v, new OnShoppingListItemClickListener()
        {
            @Override
            public void onProductClicked(int position)
            {
                // TODO: Handle this!
//                ProductDetailsDialog detailsDialog = ProductDetailsDialog.newInstance(mShoppingItemList.get(position).getStoreProduct(), ProductView.BUYER_VIEW);
//                detailsDialog.show(((Activity) mContext).getFragmentManager(), "detailsDialog");
            }

            @Override
            public void onAddClicked(int position)
            {
                StoreProduct storeProduct = mShoppingItemList.get(position).getStoreProduct();
                // TODO: Fix this storeID
                ContentManager.getInstance().addToCart("1234", storeProduct);
            }

            @Override
            public void onRemoveClicked(int position)
            {
                Integer currentQuantity = mShoppingItemList.get(position).getAmount();
                if(currentQuantity == 1)
                {
                    // You can't remove the last item
                    UIManager.getInstance().displaySnackBar(v, mContext.getString(R.string.swipe_to_dismiss), Snackbar.LENGTH_SHORT);
                }
                else
                {
                    StoreProduct storeProduct = mShoppingItemList.get(position).getStoreProduct();
                    // TODO: Fix this storeID
                    ContentManager.getInstance().removeFromCart("1234", storeProduct.getID(),false);
                }
            }
        });
    }


    @Override
    public void onBindViewHolder(ShoppingListItemViewHolder holder, final int position)
    {
        final StoreProduct storeProduct = mShoppingItemList.get(position).getStoreProduct();
        holder.nameTextView.setText(storeProduct.getName());
        holder.brandTextView.setText(storeProduct.getBrand());
        holder.priceTextView.setText(String.format(mContext.getString(R.string.min_price), Params.CURRENCY, Utils.formatDouble(storeProduct.getPrice())));
        UIManager.getInstance().loadImage(mContext, storeProduct.getPhotoGallery().getSmall(), holder.itemImageView, ImageType.PRODUCT);
        holder.quantityTextView.setText(String.valueOf(mShoppingItemList.get(position).getAmount()));
        holder.itemView.setTag(storeProduct);

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);
    }


    @Override
    public int getItemCount()
    {
        return mItemsMap.size();
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

    public void removeAt(int position)
    {
        StoreProduct storeProduct = mShoppingItemList.get(position).getStoreProduct();
        mItemsMap.remove(storeProduct.getID());

        mShoppingItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mShoppingItemList.size());

        EventsManager.getInstance().post(new CartItemDeletedWithSwipeEvent());
    }

    public void itemUpdated(StoreProduct storeProduct)
    {
        int position = 0;
        mItemsMap = ContentManager.getInstance().getShoppingListMap();
        mShoppingItemList = new ArrayList<>(mItemsMap.values());

        for (int i = 0; i < mShoppingItemList.size(); i++)
        {
            if(mShoppingItemList.get(i).getStoreProduct().getID().equals(storeProduct.getID()))
            {
                position = i;
            }
        }
        notifyItemChanged(position);
    }


}
