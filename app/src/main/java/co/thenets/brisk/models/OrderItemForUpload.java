package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 07/01/2016.
 */

@Parcel
public class OrderItemForUpload implements Serializable
{
    @SerializedName("store_product_id")
    private String mStoreProductID;

    @SerializedName("amount")
    private int mAmount;

    public void setStoreProductID(String storeProductID)
    {
        mStoreProductID = storeProductID;
    }

    public void setAmount(int amount)
    {
        mAmount = amount;
    }

    @Override
    public String toString()
    {
        return "OrderItemForUpload{" +
                "mStoreProductID='" + mStoreProductID + '\'' +
                ", mAmount=" + mAmount +
                '}';
    }
}
