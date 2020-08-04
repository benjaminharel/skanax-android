package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import co.thenets.brisk.enums.AcceptedPaymentType;

/**
 * Created by DAVID-WORK on 10/11/2015.
 */

public class StoreBasicDetails
{
    @SerializedName("name")
    private String mName;

    @SerializedName("photo")
    private String mBase64EncodeImageBytesArray;

    @SerializedName("accepts_payments")
    private ArrayList<AcceptedPaymentType> mAcceptedPaymentTypeList;

    @SerializedName("delivery_price")
    private float mDeliveryPrice;

    public StoreBasicDetails(String name, ArrayList<AcceptedPaymentType> acceptedPaymentTypeList, float deliveryPrice)
    {
        mName = name;
        mAcceptedPaymentTypeList = acceptedPaymentTypeList;
        mDeliveryPrice = deliveryPrice;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getBase64EncodeImageBytesArray()
    {
        return mBase64EncodeImageBytesArray;
    }

    public void setBase64EncodeImageBytesArray(String base64EncodeImageBytesArray)
    {
        mBase64EncodeImageBytesArray = base64EncodeImageBytesArray;
    }

    public ArrayList<AcceptedPaymentType> getAcceptedPaymentTypeList()
    {
        return mAcceptedPaymentTypeList;
    }

    public void setAcceptedPaymentTypeList(ArrayList<AcceptedPaymentType> acceptedPaymentTypeList)
    {
        mAcceptedPaymentTypeList = acceptedPaymentTypeList;
    }

    public float getDeliveryPrice()
    {
        return mDeliveryPrice;
    }

    public void setDeliveryPrice(float deliveryPrice)
    {
        mDeliveryPrice = deliveryPrice;
    }
}
