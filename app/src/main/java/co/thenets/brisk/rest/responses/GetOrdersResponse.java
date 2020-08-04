package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import co.thenets.brisk.models.Order;

/**
 * Created by DAVID-WORK on 07/01/2015.
 */

@Parcel
public class GetOrdersResponse extends BaseResponse
{
    @SerializedName("orders")
    private ArrayList<Order> mOrderList;

    public ArrayList<Order> getOrderList()
    {
        return mOrderList;
    }

    @Override
    public String toString()
    {
        return "GetOrdersResponse{" +
                "mOrderList=" + mOrderList +
                "} " + super.toString();
    }
}
