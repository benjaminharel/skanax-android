package co.thenets.brisk.gcm;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.OrderPushData;

/**
 * Created by DAVID BELOOSESKY on 03/12/2014 - 17:15.
 */
public class OrderPushPayload extends BasePushPayload
{
    @SerializedName("data")
    private OrderPushData mData;

    public OrderPushData getData()
    {
        return mData;
    }

    @Override
    public String toString()
    {
        return "OrderPushPayload{" +
                "mData=" + mData +
                "} " + super.toString();
    }
}
