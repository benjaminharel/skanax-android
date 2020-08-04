package co.thenets.brisk.models;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

import co.thenets.brisk.R;

/**
 * Created by DAVID-WORK on 08/03/2016.
 */

@Parcel
public class BriskAddress implements Serializable
{
    @SerializedName("city")
    private String mCity;

    @SerializedName("street")
    private String mStreet;

    @SerializedName("house")
    private String mHouseNumber;

    @SerializedName("floor")
    private String mFloorNumber;

    @SerializedName("apartment")
    private String mApartmentNumber;

    @SerializedName("info")
    private String mNotes;

    public String getCity()
    {
        return mCity;
    }

    public String getStreet()
    {
        return mStreet;
    }

    public String getHouseNumber()
    {
        return mHouseNumber;
    }

    public String getFloorNumber()
    {
        return mFloorNumber;
    }

    public String getApartmentNumber()
    {
        return mApartmentNumber;
    }

    public String getNotes()
    {
        return mNotes;
    }

    public void setCity(String city)
    {
        mCity = city;
    }

    public void setStreet(String street)
    {
        mStreet = street;
    }

    public void setHouseNumber(String houseNumber)
    {
        mHouseNumber = houseNumber;
    }

    public void setFloorNumber(String floorNumber)
    {
        mFloorNumber = floorNumber;
    }

    public void setApartmentNumber(String apartmentNumber)
    {
        mApartmentNumber = apartmentNumber;
    }

    public void setNotes(String notes)
    {
        mNotes = notes;
    }

    public String getAddressToNavigate()
    {
        String address = mStreet + " " + mHouseNumber + " " + mCity;
        return address;
    }

    public String getFullAddressForDisplay(Context context)
    {
        String address = getAddressToNavigate();
        if (!TextUtils.isEmpty(mFloorNumber))
        {
            address = address + " " + context.getString(R.string.floor_number) + ": " + mFloorNumber;
        }
        if (!TextUtils.isEmpty(mApartmentNumber))
        {
            address = address + " " + context.getString(R.string.apartment_number) + ": " + mApartmentNumber;
        }
        if (!TextUtils.isEmpty(mNotes))
        {
            address = address + " " + context.getString(R.string.notes) + ": " + mNotes;
        }

        return address;
    }

    @Override
    public String toString()
    {
        return "Address{" +
                "mCity='" + mCity + '\'' +
                ", mStreet='" + mStreet + '\'' +
                ", mHouseNumber='" + mHouseNumber + '\'' +
                ", mFloorNumber='" + mFloorNumber + '\'' +
                ", mApartmentNumber='" + mApartmentNumber + '\'' +
                ", mNotes='" + mNotes + '\'' +
                '}';
    }
}
