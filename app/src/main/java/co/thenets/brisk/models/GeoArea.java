package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by DAVID-WORK on 10/11/2015.
 */

@Parcel
public class GeoArea extends GeoLocation
{
    @SerializedName("radius")
    private double mRadius;

    public GeoArea()
    {
    }

    public GeoArea(double latitude, double longitude, int radius)
    {
        mLatitude = latitude;
        mLongitude = longitude;
        mRadius = radius;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public double getRadius()
    {
        return mRadius;
    }


    @Override
    public String toString()
    {
        return "GeoArea{" +
                "mRadius=" + mRadius +
                "} " + super.toString();
    }
}
