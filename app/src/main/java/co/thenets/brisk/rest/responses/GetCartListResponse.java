package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import co.thenets.brisk.models.Cart;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class GetCartListResponse extends BaseResponse
{
    @SerializedName("cart_list")
    private ArrayList<Cart> mCartList = new ArrayList<>();

    public ArrayList<Cart> getCartList()
    {
        return mCartList;
    }

    @Override
    public String toString()
    {
        return "GetCartsResponse{" +
                "mCartList=" + mCartList +
                "} " + super.toString();
    }
}
