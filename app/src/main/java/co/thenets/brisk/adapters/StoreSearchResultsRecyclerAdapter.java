package co.thenets.brisk.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.SearchResultItemViewHolder;
import co.thenets.brisk.dialogs.ProductDetailsDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.ProductView;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.OnProductClickListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.StoreProduct;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class StoreSearchResultsRecyclerAdapter extends RecyclerView.Adapter<SearchResultItemViewHolder>
{
    private ArrayList<StoreProduct> mItems;
    private Context mContext;
    private String mStoreID;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    public StoreSearchResultsRecyclerAdapter(Context context, String storeID, ArrayList<StoreProduct> storeProductList)
    {
        mContext = context;
        mItems = storeProductList;
        mStoreID = storeID;
    }
    
    public void addItems(ArrayList<StoreProduct> moreItems)
    {
        mItems.addAll(moreItems);
        notifyDataSetChanged();
    }

    @Override
    public SearchResultItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_product_item_layout, parent, false);
        return new SearchResultItemViewHolder(v, new OnProductClickListener()
        {
            @Override
            public void onProductClicked(int position)
            {
                StoreProduct storeProduct = mItems.get(position);
                ProductDetailsDialog detailsDialog = ProductDetailsDialog.newInstance(mStoreID, storeProduct, ProductView.BUYER_VIEW);
                detailsDialog.show(((Activity) mContext).getFragmentManager(), "detailsDialog");
            }

            @Override
            public void onActionClicked(int position)
            {
                StoreProduct storeProduct = mItems.get(position);
                ContentManager.getInstance().addToCart(mStoreID, storeProduct);
            }
        });
    }


    @Override
    public void onBindViewHolder(SearchResultItemViewHolder holder, final int position)
    {
        final StoreProduct storeProduct = mItems.get(position);
        holder.nameTextView.setText(storeProduct.getName());
        handleContent(storeProduct, holder);
        holder.brandTextView.setText(storeProduct.getBrand());
        holder.priceTextView.setText(String.format(mContext.getString(R.string.min_price), Params.CURRENCY, Utils.formatDouble(storeProduct.getPrice())));
        UIManager.getInstance().loadImage(mContext, storeProduct.getPhotoGallery().getMedium(), holder.itemImageView, ImageType.PRODUCT);
        holder.itemView.setTag(storeProduct);

//        // Here you apply the animation when the view is bound
//        setAnimation(holder.itemContainer, position);
    }

    private void handleContent(StoreProduct storeProduct, SearchResultItemViewHolder holder)
    {
        if(TextUtils.isEmpty(storeProduct.getContent()))
        {
            holder.contentTextView.setVisibility(View.GONE);
        }
        else
        {
            holder.contentTextView.setVisibility(View.VISIBLE);
            holder.contentTextView.setText(storeProduct.getContent());
        }
    }


    @Override
    public int getItemCount()
    {
        return mItems.size();
    }
}
