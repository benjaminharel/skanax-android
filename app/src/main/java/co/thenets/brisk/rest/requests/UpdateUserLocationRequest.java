package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.GeoLocation;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class UpdateUserLocationRequest
{
    // Device data for upload like push notification etc
    @SerializedName("location")
    private GeoLocation mGeoLocation;

    public UpdateUserLocationRequest(GeoLocation geoLocation)
    {
        mGeoLocation = geoLocation;
    }

    @Override
    public String toString()
    {
        return "UpdateUserLocationRequest{" +
                "mLocation=" + mGeoLocation +
                '}';
    }
}
