package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.StoreProductForUpload;

/**
 * Created by DAVID-WORK on 15/11/2015.
 */
public class AddExistingProductToStoreRequest
{
    @SerializedName("store_product")
    private StoreProductForUpload mProduct;

    public AddExistingProductToStoreRequest(StoreProductForUpload storeProductForUpload)
    {
        mProduct = storeProductForUpload;
    }

    @Override
    public String toString()
    {
        return "AddProductToStoreLiteRequest{" +
                "mProduct=" + mProduct +
                '}';
    }
}
