package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.Store;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class GetStoreResponse extends BaseResponse
{
    @SerializedName("store")
    private Store mStore;

    public Store getStore()
    {
        return mStore;
    }

    @Override
    public String toString()
    {
        return "GetStoreResponse{" +
                "mStore=" + mStore +
                "} " + super.toString();
    }
}
