package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import co.thenets.brisk.models.ActiveCategory;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class GetActiveCategoriesResponse extends BaseResponse
{
    @SerializedName("active_categories")
    private ArrayList<ActiveCategory> mActiveCategoriesList = new ArrayList<>();

    public ArrayList<ActiveCategory> getActiveCategoriesList()
    {
        return mActiveCategoriesList;
    }

    @Override
    public String toString()
    {
        return "GetActiveCategoriesResponse{" +
                "mActiveCategoriesList=" + mActiveCategoriesList +
                "} " + super.toString();
    }
}
