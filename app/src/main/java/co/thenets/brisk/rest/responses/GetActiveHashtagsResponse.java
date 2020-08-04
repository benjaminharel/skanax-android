package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import co.thenets.brisk.models.Hashtag;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class GetActiveHashtagsResponse extends BaseResponse
{
    @SerializedName("hash_tags")
    private ArrayList<Hashtag> mActiveHashtagsList = new ArrayList<>();

    public ArrayList<Hashtag> getActiveHashtagsList()
    {
        return mActiveHashtagsList;
    }

    @Override
    public String toString()
    {
        return "GetActiveHashtagsResponse{" +
                "mActiveHashtagsList=" + mActiveHashtagsList +
                "} " + super.toString();
    }
}
