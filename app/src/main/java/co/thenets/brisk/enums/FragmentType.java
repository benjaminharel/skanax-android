package co.thenets.brisk.enums;

/**
 * Created by DAVID BELOOSESKY on 22/01/2015.
 */
public enum FragmentType
{
    DASHBOARD(0),
    ORDERS(1),
    INVENTORY(2),
    WORKING_RADIUS(3),
    WORKING_HOURS(4),
    APP_INFO(5);

    private int mValue;

    FragmentType(int value)
    {
        mValue = value;
    }

    public int getValue()
    {
        return mValue;
    }
}
