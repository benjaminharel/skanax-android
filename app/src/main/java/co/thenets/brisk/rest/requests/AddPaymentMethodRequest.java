package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.PaymentMethod;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class AddPaymentMethodRequest
{
    @SerializedName("payment_method")
    private PaymentMethod mPaymentMethod;

    public AddPaymentMethodRequest(PaymentMethod paymentMethod)
    {
        mPaymentMethod = paymentMethod;
    }

    @Override
    public String toString()
    {
        return "AddPaymentMethodRequest{" +
                "mPaymentMethod=" + mPaymentMethod +
                '}';
    }
}
