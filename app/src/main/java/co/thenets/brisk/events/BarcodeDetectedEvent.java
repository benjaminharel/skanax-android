package co.thenets.brisk.events;

/**
 * Created by Roei on 24-Nov-15.
 */
public class BarcodeDetectedEvent
{
    private String mValue;

    public BarcodeDetectedEvent(String value)
    {
        mValue = value;
    }

    public String getValue()
    {
        return mValue;
    }
}
