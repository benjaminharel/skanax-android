package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 12/12/2015.
 */

@Parcel
public class Hashtag implements Serializable
{
    @SerializedName("name")
    private String mName;

    @SerializedName("weight")
    private int mWeight;

    public String getName()
    {
        return mName;
    }

    public int getWeight()
    {
        return mWeight;
    }

    @Override
    public String toString()
    {
        return "Hashtag{" +
                "mName='" + mName + '\'' +
                ", mWeight=" + mWeight +
                '}';
    }
}
