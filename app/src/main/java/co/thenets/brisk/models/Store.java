package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import co.thenets.brisk.enums.AcceptedPaymentType;

/**
 * Created by DAVID-WORK on 10/11/2015.
 */

@Parcel
public class Store implements Serializable
{
    @SerializedName("id")
    private String mID;

    @SerializedName("name")
    private String mName;

    @SerializedName("photo")
    private String mImageLink;

    @SerializedName("photos")
    protected PhotoGallery mPhotoGallery;

    @SerializedName("phone")
    private String mPhone;

    @SerializedName("accepts_payments")
    private ArrayList<AcceptedPaymentType> mAcceptedPaymentList = new ArrayList<>();

    @SerializedName("active")
    private boolean mIsActive;

    @SerializedName("delivery_price")
    private float mDeliveryPrice;

    @SerializedName("rating")
    private float mRating;

    @SerializedName("rating_count")
    private int mReviewsCounter;

    @SerializedName("completed_orders")
    private int mCompletedOrders;

    @SerializedName("geo_area")
    private GeoArea mGeoArea;

    @SerializedName("store_products")
    private ArrayList<StoreProduct> mStoreProducts = new ArrayList<>();

    @SerializedName("working_hours")
    private ArrayList<WorkingDay> mWorkingDays = new ArrayList<>();

    @SerializedName("eta")
    private int mETA;

    @SerializedName("minimum_order")
    private int mMinimumOrderPrice;

    // This Map is not originally come from server, but maintained locally
    private HashMap<String, StoreProduct> mAllStoreProducts = new HashMap<>();

    public Store()
    {
    }

    public String getID()
    {
        return mID;
    }

    public String getName()
    {
        return mName;
    }

    public PhotoGallery getPhotoGallery()
    {
        if(mPhotoGallery == null)
        {
            mPhotoGallery = new PhotoGallery();
        }
        return mPhotoGallery;
    }

    public ArrayList<AcceptedPaymentType> getAcceptedPaymentList()
    {
        return mAcceptedPaymentList;
    }

    public boolean isCashPaymentAccepted()
    {
        for (AcceptedPaymentType acceptedPaymentType : mAcceptedPaymentList)
        {
            if (acceptedPaymentType == AcceptedPaymentType.CASH)
            {
                return true;
            }
        }

        return false;
    }

    public boolean isBriskPaymentAccepted()
    {
        for (AcceptedPaymentType acceptedPaymentType : mAcceptedPaymentList)
        {
            if (acceptedPaymentType == AcceptedPaymentType.BRISK_PAYMENT)
            {
                return true;
            }
        }

        return false;
    }


    public boolean isActive()
    {
        return mIsActive;
    }

    public float getDeliveryPrice()
    {
        return mDeliveryPrice;
    }

    public float getRating()
    {
        return mRating / 20;
    }

    public int getReviewsCounter()
    {
        return mReviewsCounter;
    }

    public int getCompletedOrders()
    {
        return mCompletedOrders;
    }

    public GeoArea getGeoArea()
    {
        return mGeoArea;
    }

    public ArrayList<WorkingDay> getWorkingDays()
    {
        return mWorkingDays;
    }

    public void setID(String ID)
    {
        mID = ID;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public void setImageLink(String imageLink)
    {
        mImageLink = imageLink;
    }

    public void setAcceptedPaymentList(ArrayList<AcceptedPaymentType> acceptedPaymentList)
    {
        mAcceptedPaymentList = acceptedPaymentList;
    }

    public void setIsActive(boolean isActive)
    {
        mIsActive = isActive;
    }

    public void setDeliveryPrice(float deliveryPrice)
    {
        mDeliveryPrice = deliveryPrice;
    }

    public void setRating(float rating)
    {
        mRating = rating;
    }

    public void setCompletedOrders(int completedOrders)
    {
        mCompletedOrders = completedOrders;
    }

    public void setGeoArea(GeoArea geoArea)
    {
        mGeoArea = geoArea;
    }

    public void setWorkingDays(ArrayList<WorkingDay> workingDays)
    {
        mWorkingDays = workingDays;
    }

    public int getETA()
    {
        return mETA;
    }

    public void setETA(int ETA)
    {
        mETA = ETA;
    }

    public String getPhone()
    {
        return mPhone;
    }

    public void setPhone(String phone)
    {
        mPhone = phone;
    }

    public ArrayList<StoreProduct> getStoreProducts()
    {
        return mStoreProducts;
    }

    public void addStoreProducts(ArrayList<StoreProduct> storeProducts)
    {
        for (StoreProduct storeProduct : storeProducts)
        {
            mAllStoreProducts.put(storeProduct.getID(), storeProduct);
        }
    }
    public void addStoreProduct(StoreProduct storeProduct)
    {
        mAllStoreProducts.put(storeProduct.getID(), storeProduct);
    }

    public StoreProduct getStoreProduct(String storeProductID)
    {
        return mAllStoreProducts.get(storeProductID);
    }

    public int getMinimumOrderPrice()
    {
        return mMinimumOrderPrice;
    }


    @Override
    public String toString()
    {
        return "Store{" +
                "mID='" + mID + '\'' +
                ", mName='" + mName + '\'' +
                ", mImageLink='" + mImageLink + '\'' +
                ", mPhone='" + mPhone + '\'' +
                ", mAcceptedPaymentList=" + mAcceptedPaymentList +
                ", mIsActive=" + mIsActive +
                ", mDeliveryPrice=" + mDeliveryPrice +
                ", mRating=" + mRating +
                ", mReviewsCounter=" + mReviewsCounter +
                ", mCompletedOrders=" + mCompletedOrders +
                ", mGeoArea=" + mGeoArea +
                ", mWorkingDays=" + mWorkingDays +
                ", mETA=" + mETA +
                '}';
    }
}
