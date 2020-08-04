package co.thenets.brisk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.events.StoreUpdatedEvent;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;

/**
 * Created by DAVID-WORK on 14/03/2016.
 */
public class StoreIntroActivity extends IntroActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setFullscreen(true);
        super.onCreate(savedInstanceState);
        addPages();
        setSkipEnabled(false);
        setFinishEnabled(false);
        EventsManager.getInstance().register(this);
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    public void actionButtonClicked(View view)
    {
        if (ContentManager.getInstance().isUserRegistered())
        {
            if (ContentManager.getInstance().isUserCompletelyRegistered())
            {
                Intent intent = new Intent(this, BecomeBriskerActivity.class);
                startActivity(intent);
            }
            else
            {
                Snackbar.make(view, getString(R.string.edit_your_profile_and_add_first_and_last_name), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.update), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
        else
        {
            Snackbar.make(view, getString(R.string.please_register_before_becoming_a_brisker), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.register), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent mIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                            startActivity(mIntent);
                        }
                    }).show();
        }
    }

    private void addPages()
    {
        addSlide(new FragmentSlide.Builder()
                .background(R.color.intro_1)
                .backgroundDark(R.color.intro_1)
                .fragment(R.layout.intro_layout_1, R.style.AppTheme)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.intro_2)
                .fragment(R.layout.intro_layout_2, R.style.AppTheme)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.intro_3)
                .fragment(R.layout.intro_layout_3, R.style.AppTheme)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.intro_4)
                .fragment(R.layout.intro_layout_4, R.style.AppTheme)
                .build());
    }

    @Subscribe
    public void onStoreUpdated(StoreUpdatedEvent storeUpdatedEvent)
    {
        finish();
    }
}
