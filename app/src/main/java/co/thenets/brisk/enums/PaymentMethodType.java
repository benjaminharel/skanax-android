package co.thenets.brisk.enums;

/**
 * Created by DAVID BELOOSESKY on 22/01/2015.
 */
public enum PaymentMethodType
{
    CASH(0),
    PAYPAL(1),
    CREDIT_CARD(2);

    private int mValue;

    PaymentMethodType(int value)
    {
        mValue = value;
    }

    public int getValue()
    {
        return mValue;
    }

    public static PaymentMethodType fromInt(int value)
    {
        if (value == CREDIT_CARD.getValue())
        {
            return CREDIT_CARD;
        }
        else if(value == PAYPAL.getValue())
        {
            return PAYPAL;
        }

        return CASH;
    }
}
