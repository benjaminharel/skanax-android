package co.thenets.brisk.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.EditProfileActivity;
import co.thenets.brisk.activities.GetAddressAndPaymentActivity;
import co.thenets.brisk.activities.RegisterActivity;
import co.thenets.brisk.activities.StoreActivity;
import co.thenets.brisk.custom.CartListItemViewHolder;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.events.CartItemsRemovedEvent;
import co.thenets.brisk.events.CartUpdatedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.CartActionsListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.OrderForUpload;
import co.thenets.brisk.models.OrderItemForUpload;
import co.thenets.brisk.models.Store;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class CartAdapter extends RecyclerView.Adapter<CartListItemViewHolder>
{
    private Context mContext;
    // Allows to remember the last item shown on screen
    private int mLastPosition = -1;
    private String[] mStoreIDs;
    private HashMap<String, HashMap<String, Integer>> mCartMap;

    public CartAdapter(Context context)
    {
        mContext = context;
        mCartMap = ContentManager.getInstance().getCart();
        mStoreIDs = mCartMap.keySet().toArray(new String[mCartMap.size()]);
    }

    @Override
    public CartListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item, parent, false);
        return new CartListItemViewHolder(view, new CartActionsListener()
        {
            @Override
            public void onStorePressed(int position)
            {
                String storeID = mStoreIDs[position];
                Intent intent = new Intent(mContext, StoreActivity.class);
                intent.putExtra(Params.STORE_ID, storeID);
                mContext.startActivity(intent);
            }

            @Override
            public void onOrderPressed(int position)
            {
                orderFromThisStore(view, position);
            }
        });
    }


    @Override
    public void onBindViewHolder(CartListItemViewHolder holder, final int position)
    {
        String storeID = mStoreIDs[position];
        HashMap<String, Integer> productIDsAndAmount = mCartMap.get(storeID);

        Store store = ContentManager.getInstance().getStore(storeID);

        UIManager.getInstance().loadImage(mContext, store.getPhotoGallery().getMedium(), holder.imageView, ImageType.STORE);
        holder.storeNameTextView.setText(store.getName());
        holder.ratingBar.setRating(store.getRating());
        holder.reviewsCounter.setText(String.format(mContext.getString(R.string.number_of_reviews), store.getReviewsCounter()));

        holder.productsPriceTextView.setText(String.format(mContext.getString(R.string.price_value), String.format(Params.CURRENCY_FORMAT, Utils.roundDouble(ContentManager.getInstance().getProductsPrice(storeID)))));
        holder.deliveryPriceTextView.setText(String.format(mContext.getString(R.string.price_value), String.format(Params.CURRENCY_FORMAT, Utils.roundDouble(store.getDeliveryPrice()))));
        double totalPrice = ContentManager.getInstance().getProductsPrice(storeID) + store.getDeliveryPrice();
        holder.totalPriceTextView.setText(String.format(mContext.getString(R.string.price_value), String.format(Params.CURRENCY_FORMAT, Utils.roundDouble(totalPrice))));

        setProductsRecyclerView(holder, store, productIDsAndAmount);

        holder.itemView.setTag(store);

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemContainer, position);
    }


    private void setProductsRecyclerView(final CartListItemViewHolder holder, Store store, HashMap<String, Integer> productIDsAndAmount)
    {
        holder.productsRecyclerView.setHasFixedSize(true);
        holder.productsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        holder.productsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        StoreCartItemsRecyclerAdapter storeCartItemsRecyclerAdapter = new StoreCartItemsRecyclerAdapter(mContext, store, productIDsAndAmount);
        holder.productsRecyclerView.setAdapter(storeCartItemsRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                ((StoreCartItemsRecyclerAdapter) holder.productsRecyclerView.getAdapter()).removeAt(viewHolder.getAdapterPosition(), true);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(holder.productsRecyclerView);
    }


    @Override
    public int getItemCount()
    {
        return mStoreIDs.length;
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

    public void itemUpdated(CartUpdatedEvent cartUpdatedEvent)
    {
        int position = 0;

        for (String storeID : mStoreIDs)
        {
            if (storeID.equals(cartUpdatedEvent.getStoreID()))
            {
                notifyItemChanged(position);
                break;
            }

            position++;
        }
    }
    public void itemRemoved(CartItemsRemovedEvent cartItemsRemovedEvent)
    {
        int position = 0;

        for (String storeID : mStoreIDs)
        {
            if (storeID.equals(cartItemsRemovedEvent.getStoreID()))
            {
                mCartMap = ContentManager.getInstance().getCart();
                mStoreIDs = mCartMap.keySet().toArray(new String[mCartMap.size()]);
                notifyItemRemoved(position);
                break;
            }

            position++;
        }
    }


    private void orderFromThisStore(View view, int position)
    {
        String storeID = mStoreIDs[position];
        Store store = ContentManager.getInstance().getStore(storeID);
        double productsPrice = ContentManager.getInstance().getProductsPrice(storeID);

        if (ContentManager.getInstance().isUserRegistered())
        {
            if (ContentManager.getInstance().isUserCompletelyRegistered())
            {
                if ((int) productsPrice >= store.getMinimumOrderPrice())
                {
                    // Save the current store
                    ContentManager.getInstance().setSelectedCart(storeID);

                    // Save orderForUpload for later use
                    ContentManager.getInstance().setOrderForUpload(initOrderForUpload(storeID));
                    mContext.startActivity(new Intent(mContext, GetAddressAndPaymentActivity.class));
                }
                else
                {
                    String minPriceString = String.format(mContext.getString(R.string.min_price_error), String.valueOf(store.getMinimumOrderPrice()));
                    UIManager.getInstance().displaySnackBarError(view, minPriceString, Snackbar.LENGTH_LONG);
                }


            }
            else
            {
                Snackbar.make(view, mContext.getString(R.string.edit_your_profile_and_add_first_and_last_name), Snackbar.LENGTH_LONG)
                        .setAction(mContext.getString(R.string.update), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(mContext, EditProfileActivity.class);
                                mContext.startActivity(intent);
                            }
                        }).show();
            }
        }
        else
        {
            Snackbar.make(view, mContext.getString(R.string.please_register_before_order), Snackbar.LENGTH_LONG)
                    .setAction(mContext.getString(R.string.register), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent mIntent = new Intent(mContext, RegisterActivity.class);
                            mContext.startActivity(mIntent);
                        }
                    }).show();
        }
    }

    private OrderForUpload initOrderForUpload(String storeID)
    {
        OrderForUpload orderForUpload = new OrderForUpload();
        orderForUpload.setCustomerID(ContentManager.getInstance().getUser().getCustomerID());
        orderForUpload.setStoreID(storeID);

        ArrayList<OrderItemForUpload> orderItems = new ArrayList<>();
        HashMap<String, Integer> cartItems = ContentManager.getInstance().getSelectedCart();
        for (Map.Entry<String, Integer> entry : cartItems.entrySet())
        {
            String storeProductID = entry.getKey();
            int amount = entry.getValue();

            OrderItemForUpload orderItem = new OrderItemForUpload();
            orderItem.setStoreProductID(storeProductID);
            orderItem.setAmount(amount);
            orderItems.add(orderItem);
        }

        orderForUpload.setOrderItemList(orderItems);
        return orderForUpload;
    }
}
