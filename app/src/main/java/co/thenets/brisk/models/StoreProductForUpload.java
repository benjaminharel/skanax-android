package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 15/11/2015.
 */
@Parcel
public class StoreProductForUpload implements Serializable
{
    @SerializedName("product_id")
    protected String mID;

    @SerializedName("price")
    private double mPrice;

    public void setID(String ID)
    {
        mID = ID;
    }

    public void setPrice(double price)
    {
        mPrice = price;
    }

    @Override
    public String toString()
    {
        return "StoreProduct{" +
                "mID='" + mID + '\'' +
                ", mPrice=" + mPrice +
                '}';
    }
}
