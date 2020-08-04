package co.thenets.brisk.events;

/**
 * Created by Roei on 24-Nov-15.
 */
public class CodeDetectedInSmsEvent
{
    private String mValue;

    public CodeDetectedInSmsEvent(String value)
    {
        mValue = value;
    }

    public String getValue()
    {
        return mValue;
    }
}
