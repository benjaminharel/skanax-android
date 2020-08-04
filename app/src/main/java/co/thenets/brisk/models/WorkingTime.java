package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 22/02/2016.
 */

@Parcel
public class WorkingTime implements Serializable
{
    public WorkingTime()
    {
    }

    public WorkingTime(int hour)
    {
        mHour = hour;
        mMinutes = 0;
    }

    public WorkingTime(int hour, int minutes)
    {
        mHour = hour;
        mMinutes = minutes;
    }

    @SerializedName("hour")
    private int mHour;

    @SerializedName("minute")
    private int mMinutes;

    public int getHour()
    {
        return mHour;
    }

    public int getMinutes()
    {
        return mMinutes;
    }

    public void setHour(int hour)
    {
        mHour = hour;
    }

    public void setMinutes(int minutes)
    {
        mMinutes = minutes;
    }

    @Override
    public String toString()
    {
        return "WorkingTime{" +
                "mHour=" + mHour +
                ", mMinutes=" + mMinutes +
                '}';
    }
}
