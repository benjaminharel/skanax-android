package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

import co.thenets.brisk.rest.AdvancedConfiguration;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
@Parcel
public class BaseResponse implements Serializable
{
    @SerializedName("status")
    private String mStatus;

    @SerializedName("error")
    private ErrorResponse mErrorResponse;

    public boolean isSucceeded()
    {
        if(mStatus.equalsIgnoreCase(AdvancedConfiguration.STATUS_SUCCESS))
        {
            return true;
        }

        return false;
    }

    public ErrorResponse getErrorResponse()
    {
        return mErrorResponse;
    }

    @Override
    public String toString()
    {
        return "BaseResponse{" +
                "mStatus='" + mStatus + '\'' +
                ", mErrorResponse=" + mErrorResponse +
                '}';
    }
}
