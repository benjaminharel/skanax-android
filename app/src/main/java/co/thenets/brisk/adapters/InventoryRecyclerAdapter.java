package co.thenets.brisk.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.InventoryItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.OnProductClickListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Product;
import co.thenets.brisk.models.StoreProduct;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class InventoryRecyclerAdapter extends RecyclerView.Adapter<InventoryItemViewHolder>
{
    private ArrayList<Product> mItems;
    private Context mContext;

    public InventoryRecyclerAdapter(Context context)
    {
        mContext = context;
        mItems = ContentManager.getInstance().getProductList();
    }

    @Override
    public InventoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item_layout, parent, false);
        return new InventoryItemViewHolder(v, new OnProductClickListener()
        {
            @Override
            public void onProductClicked(int position)
            {
                // TODO: Handle this!
//                StoreProduct storeProduct = new StoreProduct(mItems.get(position));
//                ProductDetailsDialog detailsDialog = ProductDetailsDialog.newInstance(storeProduct, ProductView.INVENTORY_VIEW);
//                detailsDialog.show(((Activity)mContext).getFragmentManager(), "detailsDialog");
            }

            @Override
            public void onActionClicked(int position)
            {
                // Handled in "setActionsLogic"
            }
        });
    }


    @Override
    public void onBindViewHolder(InventoryItemViewHolder holder, final int position)
    {
        final Product product = mItems.get(position);
        holder.nameTextView.setText(product.getName());
        holder.brandTextView.setText(product.getBrand());
        holder.priceTextView.setText(Utils.formatDouble(product.getMarketPrice()));
        UIManager.getInstance().loadImage(mContext, product.getPhotoGallery().getSmall(), holder.itemImageView, ImageType.PRODUCT);
        setActionsLogic(holder, position);
    }

    private void setActionsLogic(InventoryItemViewHolder holder, final int position)
    {
        final Product product = mItems.get(position);

        if(ContentManager.getInstance().isProductExistsInMyStoreProducts(product.getID()))
        {
            holder.checkBox.setChecked(true);
            holder.checkBox.setEnabled(false);
        }
        else
        {
            holder.checkBox.setChecked(false);
            holder.checkBox.setEnabled(true);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    StoreProduct storeProduct = new StoreProduct(product);
                    storeProduct.setPrice(product.getMarketPrice());
                    RestClientManager.getInstance().addNewProductToStore(storeProduct, new RequestListener()
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
            }
        });
        holder.itemView.setTag(product);
    }

    @Override
    public int getItemCount()
    {
        return mItems.size();
    }
}
