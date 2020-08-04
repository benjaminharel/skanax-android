package co.thenets.brisk.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import co.thenets.brisk.R;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.UIManager;

public class NoInternetActivity extends AppCompatActivity
{
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        setFullScreen();
        setContentView(R.layout.activity_no_internet);
        setViews();
    }

    private void setViews()
    {
        Button checkInternetConnectionButton = (Button) findViewById(R.id.checkInternetConnectionButton);
        checkInternetConnectionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(Utils.isNetworkAvailable(mContext))
                {
                    finish();
                }
                else
                {
                    UIManager.getInstance().displaySnackBarError(v, getString(R.string.no_internet_connection_error), Snackbar.LENGTH_SHORT);
                }
            }
        });
    }

    private void setFullScreen()
    {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
