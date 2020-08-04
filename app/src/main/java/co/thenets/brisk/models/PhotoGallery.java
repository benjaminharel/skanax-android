package co.thenets.brisk.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 10/11/2015.
 */

@Parcel
public class PhotoGallery implements Serializable
{
    @SerializedName("small")
    protected String mSmall;

    @SerializedName("medium")
    protected String mMedium;

    @SerializedName("original")
    protected String mOriginal;


    public String getSmall()
    {
        return TextUtils.isEmpty(mSmall) ? "" : mSmall;
    }

    public String getMedium()
    {
        return TextUtils.isEmpty(mMedium) ? "" : mMedium;
    }

    public String getOriginal()
    {
        return TextUtils.isEmpty(mOriginal) ? "" : mOriginal;
    }

    @Override
    public String toString()
    {
        return "PhotoGallery{" +
                "mSmall=" + mSmall +
                ", mMedium=" + mMedium +
                ", mOriginal=" + mOriginal +
                '}';
    }
}
