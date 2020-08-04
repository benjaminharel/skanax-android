package co.thenets.brisk.enums;

/**
 * Created by DAVID-WORK on 30/03/2016.
 */
public enum AcceptedPaymentType
{
    CASH(0),
    BRISK_PAYMENT(1);

    private int mValue;

    AcceptedPaymentType(int value)
    {
        mValue = value;
    }

    public int getValue()
    {
        return mValue;
    }

    public static AcceptedPaymentType fromInt(int value)
    {
        if (value == BRISK_PAYMENT.getValue())
        {
            return BRISK_PAYMENT;
        }

        return CASH;
    }
}
