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
import co.thenets.brisk.custom.OrderItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.RoleType;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Order;
import co.thenets.brisk.models.OrderItem;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderItemViewHolder>
{
    private Context mContext;
    private ArrayList<OrderItem> mOrderItemsList;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;
    private final RoleType mRoleType;

    public OrderRecyclerAdapter(Context context, Order order, RoleType roleType)
    {
        mContext = context;
        mRoleType = roleType;
        setData(order);
    }

    private void setData(Order order)
    {
        mOrderItemsList = order.getOrderItemList();

        // remove items with zero amount only if it's in a Store view
        if(mRoleType == RoleType.STORE)
        {
            ArrayList<OrderItem> orderItemsList = new ArrayList<>();
            for (OrderItem orderItem : mOrderItemsList)
            {
                if(orderItem.getAmount() > 0)
                {
                    orderItemsList.add(orderItem);
                }
            }
            mOrderItemsList = orderItemsList;
        }
    }

    @Override
    public OrderItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item_layout, parent, false);
        return new OrderItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderItemViewHolder holder, final int position)
    {
        OrderItem orderItem = mOrderItemsList.get(position);
        holder.nameTextView.setText(orderItem.getName());
        handleContent(orderItem, holder);
        holder.brandTextView.setText(orderItem.getBrand());
        holder.quantityTextView.setText(String.valueOf(Utils.formatDouble(orderItem.getAmount())));
        holder.priceTextView.setText(String.format(mContext.getString(R.string.price_value), orderItem.getPrice()));
        setNotAvailableCoverVisibility(holder, orderItem);
        UIManager.getInstance().loadImage(mContext, orderItem.getPhotoGallery().getSmall(), holder.itemImageView, ImageType.PRODUCT);
        holder.itemView.setTag(orderItem);
        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);
    }

    private void handleContent(OrderItem orderItem, OrderItemViewHolder holder)
    {
        if(TextUtils.isEmpty(orderItem.getContent()))
        {
            holder.contentTextView.setVisibility(View.GONE);
        }
        else
        {
            holder.contentTextView.setVisibility(View.VISIBLE);
            holder.contentTextView.setText(orderItem.getContent());
        }
    }

    private void setNotAvailableCoverVisibility(OrderItemViewHolder holder, OrderItem orderItem)
    {
        if(orderItem.getAmount() > 0)
        {
            holder.notAvailableCover.setVisibility(View.GONE);
        }
        else
        {
            holder.notAvailableCover.setVisibility(View.VISIBLE);
        }
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

    public void removeAt(int position)
    {
        mOrderItemsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mOrderItemsList.size());
    }
}
