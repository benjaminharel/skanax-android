package co.thenets.brisk.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class SetOrderRatingRequest
{
    @SerializedName("rating")
    private int mRating;

    public SetOrderRatingRequest(int rating)
    {
        mRating = rating;
    }

    @Override
    public String toString()
    {
        return "SetOrderRatingRequest{" +
                "mRating=" + mRating +
                '}';
    }
}
