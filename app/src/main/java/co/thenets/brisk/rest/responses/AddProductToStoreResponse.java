package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import co.thenets.brisk.models.StoreProduct;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class AddProductToStoreResponse extends BaseResponse
{
    @SerializedName("store_product")
    private StoreProduct mStoreProduct;

    public StoreProduct getStoreProduct()
    {
        return mStoreProduct;
    }

    @Override
    public String toString()
    {
        return "AddProductToStoreResponse{" +
                "mStoreProduct=" + mStoreProduct +
                "} " + super.toString();
    }
}
