package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 10/03/2016.
 */

@Parcel
public class BriskCreditCard implements Serializable
{
    public BriskCreditCard()
    {
    }

    public BriskCreditCard(String number, String expireMonth, String expireYear, String cvv)
    {
        mNumber = number;
        mExpireMonth = expireMonth;
        mExpireYear = expireYear;
        mCvv = cvv;
    }

    @SerializedName("number")
    private String mNumber;

    @SerializedName("expire_month")
    private String mExpireMonth;

    @SerializedName("expire_year")
    private String mExpireYear;

    @SerializedName("cvv")
    private String mCvv;

    public String getNumber()
    {
        return mNumber;
    }

    public void setNumber(String number)
    {
        mNumber = number;
    }

    public String getExpireMonth()
    {
        return mExpireMonth;
    }

    public void setExpireMonth(String expireMonth)
    {
        mExpireMonth = expireMonth;
    }

    public String getExpireYear()
    {
        return mExpireYear;
    }

    public void setExpireYear(String expireYear)
    {
        mExpireYear = expireYear;
    }

    public String getCvv()
    {
        return mCvv;
    }

    public void setCvv(String cvv)
    {
        mCvv = cvv;
    }

    public String getRedactedCardNumber()
    {
        String lastDigits = mNumber.substring(mNumber.length() - 4);
        return "**************" + lastDigits;
    }

    @Override
    public String toString()
    {
        return "CreditCard{" +
                "mNumber='" + mNumber + '\'' +
                ", mExpireMonth='" + mExpireMonth + '\'' +
                ", mExpireYear='" + mExpireYear + '\'' +
                ", mCvv='" + mCvv + '\'' +
                '}';
    }
}
