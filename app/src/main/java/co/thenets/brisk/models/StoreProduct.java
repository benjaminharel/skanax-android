package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 15/11/2015.
 */
@Parcel
public class StoreProduct extends Product implements Serializable
{
    public StoreProduct()
    {
    }

    public StoreProduct(Product product)
    {
        super(product);
    }

    @SerializedName("price")
    private double mPrice;

    @SerializedName("sold")
    private double mSoldAmount;

    @SerializedName("product_id")
    private String mProductID;

    @Override
    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public void setBrand(String brand)
    {
        mBrand = brand;
    }

    public void setImageLink(String imageLink)
    {
        mImageLink = imageLink;
    }

    public int getCategoryCode()
    {
        return mCategoryCode;
    }

    public void setCategoryCode(int category)
    {
        mCategoryCode = category;
    }

    public int getSubCategoryCode()
    {
        return mSubCategoryCode;
    }

    public void setSubCategoryCode(int subCategory)
    {
        mSubCategoryCode = subCategory;
    }

    public double getPrice()
    {
        return mPrice;
    }

    public void setPrice(double price)
    {
        mPrice = price;
    }

    public String getBrand()
    {
        return mBrand;
    }

    public String getProductID()
    {
        return mProductID;
    }

    public double getSoldAmount()
    {
        return mSoldAmount;
    }



    @Override
    public String toString()
    {
        return "StoreProduct{" +
                "mPrice=" + mPrice +
                ", mSoldAmount=" + mSoldAmount +
                ", mProductID='" + mProductID + '\'' +
                "} " + super.toString();
    }
}
