package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import co.thenets.brisk.models.CartItemForUpload;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class CreateCartListRequest
{
    @SerializedName("cart_list")
    private ArrayList<CartItemForUpload> mCartList;

    public CreateCartListRequest(ArrayList<CartItemForUpload> cartList)
    {
        mCartList = cartList;
    }

    public ArrayList<CartItemForUpload> getCartList()
    {
        return mCartList;
    }

    @Override
    public String toString()
    {
        return "CreateCartListRequest{" +
                "mCartList=" + mCartList +
                '}';
    }
}
