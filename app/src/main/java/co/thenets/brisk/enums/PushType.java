package co.thenets.brisk.enums;

/**
 * Created by DAVID BELOOSESKY on 22/01/2015.
 */
public enum PushType
{
    PROMOTION(0),
    ORDER(1),
    CHAT_MESSAGE(2),
    STORE_STATE(3);

    private int mValue;

    PushType(int value)
    {
        mValue = value;
    }

    public int getValue()
    {
        return mValue;
    }

    public static PushType fromInt(int value)
    {
        if (value == ORDER.getValue())
        {
            return ORDER;
        }
        else if (value == STORE_STATE.getValue())
        {
            return STORE_STATE;
        }
        return PROMOTION;
    }
}
