package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import co.thenets.brisk.models.Store;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class GetStoresResponse extends BaseResponse
{
    @SerializedName("stores")
    private ArrayList<Store> mStoreList = new ArrayList<>();

    public ArrayList<Store> getStoreList()
    {
        return mStoreList;
    }

    @Override
    public String toString()
    {
        return "GetAvailableStoresResponse{" +
                "mStoreList=" + mStoreList +
                "} " + super.toString();
    }
}
