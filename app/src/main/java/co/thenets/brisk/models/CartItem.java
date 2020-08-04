package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 17/12/2015.
 */

@Parcel
public class CartItem implements Serializable
{
    @SerializedName("amount")
    private int mAmount;

    @SerializedName("available")
    private boolean mAvailable;

    @SerializedName("store_product")
    private StoreProduct mStoreProduct;

    public int getAmount()
    {
        return mAmount;
    }

    public boolean isAvailable()
    {
        return mAvailable;
    }

    public StoreProduct getStoreProduct()
    {
        return mStoreProduct;
    }

    public float getTotalPrice()
    {
        return (float) (mAmount * mStoreProduct.getPrice());
    }


    @Override
    public String toString()
    {
        return "CartItem{" +
                "mAmount=" + mAmount +
                ", mAvailable=" + mAvailable +
                ", mStoreProduct=" + mStoreProduct +
                '}';
    }
}
