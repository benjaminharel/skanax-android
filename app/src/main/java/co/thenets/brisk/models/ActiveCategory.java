package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

import co.thenets.brisk.general.Utils;

/**
 * Created by DAVID-WORK on 14/02/2016.
 */

@Parcel
public class ActiveCategory implements Serializable
{
    @SerializedName("code")
    private int mCode;

    @SerializedName("name")
    private String mName;

    @SerializedName("weight")
    private int mWeight;

    @SerializedName("color")
    private int mColor;

    @SerializedName("icon")
    private String mIconLink;

    // Count of items in this category
    @SerializedName("count")
    private int mCounter;

    @SerializedName("visible")
    private boolean mVisible;

    @SerializedName("sub_categories")
    private ArrayList<ActiveCategory> mSubCategories;

    public int getCode()
    {
        return mCode;
    }

    public String getName()
    {
        return mName;
    }

    public int getWeight()
    {
        return mWeight;
    }

    public int getColor()
    {
        return Utils.convertDecColorToHex(mColor);
    }

    public String getIconLink()
    {
        return mIconLink;
    }

    public int getCounter()
    {
        return mCounter;
    }

    public ArrayList<ActiveCategory> getSubCategories()
    {
        return mSubCategories;
    }

    public boolean isVisible()
    {
        return mVisible;
    }

    @Override
    public String toString()
    {
        return "ActiveCategory{" +
                ", mCode=" + mCode +
                ", mName='" + mName + '\'' +
                ", mWeight=" + mWeight +
                ", mColor=" + mColor +
                ", mIconLink='" + mIconLink + '\'' +
                ", mCounter=" + mCounter +
                ", mVisible=" + mVisible +
                ", mSubCategories=" + mSubCategories +
                '}';
    }
}
