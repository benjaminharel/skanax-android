package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

import co.thenets.brisk.enums.OrderState;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;

/**
 * Created by DAVID-WORK on 07/01/2016.
 */

@Parcel
public class Order implements Serializable
{
    @SerializedName("id")
    private String mID;

    @SerializedName("order_number")
    private String mOrderNumber;

    @SerializedName("products_price")
    private float mProductsSubtotal;

    @SerializedName("delivery_price")
    private float mDeliveryPrice;

    @SerializedName("state")
    private OrderState mState;

    @SerializedName("tip")
    private float mTip;

    @SerializedName("eta")
    private int mEta;

    @SerializedName("store")
    private Store mStore;

    @SerializedName("customer")
    private Customer mCustomer;

    @SerializedName("address")
    private BriskAddress mAddress;

    @SerializedName("order_items")
    private ArrayList<OrderItem> mOrderItemList;

    @SerializedName("payment_method")
    private PaymentMethod mPaymentMethod;

    @SerializedName("created_at")
    private long mEpochDate;

    public String getID()
    {
        return mID;
    }

    public float getTotalPrice()
    {
        return mProductsSubtotal + mDeliveryPrice + mTip;
    }

    public String getTotalPriceAsString()
    {
        return String.format(Params.CURRENCY_FORMAT, Utils.roundDouble(mProductsSubtotal + mDeliveryPrice + mTip));
    }

    public String getProductsSubtotal()
    {
        return String.format(Params.CURRENCY_FORMAT, Utils.roundDouble(mProductsSubtotal));
    }

    public String getDeliveryPrice()
    {
        return String.format(Params.CURRENCY_FORMAT, Utils.roundDouble(mDeliveryPrice));
    }

    public String getTip()
    {
        return String.format(Params.CURRENCY_FORMAT, Utils.roundDouble(mTip));
    }

    public OrderState getState()
    {
        return mState;
    }

    public void setStore(Store store)
    {
        mStore = store;
    }

    public Store getStore()
    {
        return mStore;
    }

    public Customer getCustomer()
    {
        return mCustomer;
    }

    public ArrayList<OrderItem> getOrderItemList()
    {
        return mOrderItemList;
    }

    public long getEpochDate()
    {
        return mEpochDate;
    }

    public void setState(OrderState state)
    {
        mState = state;
    }

    public String getOrderNumber()
    {
        return mOrderNumber;
    }

    public PaymentMethod getPaymentMethod()
    {
        return mPaymentMethod;
    }

    public int getEta()
    {
        // don't return zero eta, at least 1 min
        return mEta == 0 ? 1 : mEta;
    }

    // remove some order item from the order list
    public void removeOrderItem(String orderItemID)
    {
        for (int i = 0; i < mOrderItemList.size(); i++)
        {
            if (mOrderItemList.get(i).getID().equals(orderItemID))
            {
                mOrderItemList.remove(i);
            }
        }
    }

    // get how many items are with amount == 0
    public int getMissingItemsCounter()
    {
        int missingItemsCounter = 0;
        for (OrderItem orderItem : mOrderItemList)
        {
            if (orderItem.getAmount() == 0)
            {
                missingItemsCounter++;
            }
        }

        return missingItemsCounter;
    }

    public BriskAddress getAddress()
    {
        return mAddress;
    }

    @Override
    public String toString()
    {
        return "Order{" +
                "mID='" + mID + '\'' +
                ", mProductsSubtotal=" + mProductsSubtotal +
                ", mDeliveryPrice=" + mDeliveryPrice +
                ", mTip=" + mTip +
                ", mState=" + mState +
                ", mStore=" + mStore +
                ", mCustomer=" + mCustomer +
                ", mOrderItemList=" + mOrderItemList +
                ", mEpochDate=" + mEpochDate +
                '}';
    }
}
