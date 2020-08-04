package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by DAVID-WORK on 15/11/2015.
 */

@Parcel
public class Product implements Serializable
{
    public Product()
    {
    }

    public Product(Product product)
    {
        mID = product.getID();
        mName = product.getName();
        mBrand = product.getBrand();
        mBarcode = product.getBarcode();
        mPhotoGallery = product.getPhotoGallery();
        mMarketPrice = product.getMarketPrice();
        mHashtagList = product.mHashtagList;
        mContent  = product.getContent();
        mNotes = product.getNotes();
        mCategoryCode = product.getCategoryCode();
        mSubCategoryCode = product.getSubCategoryCode();
        mWeight = product.getWeight();
    }

    @SerializedName("id")
    protected String mID;

    @SerializedName("name")
    protected String mName;

    @SerializedName("brand")
    protected String mBrand;

    @SerializedName("barcode")
    protected String mBarcode;

    @SerializedName("photo")
    protected String mImageLink;

    @SerializedName("photos")
    protected PhotoGallery mPhotoGallery;

    @SerializedName("market_price")
    protected double mMarketPrice;

    @SerializedName("hash_tags")
    protected ArrayList<String> mHashtagList = new ArrayList<>();

    @SerializedName("category")
    protected int mCategoryCode;

    @SerializedName("sub_category")
    protected int mSubCategoryCode;

    @SerializedName("weight")
    protected int mWeight;

    @SerializedName("notes")
    protected String mNotes;

    @SerializedName("content")
    protected String mContent;

    public void setID(String ID)
    {
        mID = ID;
    }

    public String getID()
    {
        return mID;
    }

    public String getName()
    {
        return mName;
    }

    public String getBrand()
    {
        return mBrand;
    }

    public String getBarcode()
    {
        return mBarcode;
    }

    public PhotoGallery getPhotoGallery()
    {
        if(mPhotoGallery == null)
        {
            mPhotoGallery = new PhotoGallery();
        }
        return mPhotoGallery;
    }

    public double getMarketPrice()
    {
        return mMarketPrice;
    }

    public ArrayList<String> getHashtagList()
    {
        return mHashtagList;
    }

    public int getCategoryCode()
    {
        return mCategoryCode;
    }

    public void setCategoryCode(int categoryCode)
    {
        mCategoryCode = categoryCode;
    }

    public int getSubCategoryCode()
    {
        return mSubCategoryCode;
    }

    public void setSubCategoryCode(int subCategoryCode)
    {
        mSubCategoryCode = subCategoryCode;
    }

    public int getWeight()
    {
        return mWeight;
    }

    public void setWeight(int weight)
    {
        mWeight = weight;
    }

    public String getNotes()
    {
        return mNotes;
    }

    public void setNotes(String notes)
    {
        mNotes = notes;
    }

    public String getContent()
    {
        return mContent;
    }

    @Override
    public String toString()
    {
        return "Product{" +
                "mID='" + mID + '\'' +
                ", mName='" + mName + '\'' +
                ", mBrand='" + mBrand + '\'' +
                ", mBarcode='" + mBarcode + '\'' +
                ", mPhotoGallery=" + mPhotoGallery +
                ", mMarketPrice=" + mMarketPrice +
                ", mHashtagList=" + mHashtagList +
                ", mCategoryCode=" + mCategoryCode +
                ", mSubCategoryCode=" + mSubCategoryCode +
                ", mWeight=" + mWeight +
                ", mNotes='" + mNotes + '\'' +
                ", mContent='" + mContent + '\'' +
                '}';
    }
}
