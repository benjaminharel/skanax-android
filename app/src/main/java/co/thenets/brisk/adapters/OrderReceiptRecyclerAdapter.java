package co.thenets.brisk.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.OrderReceiptItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Order;
import co.thenets.brisk.models.OrderItem;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class OrderReceiptRecyclerAdapter extends RecyclerView.Adapter<OrderReceiptItemViewHolder>
{
    private Context mContext;
    private ArrayList<OrderItem> mOrderItemsList;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    public OrderReceiptRecyclerAdapter(Context context, Order order)
    {
        mContext = context;
        mOrderItemsList = order.getOrderItemList();
    }

    @Override
    public OrderReceiptItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_receipt_list_item_layout, parent, false);
        return new OrderReceiptItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderReceiptItemViewHolder holder, final int position)
    {
        OrderItem orderItem = mOrderItemsList.get(position);
        holder.nameTextView.setText(orderItem.getName());
        holder.brandTextView.setText(orderItem.getBrand());
        holder.quantityTextView.setText(String.valueOf(Utils.formatDouble(orderItem.getAmount())));

        holder.priceTextView.setText(String.format(mContext.getString(R.string.price_value), orderItem.getPrice()));
        UIManager.getInstance().loadImage(mContext, orderItem.getPhotoGallery().getSmall(), holder.itemImageView, ImageType.PRODUCT);
        holder.itemView.setTag(orderItem);
        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);
    }

    @Override
    public int getItemCount()
    {
        return mOrderItemsList.size();
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
