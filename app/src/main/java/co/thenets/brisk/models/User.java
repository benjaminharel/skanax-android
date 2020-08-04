package co.thenets.brisk.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class User implements Serializable
{
    @SerializedName("id")
    private String mID;

    @SerializedName("device")
    private DeviceData mDeviceData;

    @SerializedName("first_name")
    private String mFirstName;

    @SerializedName("last_name")
    private String mLastName;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("phone")
    private String mPhone;

    @SerializedName("photo")
    private String mImageLink;

    @SerializedName("photos")
    protected PhotoGallery mPhotoGallery;

    @SerializedName("customer_id")
    private String mCustomerID;

    @SerializedName("store_id")
    private String mStoreID;

    @SerializedName("payment_method_token")
    private String mPaymentMethodToken;

    public String getID()
    {
        return mID;
    }

    public DeviceData getDeviceData()
    {
        return mDeviceData;
    }

    public String getFirstName()
    {
        if(TextUtils.isEmpty(mFirstName))
        {
            return "";
        }
        return mFirstName;
    }

    public String getLastName()
    {
        if(TextUtils.isEmpty(mLastName))
        {
            return "";
        }
        return mLastName;
    }

    public String getFullName()
    {
        if(TextUtils.isEmpty(mLastName) && TextUtils.isEmpty(mFirstName))
        {
            return "";
        }
        return mFirstName + " " + mLastName;
    }


    public String getEmail()
    {
        if(TextUtils.isEmpty(mEmail))
        {
            return "";
        }
        return mEmail;
    }

    public String getPhone()
    {
        return mPhone;
    }

    public PhotoGallery getPhotoGallery()
    {
        return mPhotoGallery;
    }

    public String getCustomerID()
    {
        return mCustomerID;
    }

    public String getStoreID()
    {
        return mStoreID;
    }

    public void setFirstName(String firstName)
    {
        mFirstName = firstName;
    }

    public void setLastName(String lastName)
    {
        mLastName = lastName;
    }

    public void setEmail(String email)
    {
        mEmail = email;
    }

    public void setPhone(String phone)
    {
        mPhone = phone;
    }

    public void setImageLink(String imageLink)
    {
        mImageLink = imageLink;
    }

    public void setDeviceData(DeviceData deviceData)
    {
        mDeviceData = deviceData;
    }

    public void setCustomerID(String customerID)
    {
        mCustomerID = customerID;
    }

    public void setStoreID(String storeID)
    {
        mStoreID = storeID;
    }

    public boolean isCCExists()
    {
        if(!TextUtils.isEmpty(mPaymentMethodToken))
        {
            return true;
        }
        return false;
    }

    public void setPaymentMethodToken(String paymentMethodToken)
    {
        mPaymentMethodToken = paymentMethodToken;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "mID='" + mID + '\'' +
                ", mDeviceData=" + mDeviceData +
                ", mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mPhone='" + mPhone + '\'' +
                ", mImageLink='" + mImageLink + '\'' +
                ", mCustomerID='" + mCustomerID + '\'' +
                ", mStoreID='" + mStoreID + '\'' +
                '}';
    }
}
