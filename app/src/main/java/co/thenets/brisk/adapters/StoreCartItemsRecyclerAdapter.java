package co.thenets.brisk.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.HashMap;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.ShoppingListItemViewHolder;
import co.thenets.brisk.dialogs.ProductDetailsDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.ProductView;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.OnShoppingListItemClickListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Store;
import co.thenets.brisk.models.StoreProduct;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class StoreCartItemsRecyclerAdapter extends RecyclerView.Adapter<ShoppingListItemViewHolder>
{
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    private Store mStore;
    private String[] mStoreProductIDs;
    private HashMap<String, Integer> mProductIDsAndAmount;

    public StoreCartItemsRecyclerAdapter(Context context, Store store, HashMap<String, Integer> productIDsAndAmount)
    {
        mContext = context;
        mStore = store;
        mProductIDsAndAmount = productIDsAndAmount;
        mStoreProductIDs = mProductIDsAndAmount.keySet().toArray(new String[mProductIDsAndAmount.size()]);
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
                String storeProductID = mStoreProductIDs[position];
                StoreProduct storeProduct = ContentManager.getInstance().getStoreProduct(mStore.getID(), storeProductID);
                ProductDetailsDialog detailsDialog = ProductDetailsDialog.newInstance(mStore.getID(), storeProduct, ProductView.BUYER_VIEW);
                detailsDialog.show(((Activity) mContext).getFragmentManager(), "detailsDialog");
            }

            @Override
            public void onAddClicked(int position)
            {
                String storeProductID = mStoreProductIDs[position];
                StoreProduct storeProduct = ContentManager.getInstance().getStoreProduct(mStore.getID(), storeProductID);
                ContentManager.getInstance().addToCart(mStore.getID(), storeProduct);
            }

            @Override
            public void onRemoveClicked(int position)
            {
                String storeProductID = mStoreProductIDs[position];
                StoreProduct storeProduct = ContentManager.getInstance().getStoreProduct(mStore.getID(), storeProductID);
                Integer currentQuantity = mProductIDsAndAmount.get(storeProductID);
                if (currentQuantity == 1)
                {
                    // You can't remove the last item
                    UIManager.getInstance().displaySnackBar(v, mContext.getString(R.string.swipe_to_dismiss), Snackbar.LENGTH_SHORT);
                }
                else
                {
                    ContentManager.getInstance().removeFromCart(mStore.getID(), storeProductID, false);
                }
            }
        });
    }


    @Override
    public void onBindViewHolder(ShoppingListItemViewHolder holder, final int position)
    {
        String storeProductID = mStoreProductIDs[position];
        final StoreProduct storeProduct = ContentManager.getInstance().getStoreProduct(mStore.getID(), storeProductID);
        Integer currentQuantity = mProductIDsAndAmount.get(storeProductID);

        holder.nameTextView.setText(storeProduct.getName());
        holder.brandTextView.setText(storeProduct.getBrand());
        holder.priceTextView.setText(String.format(mContext.getString(R.string.min_price), Params.CURRENCY, Utils.formatDouble(storeProduct.getPrice())));
        UIManager.getInstance().loadImage(mContext, storeProduct.getPhotoGallery().getSmall(), holder.itemImageView, ImageType.PRODUCT);
        holder.quantityTextView.setText(String.valueOf(currentQuantity));
        holder.itemView.setTag(storeProduct);

        // Here you apply the animation when the view is bound
//        setAnimation(holder.itemContainer, position);
    }


    @Override
    public int getItemCount()
    {
        return mProductIDsAndAmount.size();
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

    public void removeAt(int position, boolean removeAllItems)
    {
        // Update the local arrays
        String storeProductID = mStoreProductIDs[position];

        // Notify listeners
        ContentManager.getInstance().removeFromCart(mStore.getID(), storeProductID, removeAllItems);
    }


}
