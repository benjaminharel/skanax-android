package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.StoreProduct;

/**
 * Created by DAVID-WORK on 15/11/2015.
 */
public class AddProductToStoreRequest
{
    @SerializedName("store_product")
    private StoreProduct mProduct;

    public AddProductToStoreRequest(StoreProduct storeProduct)
    {
        mProduct = storeProduct;
    }

    @Override
    public String toString()
    {
        return "AddProductToStoreRequest{" +
                "mProduct=" + mProduct +
                '}';
    }
}
