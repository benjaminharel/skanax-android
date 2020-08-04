package co.thenets.brisk.events;

/**
 * Created by DAVID-WORK on 23/11/2015.
 */
public class SearchWithHashtagEvent
{
    private String mHashtag;

    public SearchWithHashtagEvent(String hashtag)
    {
        mHashtag = hashtag;
    }

    public String getHashtag()
    {
        return mHashtag;
    }
}
