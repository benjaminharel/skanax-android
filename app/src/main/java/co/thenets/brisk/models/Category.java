package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by DAVID-WORK on 22/11/2015.
 */

@Parcel
public class Category implements Serializable
{
    @SerializedName("code")
    private int mCode;

    @SerializedName("name")
    private String mName;

    @SerializedName("weight")
    private int mWeight;

    @SerializedName("sub_categories")
    private ArrayList<Category> mSubCategories;

    public Category()
    {
    }

    public Category(int code, String name)
    {
        mCode = code;
        mName = name;
    }

    private boolean mIsChecked;

    public int getCode()
    {
        return mCode;
    }

    public void setCode(int code)
    {
        mCode = code;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public boolean isChecked()
    {
        return mIsChecked;
    }

    public void setIsChecked(boolean isChecked)
    {
        mIsChecked = isChecked;
    }

    public int getWeight()
    {
        return mWeight;
    }

    public ArrayList<Category> getSubCategories()
    {
        return mSubCategories;
    }

    @Override
    public String toString()
    {
        return "Category{" +
                "mCode=" + mCode +
                ", mName='" + mName + '\'' +
                '}';
    }
}
