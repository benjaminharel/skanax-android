package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 01/12/2015.
 */

@Parcel
public class GeoLocation implements Serializable
{
    @SerializedName("lat")
    protected double mLatitude;

    @SerializedName("lon")
    protected double mLongitude;

    public GeoLocation()
    {
    }

    public GeoLocation(double latitude, double longitude)
    {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    @Override
    public String toString()
    {
        return "Location{" +
                "mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                '}';
    }
}
