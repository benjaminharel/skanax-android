package co.thenets.brisk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.ShoppingListItemViewHolder;
import co.thenets.brisk.interfaces.OnShoppingListItemClickListener;
import co.thenets.brisk.managers.ContentManager;

/**
 * Created by DAVID-WORK on 16/06/2016.
 */

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    //our items
    List<ShoppingListItemViewHolder> items = new ArrayList<>();
    //headers
    List<View> headers = new ArrayList<>();
    //footers
    List<View> footers = new ArrayList<>();

    public static final int TYPE_HEADER = 111;
    public static final int TYPE_FOOTER = 222;
    public static final int TYPE_ITEM = 333;
    private HashMap<String, HashMap<String, Integer>> mShoppingCart;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type)
    {
        mShoppingCart = ContentManager.getInstance().getCart();

        //if our position is one of our items (this comes from getItemViewType(int position) below)
        if (type == TYPE_ITEM)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shopping_list_item_layout, viewGroup, false);
            return new ShoppingListItemViewHolder(view, new OnShoppingListItemClickListener()
            {
                @Override
                public void onProductClicked(int position)
                {

                }

                @Override
                public void onAddClicked(int position)
                {

                }

                @Override
                public void onRemoveClicked(int position)
                {

                }
            });
            //else we have a header/footer
        }
        else
        {
            //create a new framelayout, or inflate from a resource
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            //make sure it fills the space
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return new HeaderFooterViewHolder(frameLayout);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position)
    {
        //check what type of view our position is
        if (position < headers.size())
        {
            View v = headers.get(position);
            //add our view to a header view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
        }
        else if (position >= headers.size() + items.size())
        {
            View v = footers.get(position - items.size() - headers.size());
            //add oru view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
        }
        else
        {
            //it's one of our items, display as required
            prepareGeneric((ShoppingListItemViewHolder) vh, position - headers.size());
        }
    }

    @Override
    public int getItemCount()
    {
        //make sure the adapter knows to look for all our items, headers, and footers
        return headers.size() + items.size() + footers.size();
    }

    private void prepareHeaderFooter(HeaderFooterViewHolder vh, View view)
    {
        //empty out our FrameLayout and replace with our header/footer
        vh.base.removeAllViews();
        vh.base.addView(view);
    }

    private void prepareGeneric(ShoppingListItemViewHolder holder, int position)
    {
//        mShoppingCart.get(position).;
//        //do whatever we need to for our other type
//        final StoreProduct storeProduct = mShoppingItemList.get(position).getStoreProduct();
//        holder.nameTextView.setText(storeProduct.getName());
//        holder.brandTextView.setText(storeProduct.getBrand());
//        holder.priceTextView.setText(String.format(mContext.getString(R.string.min_price), Params.CURRENCY, Utils.formatDouble(storeProduct.getPrice())));
//        UIManager.getInstance().loadImage(mContext, storeProduct.getImageLink(), holder.itemImageView, ImageType.PRODUCT);
//        holder.quantityTextView.setText(String.valueOf(mShoppingItemList.get(position).getAmount()));
//        holder.itemView.setTag(storeProduct);
//
//        // Here you apply the animation when the view is bound
//        setAnimation(holder.itemContainer, position);
    }

    @Override
    public int getItemViewType(int position)
    {
        //check what type our position is, based on the assumption that the order is headers > items > footers
        if (position < headers.size())
        {
            return TYPE_HEADER;
        }
        else if (position >= headers.size() + items.size())
        {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    //add a header to the adapter
    public void addHeader(View header)
    {
        if (!headers.contains(header))
        {
            headers.add(header);
            //animate
            notifyItemInserted(headers.size() - 1);
        }
    }

    //remove a header from the adapter
    public void removeHeader(View header)
    {
        if (headers.contains(header))
        {
            //animate
            notifyItemRemoved(headers.indexOf(header));
            headers.remove(header);
        }
    }

    //add a footer to the adapter
    public void addFooter(View footer)
    {
        if (!footers.contains(footer))
        {
            footers.add(footer);
            //animate
            notifyItemInserted(headers.size() + items.size() + footers.size() - 1);
        }
    }

    //remove a footer from the adapter
    public void removeFooter(View footer)
    {
        if (footers.contains(footer))
        {
            //animate
            notifyItemRemoved(headers.size() + items.size() + footers.indexOf(footer));
            footers.remove(footer);
        }
    }

    //our header/footer RecyclerView.ViewHolder is just a FrameLayout
    public static class HeaderFooterViewHolder extends RecyclerView.ViewHolder
    {
        FrameLayout base;

        public HeaderFooterViewHolder(View itemView)
        {
            super(itemView);
            this.base = (FrameLayout) itemView;
        }
    }
}
