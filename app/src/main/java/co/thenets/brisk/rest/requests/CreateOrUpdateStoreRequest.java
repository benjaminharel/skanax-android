package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.Store;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class CreateOrUpdateStoreRequest
{
    // Device data for upload like push notification etc
    @SerializedName("store")
    private Store mStore;

    public CreateOrUpdateStoreRequest(Store store)
    {
        mStore = store;
    }

    @Override
    public String toString()
    {
        return "CreateStoreRequest{" +
                "mStore=" + mStore +
                '}';
    }
}
