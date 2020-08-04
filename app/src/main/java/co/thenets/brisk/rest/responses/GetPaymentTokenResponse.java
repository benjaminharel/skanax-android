package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class GetPaymentTokenResponse extends BaseResponse
{
    @SerializedName("customer_token")
    private String mPaymentToken;

    public String getPaymentToken()
    {
        return mPaymentToken;
    }

    @Override
    public String toString()
    {
        return "GetPaymentTokenResponse{" +
                "mPaymentToken='" + mPaymentToken + '\'' +
                "} " + super.toString();
    }
}
