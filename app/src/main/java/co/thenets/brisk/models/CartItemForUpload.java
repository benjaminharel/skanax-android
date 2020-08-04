package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by DAVID-WORK on 17/12/2015.
 */

@Parcel
public class CartItemForUpload
{
    @SerializedName("active_product_id")
    private String mActiveProductID;

    @SerializedName("amount")
    private int mAmount;

    public CartItemForUpload()
    {
    }

    public CartItemForUpload(String activeProductID, int amount)
    {
        mActiveProductID = activeProductID;
        mAmount = amount;
    }

    @Override
    public String toString()
    {
        return "CartListItemForUpload{" +
                "mActiveProductID='" + mActiveProductID + '\'' +
                ", mAmount=" + mAmount +
                '}';
    }
}
