package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 19/03/2016.
 */

@Parcel
public class Tip implements Serializable
{
    public Tip()
    {
    }

    @SerializedName("tip")
    private int mTip;

    public Tip(int tip)
    {
        mTip = tip;
    }
}
