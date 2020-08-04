package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import co.thenets.brisk.models.PaymentMethod;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class GetPaymentMethodResponse extends BaseResponse
{
    public ArrayList<PaymentMethod> getPaymentMethodList()
    {
        return mPaymentMethodList;
    }

    @SerializedName("payment_methods")
    private ArrayList<PaymentMethod> mPaymentMethodList;

    @Override
    public String toString()
    {
        return "GetPaymentMethodResponse{" +
                "mPaymentMethodList=" + mPaymentMethodList +
                "} " + super.toString();
    }
}
