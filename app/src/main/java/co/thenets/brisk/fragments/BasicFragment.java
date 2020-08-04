package co.thenets.brisk.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import co.thenets.brisk.interfaces.NavigationActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class BasicFragment extends Fragment
{
    private static final String LOG_TAG = BasicFragment.class.getSimpleName();
    protected View mRootView;
    protected Toolbar mToolbar;
    protected FloatingActionButton mFAB;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof NavigationActivity)
        {
            ((NavigationActivity)getActivity()).setupNavigationDrawer(mToolbar);
        }
    }
}
