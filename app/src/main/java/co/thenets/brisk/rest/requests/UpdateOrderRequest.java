package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.Tip;

/**
 * Created by Roei on 08/11/2015.
 */
public class UpdateOrderRequest
{
    // Device data for upload like push notification etc
    @SerializedName("order")
    private Tip mTip;

    public UpdateOrderRequest(Tip tip)
    {
        mTip = tip;
    }

    @Override
    public String toString()
    {
        return "UpdateOrderRequest{" +
                "mTip=" + mTip +
                '}';
    }
}
