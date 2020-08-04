package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class UpdateOrderStateRequest
{
    @SerializedName("event")
    private String mEventValue;

    @SerializedName("data")
    private String mData;

    public UpdateOrderStateRequest(String eventValue)
    {
        mEventValue = eventValue;
    }

    public void setData(String data)
    {
        mData = data;
    }

    @Override
    public String toString()
    {
        return "UpdateOrderStateRequest{" +
                "mEventValue='" + mEventValue + '\'' +
                '}';
    }
}
