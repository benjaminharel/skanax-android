package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import co.thenets.brisk.models.StoreProduct;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
@Parcel
public class GetStoreProductsResponse extends BaseResponse
{
    @SerializedName("store_products")
    private ArrayList<StoreProduct> mStoreProductList = new ArrayList<>();

    public ArrayList<StoreProduct> getStoreProductList()
    {
        return mStoreProductList;
    }

    @Override
    public String toString()
    {
        return "GetStoreProductsResponse{" +
                "mStoreProductList=" + mStoreProductList +
                "} " + super.toString();
    }
}
