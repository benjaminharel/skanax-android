package co.thenets.brisk.enums;

/**
 * Created by DAVID-WORK on 11/11/2015.
 */
public enum TimeDirection
{
    FROM(0),
    TO(1);

    private int mValue;

    TimeDirection(int value)
    {
        mValue = value;
    }

    public int getValue()
    {
        return mValue;
    }

    public static TimeDirection fromInt(int value)
    {
        if (value == FROM.getValue())
        {
            return FROM;
        }

        return TO;
    }

}
