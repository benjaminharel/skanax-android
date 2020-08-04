package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.User;

/**
 * Created by Roei on 08/11/2015.
 */
public class UpdateUserRequest
{
    // Device data for upload like push notification etc
    @SerializedName("user")
    private User mUser;

    public UpdateUserRequest(User user)
    {
        mUser = user;
    }

    @Override
    public String toString()
    {
        return "UpdateUserRequest{" +
                "mUser=" + mUser +
                '}';
    }
}
