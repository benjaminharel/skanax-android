package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
@Parcel
public class ErrorResponse implements Serializable
{
    @SerializedName("code")
    private int mCode;

    @SerializedName("message")
    private String mMessage;

    public int getCode()
    {
        return mCode;
    }

    public String getMessage()
    {
        return mMessage;
    }

    @Override
    public String toString()
    {
        return "ErrorResponse{" +
                "mCode=" + mCode +
                ", mMessage='" + mMessage + '\'' +
                '}';
    }
}
