package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 02/12/2015.
 */

@Parcel
public class Customer implements Serializable
{
    public Customer()
    {
    }

    public Customer(String ID)
    {
        mID = ID;
    }

    @SerializedName("id")
    private String mID;

    @SerializedName("name")
    private String mName;

    @SerializedName("photo")
    private String mImageLink;

    @SerializedName("phone")
    private String mPhone;

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

    public String getImageLink()
    {
        return mImageLink;
    }

    public String getPhone()
    {
        return mPhone;
    }

    @Override
    public String toString()
    {
        return "Customer{" +
                "mID='" + mID + '\'' +
                '}';
    }
}
