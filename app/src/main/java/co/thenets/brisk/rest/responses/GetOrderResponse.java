package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import co.thenets.brisk.models.Order;

/**
 * Created by DAVID-WORK on 07/01/2015.
 */

@Parcel
public class GetOrderResponse extends BaseResponse
{
    @SerializedName("order")
    private Order mOrder;

    public Order getOrder()
    {
        return mOrder;
    }

    @Override
    public String toString()
    {
        return "GetOrderResponse{" +
                "mOrder=" + mOrder +
                "} " + super.toString();
    }
}
