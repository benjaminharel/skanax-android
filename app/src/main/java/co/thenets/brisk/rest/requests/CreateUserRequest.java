package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.User;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class CreateUserRequest
{
    @SerializedName("user")
    private User mUser;

    public CreateUserRequest(User user)
    {
        mUser = user;
    }

    @Override
    public String toString()
    {
        return "CreateUserRequest{" +
                "mUser=" + mUser +
                '}';
    }
}
