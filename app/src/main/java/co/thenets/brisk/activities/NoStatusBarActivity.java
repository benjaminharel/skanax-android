package co.thenets.brisk.activities;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by DAVID-WORK on 25/02/2016.
 */
public class NoStatusBarActivity extends BaseActivity
{
    protected void hideStatusBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
