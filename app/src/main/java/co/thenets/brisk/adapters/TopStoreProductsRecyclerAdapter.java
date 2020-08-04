package co.thenets.brisk.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.TopProductItemViewHolder;
import co.thenets.brisk.dialogs.ProductDetailsDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.ProductView;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Store;
import co.thenets.brisk.models.StoreProduct;

import static co.thenets.brisk.general.Params.DEFAULT_NUMBER_OF_TOP_PRODUCTS_FOR_STORE;

/**
 * Created by DAVID BELOOSESKY on 05/06/2016.
 */
public class TopStoreProductsRecyclerAdapter extends RecyclerView.Adapter<TopProductItemViewHolder>
{
    private ArrayList<StoreProduct> mItems;
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;
    private int mParentViewWidth;
    private Store mStore;

    public TopStoreProductsRecyclerAdapter(Context context, int parentWidth, Store store)
    {
        mContext = context;
        mParentViewWidth = parentWidth;
        mStore = store;
        mItems = mStore.getStoreProducts();
    }

    @Override
    public TopProductItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_top_product_item_layout, parent, false);
        return new TopProductItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TopProductItemViewHolder holder, final int position)
    {
        final StoreProduct storeProduct = mItems.get(position);
        UIManager.getInstance().loadImage(mContext, storeProduct.getPhotoGallery().getMedium(), holder.itemImageView, ImageType.PRODUCT);
        holder.itemView.setTag(storeProduct);

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemImageView, position);


        //set view size in proportion to screen size
        setItemSize(holder.itemImageView);

        holder.itemImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                StoreProduct storeProduct = mItems.get(position);
                ProductDetailsDialog detailsDialog = ProductDetailsDialog.newInstance(mStore.getID(), storeProduct, ProductView.BUYER_VIEW);
                detailsDialog.show(((Activity) mContext).getFragmentManager(), "detailsDialog");
            }
        });
    }

    private void setItemSize(ImageView itemContainer)
    {
        int itemWidth = Utils.getScreenWidth(mContext) / 3;
        itemContainer.getLayoutParams().height = itemWidth;
        itemContainer.getLayoutParams().width = itemWidth;
    }

    @Override
    public int getItemCount()
    {
        return Math.min(mItems.size(), DEFAULT_NUMBER_OF_TOP_PRODUCTS_FOR_STORE);
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
