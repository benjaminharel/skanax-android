package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by David on 07/07/2016.
 */
public class TransferOrderRequest
{
    @SerializedName("phone")
    private String mMessengerPhone;

    public TransferOrderRequest(String messengerPhone)
    {
        mMessengerPhone = messengerPhone;
    }

    @Override
    public String toString()
    {
        return "TransferOrderRequest{" +
                "mMessengerPhone='" + mMessengerPhone + '\'' +
                '}';
    }
}
