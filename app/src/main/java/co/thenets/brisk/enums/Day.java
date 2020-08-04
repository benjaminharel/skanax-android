package co.thenets.brisk.enums;

/**
 * Created by DAVID-WORK on 11/11/2015.
 */
public enum Day
{
    SUNDAY(1, "Sunday"),
    MONDAY(2, "Monday"),
    TUESDAY(3, "Tuesday"),
    WEDNESDAY(4, "Wednesday"),
    THURSDAY(5, "Thursday"),
    FRIDAY(6, "Friday"),
    SATURDAY(7, "Saturday");

    private int mValue;
    private String mLabel;

    Day(int value, String label)
    {
        mValue = value;
        mLabel = label;
    }

    public int getValue()
    {
        return mValue;
    }

    public String getLabel()
    {
        return mLabel;
    }

    public static Day getDayType(String label)
    {
        if (label.equals(SUNDAY.getLabel()))
        {
            return SUNDAY;
        }
        else if (label.equals(MONDAY.getLabel()))
        {
            return MONDAY;
        }
        else if (label.equals(TUESDAY.getLabel()))
        {
            return TUESDAY;
        }
        else if (label.equals(WEDNESDAY.getLabel()))
        {
            return WEDNESDAY;
        }
        else if (label.equals(THURSDAY.getLabel()))
        {
            return THURSDAY;
        }
        else if (label.equals(FRIDAY.getLabel()))
        {
            return FRIDAY;
        }

        return SATURDAY;
    }

}
