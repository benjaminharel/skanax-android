package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 14/01/2016.
 */

@Parcel
public class StoreStatePushData implements Serializable
{
    @SerializedName("store_open")
    private boolean mIsStoreOpen;

    public boolean isStoreOpen()
    {
        return mIsStoreOpen;
    }

    @Override
    public String toString()
    {
        return "StoreStatePushData{" +
                "mIsStoreOpen=" + mIsStoreOpen +
                '}';
    }
}
