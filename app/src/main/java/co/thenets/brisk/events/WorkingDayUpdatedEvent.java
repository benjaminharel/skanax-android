package co.thenets.brisk.events;

import co.thenets.brisk.enums.Day;

/**
 * Created by DAVID-WORK on 22/02/2016.
 */
public class WorkingDayUpdatedEvent
{
    private Day mUpdatedDay;

    public WorkingDayUpdatedEvent(Day updatedDay)
    {
        mUpdatedDay = updatedDay;
    }

    public Day getUpdatedDay()
    {
        return mUpdatedDay;
    }
}
