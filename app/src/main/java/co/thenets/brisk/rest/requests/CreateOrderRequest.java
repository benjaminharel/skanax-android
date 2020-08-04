package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.OrderForUpload;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class CreateOrderRequest
{
    @SerializedName("order")
    private OrderForUpload mOrderForUpload;

    public CreateOrderRequest(OrderForUpload orderForUpload)
    {
        mOrderForUpload = orderForUpload;
    }

    public OrderForUpload getOrderForUpload()
    {
        return mOrderForUpload;
    }

    @Override
    public String toString()
    {
        return "CreateOrderRequest{" +
                "mOrderForUpload=" + mOrderForUpload +
                '}';
    }
}
