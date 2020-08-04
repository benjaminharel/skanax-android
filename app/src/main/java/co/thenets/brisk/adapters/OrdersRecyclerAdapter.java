package co.thenets.brisk.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.CustomerOrderActivity;
import co.thenets.brisk.activities.ReceiptActivity;
import co.thenets.brisk.activities.StoreOrderActivity;
import co.thenets.brisk.custom.OrderListItemViewHolder;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.models.Order;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class OrdersRecyclerAdapter extends RecyclerView.Adapter<OrderListItemViewHolder>
{
    private ArrayList<Order> mOrderList = new ArrayList<>();
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;

    public OrdersRecyclerAdapter(Context context)
    {
        mContext = context;
        mOrderList = ContentManager.getInstance().getOrderList();
    }

    @Override
    public OrderListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
        return new OrderListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderListItemViewHolder holder, final int position)
    {
        final Order order = mOrderList.get(position);
        if (ContentManager.getInstance().isAppInSellerMode())
        {
            // In seller mode, the seller wants to see the customers details
            holder.nameTextView.setText(order.getCustomer().getName());
        }
        else
        {
            // In customer mode, the customer wants to see the sellers details
            holder.nameTextView.setText(order.getStore().getName());
        }

        holder.dateTextView.setText(Utils.getDateAndTimeFromEpoch(order.getEpochDate()));
        holder.priceTextView.setText(String.format(mContext.getString(R.string.total_price), String.valueOf(order.getTotalPriceAsString())));

        switch (order.getState())
        {
            case CLOSED:
                holder.nameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray));
                holder.dateTextView.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray));
                holder.priceTextView.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray));
                holder.itemImageView.setVisibility(View.INVISIBLE);
                holder.itemCanceledTextView.setVisibility(View.INVISIBLE);
                break;
            case CANCELED:
                holder.nameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray));
                holder.dateTextView.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray));
                holder.priceTextView.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray));
                holder.itemImageView.setVisibility(View.INVISIBLE);
                holder.itemCanceledTextView.setVisibility(View.VISIBLE);
                break;
            case DISPUTE:
                holder.nameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                holder.dateTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                holder.priceTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                holder.itemImageView.setVisibility(View.VISIBLE);
                holder.itemCanceledTextView.setVisibility(View.INVISIBLE);
                holder.itemImageView.setImageResource(R.drawable.ic_dispute);
            default:
                // Order that is still active
                holder.nameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                holder.dateTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                holder.priceTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                holder.itemImageView.setVisibility(View.VISIBLE);
                holder.itemCanceledTextView.setVisibility(View.INVISIBLE);
                break;
        }

        holder.itemView.setTag(order);
        holder.itemContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = null;
                switch (order.getState())
                {
                    case CLOSED:
                    case CANCELED:
                        intent = new Intent(mContext, ReceiptActivity.class);
                        intent.putExtra(Params.ORDER, order);
                        break;
                    default:
                        if(ContentManager.getInstance().isAppInSellerMode())
                        {
                            intent = new Intent(mContext, StoreOrderActivity.class);
                        }
                        else
                        {
                            intent = new Intent(mContext, CustomerOrderActivity.class);
                        }
                }
                intent.putExtra(Params.ORDER_ID, order.getID());
                mContext.startActivity(intent);
            }
        });

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);
    }

    @Override
    public int getItemCount()
    {
        return mOrderList.size();
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
