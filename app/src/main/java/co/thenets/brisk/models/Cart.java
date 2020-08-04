package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by DAVID-WORK on 17/12/2015.
 */
@Parcel
public class Cart implements Serializable
{
    @SerializedName("store")
    private Store mStore;

    @SerializedName("items")
    private ArrayList<CartItem> mCartItems;

    public Store getStore()
    {
        return mStore;
    }

    public ArrayList<CartItem> getCartItems()
    {
        return mCartItems;
    }

    public float getProductsPrice()
    {
        float totalCartPrice = 0;

        // Sum all cart items cost
        for (CartItem cartItem : mCartItems)
        {
            totalCartPrice += cartItem.getTotalPrice();
        }

        return Float.parseFloat(new DecimalFormat("##.##").format(totalCartPrice));
    }

    public float getTotalPrice()
    {
        float totalCartPrice = 0;

        // Sum all cart items cost
        for (CartItem cartItem : mCartItems)
        {
            totalCartPrice += cartItem.getTotalPrice();
        }

        // Add delivery price
        totalCartPrice += mStore.getDeliveryPrice();
        return Float.parseFloat(new DecimalFormat("##.##").format(totalCartPrice));
    }

    public boolean areAllProductsAvailable()
    {
        for (CartItem cartItem : mCartItems)
        {
            if(!cartItem.isAvailable())
            {
                return false;
            }
        }

        return true;
    }

    public int getAvailableItemsCounter()
    {
        int availableItemsCounter = 0;
        for (CartItem cartItem : mCartItems)
        {
            if(cartItem.isAvailable())
            {
                availableItemsCounter++;
            }
        }

        return availableItemsCounter;
    }


    @Override
    public String toString()
    {
        return "Cart{" +
                "mStore=" + mStore +
                ", mCartItems=" + mCartItems +
                '}';
    }
}
