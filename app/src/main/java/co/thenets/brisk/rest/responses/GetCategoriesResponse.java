package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import co.thenets.brisk.models.Category;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */

@Parcel
public class GetCategoriesResponse extends BaseResponse
{
    @SerializedName("categories")
    private ArrayList<Category> mCategoryList = new ArrayList<>();

    public ArrayList<Category> getCategoryList()
    {
        return mCategoryList;
    }

    @Override
    public String toString()
    {
        return "GetCategoriesResponse{" +
                "mCategoryList=" + mCategoryList +
                "} " + super.toString();
    }
}
