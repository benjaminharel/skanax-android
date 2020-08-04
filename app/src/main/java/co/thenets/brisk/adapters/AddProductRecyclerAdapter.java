package co.thenets.brisk.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.AddProductItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.interfaces.OnProductSelectedListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Product;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class AddProductRecyclerAdapter extends RecyclerView.Adapter<AddProductItemViewHolder>
{
    private ArrayList<Product> mItems;
    private Context mContext;
    private OnProductSelectedListener mOnProductSelectedListener;


    public AddProductRecyclerAdapter(Context context, ArrayList<Product> items, OnProductSelectedListener onProductSelectedListener)
    {
        mContext = context;
        mItems = items;
        mOnProductSelectedListener = onProductSelectedListener;
    }

    @Override
    public AddProductItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
        return new AddProductItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AddProductItemViewHolder holder, final int position)
    {
        final Product product = mItems.get(position);
        holder.nameTextView.setText(product.getName());
        holder.brandTextView.setText(product.getBrand());

        UIManager.getInstance().loadImage(mContext, product.getPhotoGallery().getSmall(), holder.itemImageView, ImageType.PRODUCT);


        if (ContentManager.getInstance().isProductExistsInMyStoreProducts(product.getID()))
        {
            holder.disableLayer.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.disableLayer.setVisibility(View.GONE);
        }

        holder.itemContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContentManager.getInstance().isProductExistsInMyStoreProducts(product.getID()))
                {
                    UIManager.getInstance().hideKeyboard(v, (Activity) mContext);
                    UIManager.getInstance().displaySnackBarError(v, mContext.getString(R.string.product_already_exists_in_your_store), Snackbar.LENGTH_LONG);
                }
                else
                {
                    mOnProductSelectedListener.onProductSelected(product);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mItems.size();
    }
}
