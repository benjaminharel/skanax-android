package co.thenets.brisk.gcm;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.StoreStatePushData;

/**
 * Created by DAVID BELOOSESKY on 03/12/2014 - 17:15.
 */
public class StoreStatePushPayload extends BasePushPayload
{
    @SerializedName("data")
    private StoreStatePushData mData;

    public StoreStatePushData getData()
    {
        return mData;
    }

    @Override
    public String toString()
    {
        return "StoreStatePushPayload{" +
                "mData=" + mData +
                "} " + super.toString();
    }
}
