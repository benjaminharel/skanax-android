package co.thenets.brisk.enums;

/**
 * Created by DAVID-WORK on 24/01/2016.
 */
public enum RoleType
{
    CUSTOMER(0),
    STORE(1);

    private int mValue;

    RoleType(int value)
    {
        mValue = value;
    }

    public int getValue()
    {
        return mValue;
    }

    public static RoleType fromInt(int value)
    {
        if (value == STORE.getValue())
        {
            return STORE;
        }
        return CUSTOMER;
    }
}
