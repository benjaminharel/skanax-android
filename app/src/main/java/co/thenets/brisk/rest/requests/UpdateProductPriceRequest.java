package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.StoreProduct;

/**
 * Created by DAVID-WORK on 15/11/2015.
 */
public class UpdateProductPriceRequest
{
    @SerializedName("store_product")
    private StoreProduct mProduct;

    public UpdateProductPriceRequest(StoreProduct storeProduct)
    {
        mProduct = storeProduct;
    }

    @Override
    public String toString()
    {
        return "UpdateProductPriceRequest{" +
                "mProduct=" + mProduct +
                '}';
    }
}
