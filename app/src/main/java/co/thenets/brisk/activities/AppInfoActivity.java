package co.thenets.brisk.activities;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.events.StoresRefreshedEvent;
import co.thenets.brisk.managers.ContentManager;

public class AppInfoActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // The XML contains the app info fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
    }

    @Subscribe
    public void activeProductsRefreshed(StoresRefreshedEvent storesRefreshedEvent)
    {
        if(!ContentManager.getInstance().isAppInSellerMode())
        {
            finish();
        }
    }
}
