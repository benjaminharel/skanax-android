package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

import co.thenets.brisk.enums.OrderState;

/**
 * Created by DAVID-WORK on 14/01/2016.
 */

@Parcel
public class OrderPushData implements Serializable
{
    @SerializedName("order_id")
    private String mOrderID;

    @SerializedName("state")
    private OrderState mState;

    public String getOrderID()
    {
        return mOrderID;
    }

    public OrderState getState()
    {
        return mState;
    }
}
