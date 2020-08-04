package co.thenets.brisk.events;

import co.thenets.brisk.enums.FragmentType;

/**
 * Created by DAVID-WORK on 21/03/2016.
 */
public class FragmentResumedEvent
{
    private FragmentType mFragmentType;

    public FragmentResumedEvent(FragmentType fragmentType)
    {
        mFragmentType = fragmentType;
    }

    public FragmentType getFragmentType()
    {
        return mFragmentType;
    }
}
