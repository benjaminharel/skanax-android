package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by DAVID-WORK on 07/01/2016.
 */

@Parcel
public class OrderForUpload implements Serializable
{
    @SerializedName("customer")
    private Customer mCustomer;

    @SerializedName("store")
    private Store mStore;

    @SerializedName("order_items")
    private ArrayList<OrderItemForUpload> mOrderItemList;

    @SerializedName("address")
    private BriskAddress mBriskAddress;

    @SerializedName("payment_method")
    private PaymentMethod mPaymentMethod;

    public void setCustomerID(String customerID)
    {
        mCustomer = new Customer();
        mCustomer.setID(customerID);
    }

    public void setStoreID(String storeID)
    {
        mStore = new Store();
        mStore.setID(storeID);
    }

    public void setOrderItemList(ArrayList<OrderItemForUpload> orderItemList)
    {
        mOrderItemList = orderItemList;
    }

    public void setBriskAddress(BriskAddress briskAddress)
    {
        mBriskAddress = briskAddress;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod)
    {
        mPaymentMethod = paymentMethod;
    }
}
