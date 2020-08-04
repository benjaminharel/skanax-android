package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 10/11/2015.
 */

@Parcel
public class WorkingDay implements Serializable
{
    public WorkingDay()
    {
    }

    public WorkingDay(String dayLabel, WorkingTime from, WorkingTime to)
    {
        mDayLabel = dayLabel;
        mFrom = from;
        mTo = to;
    }

    @SerializedName("day")
    private String mDayLabel;

    @SerializedName("from")
    private WorkingTime mFrom;

    @SerializedName("to")
    private WorkingTime mTo;

    public String getDayLabel()
    {
        return mDayLabel;
    }

    public void setDayLabel(String dayLabel)
    {
        mDayLabel = dayLabel;
    }

    public WorkingTime getFrom()
    {
        return mFrom;
    }

    public void setFrom(WorkingTime from)
    {
        mFrom = from;
    }

    public WorkingTime getTo()
    {
        return mTo;
    }

    public void setTo(WorkingTime to)
    {
        mTo = to;
    }

    @Override
    public String toString()
    {
        return "WorkingDay{" +
                "mDayLabel='" + mDayLabel + '\'' +
                ", mFrom=" + mFrom +
                ", mTo=" + mTo +
                '}';
    }
}
