package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import co.thenets.brisk.models.Product;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class GetProductsResponse extends BaseResponse
{
    @SerializedName("products")
    private ArrayList<Product> mProductList = new ArrayList<>();

    public ArrayList<Product> getProductList()
    {
        return mProductList;
    }

    @Override
    public String toString()
    {
        return "GetProductsResponse{" +
                "mProductList=" + mProductList +
                "} " + super.toString();
    }
}
