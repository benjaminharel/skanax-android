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
import co.thenets.brisk.custom.MyInventoryItemViewHolder;
import co.thenets.brisk.dialogs.ProductDetailsDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.ProductView;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.OnProductClickListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.StoreProduct;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class MyInventoryRecyclerAdapter extends RecyclerView.Adapter<MyInventoryItemViewHolder>
{
    private ArrayList<StoreProduct> mItems;
    private Context mContext;

    public MyInventoryRecyclerAdapter(Context context)
    {
        mContext = context;
        mItems = ContentManager.getInstance().getStoreProductList();
    }

    @Override
    public MyInventoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_inventory_item_layout, parent, false);
        return new MyInventoryItemViewHolder(v, new OnProductClickListener()
        {
            @Override
            public void onProductClicked(int position)
            {
                StoreProduct storeProduct = mItems.get(position);
                ProductDetailsDialog detailsDialog = ProductDetailsDialog.newInstance(ContentManager.getInstance().getStore().getID(),storeProduct, ProductView.MY_INVENTORY_VIEW);
                detailsDialog.show(((Activity) mContext).getFragmentManager(), "detailsDialog");
            }

            @Override
            public void onActionClicked(final int position)
            {
                final StoreProduct storeProduct = mItems.get(position);
                RestClientManager.getInstance().removeProductFromStore(storeProduct.getID(), new RequestListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        // Remove the item from the adapter list
                        mItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mItems.size());
                    }

                    @Override
                    public void onInternalServerFailure(ErrorResponse error)
                    {

                    }

                    @Override
                    public void onNetworkFailure(RetrofitError error)
                    {

                    }
                });
            }
        });
    }


    @Override
    public void onBindViewHolder(MyInventoryItemViewHolder holder, final int position)
    {
        final StoreProduct storeProduct = mItems.get(position);
        holder.nameTextView.setText(storeProduct.getName());
        holder.brandTextView.setText(storeProduct.getBrand());
        handleContent(storeProduct, holder);
        holder.priceTextView.setText(String.format(mContext.getString(R.string.price_value), Utils.formatDouble(storeProduct.getPrice())));
        UIManager.getInstance().loadImage(mContext, storeProduct.getPhotoGallery().getOriginal(), holder.itemImageView, ImageType.PRODUCT);
        setActionsLogic(holder, position);
    }

    private void handleContent(StoreProduct storeProduct, MyInventoryItemViewHolder holder)
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

    private void setActionsLogic(MyInventoryItemViewHolder holder, final int position)
    {
        final StoreProduct storeProduct = mItems.get(position);
        holder.actionLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RestClientManager.getInstance().removeProductFromStore(storeProduct.getID(), new RequestListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        // Remove the item from the adapter list
                        mItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mItems.size());
                    }

                    @Override
                    public void onInternalServerFailure(ErrorResponse error)
                    {

                    }

                    @Override
                    public void onNetworkFailure(RetrofitError error)
                    {

                    }
                });
            }
        });

        holder.itemView.setTag(storeProduct);
    }

    @Override
    public int getItemCount()
    {
        return mItems.size();
    }
}
