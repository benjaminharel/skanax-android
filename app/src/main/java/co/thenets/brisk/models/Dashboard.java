package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

import co.thenets.brisk.general.Params;

/**
 * Created by DAVID-WORK on 12/03/2016.
 */

@Parcel
public class Dashboard implements Serializable
{
    @SerializedName("active")
    private boolean mIsStoreActive;

    @SerializedName("daily_income")
    private float mDailyIncome;

    @SerializedName("monthly_income")
    private float mMonthlyIncome;

    @SerializedName("open_orders_count")
    private int mNewOrdersCounter;

    @SerializedName("working_orders_count")
    private int mInProcessOrdersCounter;

    @SerializedName("finished_orders_count")
    private int mFinishedOrdersCounter;

    @SerializedName("all_orders_count")
    private int mAllOrdersCounter;

    @SerializedName("delivery_price")
    private float mDeliveryPrice;

    @SerializedName("radius")
    private int mRadius;

    @SerializedName("rating_value")
    private float mRatingValue;

    @SerializedName("rating_count")
    private int mRatingCounter;

    public boolean isStoreActive()
    {
        return mIsStoreActive;
    }

    public String getDailyIncome()
    {
        return String.format(Params.CURRENCY_FORMAT, mDailyIncome);
    }

    public String getMonthlyIncome()
    {
        return String.format(Params.CURRENCY_FORMAT, mMonthlyIncome);
    }

    public int getNewOrdersCounter()
    {
        return mNewOrdersCounter;
    }

    public int getInProcessOrdersCounter()
    {
        return mInProcessOrdersCounter;
    }

    public int getAllOrdersCounter()
    {
        return mAllOrdersCounter;
    }

    public float getDeliveryPrice()
    {
        return mDeliveryPrice;
    }

    public int getRadius()
    {
        return mRadius;
    }

    public float getRatingValue()
    {
        return mRatingValue / 20;
    }

    public int getRatingCounter()
    {
        return mRatingCounter;
    }

    public int getFinishedOrderCounter()
    {
        return mFinishedOrdersCounter;
    }

    @Override
    public String toString()
    {
        return "Dashboard{" +
                "mIsStoreActive=" + mIsStoreActive +
                ", mDailyIncome=" + mDailyIncome +
                ", mMonthlyIncome=" + mMonthlyIncome +
                ", mNewOrdersCounter=" + mNewOrdersCounter +
                ", mInProcessOrdersCounter=" + mInProcessOrdersCounter +
                ", mAllOrdersCounter=" + mAllOrdersCounter +
                ", mDeliveryPrice=" + mDeliveryPrice +
                ", mRadius=" + mRadius +
                ", mRatingValue=" + mRatingValue +
                ", mRatingCounter=" + mRatingCounter +
                '}';
    }
}
