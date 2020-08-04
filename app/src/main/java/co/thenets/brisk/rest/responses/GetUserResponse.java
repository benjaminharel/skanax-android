package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.User;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class GetUserResponse extends BaseResponse
{
    @SerializedName("user")
    private User mUser;

    public User getUser()
    {
        return mUser;
    }

    @Override
    public String toString()
    {
        return "GetUserResponse{" +
                "mUser=" + mUser +
                "} " + super.toString();
    }
}
