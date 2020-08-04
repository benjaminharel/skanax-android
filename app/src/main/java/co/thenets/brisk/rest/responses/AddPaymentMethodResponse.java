package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import co.thenets.brisk.models.PaymentMethod;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class AddPaymentMethodResponse extends BaseResponse
{
    @SerializedName("payment_method")
    private PaymentMethod mPaymentMethod;

    public PaymentMethod getPaymentMethod()
    {
        return mPaymentMethod;
    }

    @Override
    public String toString()
    {
        return "AddPaymentMethodResponse{" +
                "mPaymentMethod=" + mPaymentMethod +
                "} " + super.toString();
    }
}
