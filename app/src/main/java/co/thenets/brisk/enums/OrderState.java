package co.thenets.brisk.enums;

/**
 * Created by DAVID-WORK on 14/01/2016.
 */
public enum OrderState
{
    CANCELED(-1, "cancel"),
    OPEN(0,"process"),
    PROCESSING(1, "on_the_way"),
    ON_THE_WAY(2, "deliver"),
    DELIVERED(3, "pay"),
    CLOSED(10, "pay"),
    DISPUTE(20, "report");

    private int mValue;
    private String mRelatedAction;

    OrderState(int value, String relatedAction)
    {
        mValue = value;
        mRelatedAction = relatedAction;
    }

    public int getValue()
    {
        return mValue;
    }

    public String getRelatedAction()
    {
        return mRelatedAction;
    }

    public static OrderState fromInt(int value)
    {
        if (value == CANCELED.getValue())
        {
            return CANCELED;
        }
        if (value == OPEN.getValue())
        {
            return OPEN;
        }
        if (value == PROCESSING.getValue())
        {
            return PROCESSING;
        }
        if (value == ON_THE_WAY.getValue())
        {
            return ON_THE_WAY;
        }
        if (value == DELIVERED.getValue())
        {
            return DELIVERED;
        }
        if (value == CLOSED.getValue())
        {
            return CLOSED;
        }
        if (value == DISPUTE.getValue())
        {
            return DISPUTE;
        }
        return OPEN;
    }
}
