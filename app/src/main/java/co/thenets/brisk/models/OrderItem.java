package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

import co.thenets.brisk.general.Params;

/**
 * Created by DAVID-WORK on 07/01/2016.
 */

@Parcel
public class OrderItem implements Serializable
{
    @SerializedName("id")
    private String mID;

    @SerializedName("store_product_id")
    private String mStoreProductID;

    @SerializedName("amount")
    private double mAmount;

    @SerializedName("name")
    private String mName;

    @SerializedName("content")
    private String mContent;

    @SerializedName("brand")
    private String mBrand;

    @SerializedName("price")
    private float mPrice;

    @SerializedName("photos")
    protected PhotoGallery mPhotoGallery;

    public void setStoreProductID(String storeProductID)
    {
        mStoreProductID = storeProductID;
    }

    public void setAmount(int amount)
    {
        mAmount = amount;
    }

    public String getID()
    {
        return mID;
    }

    public String getStoreProductID()
    {
        return mStoreProductID;
    }

    public double getAmount()
    {
        return mAmount;
    }

    public String getName()
    {
        return mName;
    }

    public String getBrand()
    {
        return mBrand;
    }

    public String getContent()
    {
        return mContent;
    }

    public String getPrice()
    {
        return String.format(Params.CURRENCY_FORMAT, mPrice);
    }

    public PhotoGallery getPhotoGallery()
    {
        if(mPhotoGallery == null)
        {
            mPhotoGallery = new PhotoGallery();
        }
        return mPhotoGallery;
    }

    @Override
    public String toString()
    {
        return "OrderItem{" +
                "mID='" + mID + '\'' +
                ", mStoreProductID='" + mStoreProductID + '\'' +
                ", mAmount=" + mAmount +
                ", mName='" + mName + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mBrand='" + mBrand + '\'' +
                ", mPrice=" + mPrice +
                ", mPhotoGallery=" + mPhotoGallery +
                '}';
    }
}
