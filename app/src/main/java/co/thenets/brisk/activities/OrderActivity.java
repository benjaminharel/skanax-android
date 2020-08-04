package co.thenets.brisk.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.wujingchao.android.view.SimpleTagImageView;

import co.thenets.brisk.R;
import co.thenets.brisk.dialogs.ConfirmationDialog;
import co.thenets.brisk.enums.OrderState;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.interfaces.ConfirmationListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Order;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.UpdateOrderStateRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

public abstract class OrderActivity extends BaseActivity
{
    protected Order mOrder;
    protected static String msOrderID;
    protected LinearLayout mProgressBarLayout;
    protected Toolbar mToolbar;
    protected ImageView mImageView;
    protected TextView mNameTextView;
    protected static boolean msVisible = false;
    protected Context mContext;
    protected SimpleTagImageView mCanceledImageView;
    protected ImageButton mCallButton;
    protected ImageButton mSmsButton;

    protected abstract void setHeaderDetails();
    protected abstract void setViewsVisibility();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        msOrderID = getIntent().getStringExtra(Params.ORDER_ID);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        msVisible = true;
    }

    @Override
    protected void onPause()
    {
        msVisible = false;
        super.onPause();
    }

    protected void setViews()
    {
        setToolbar();
        setOtherViews();
    }

    protected void setToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mOrder != null)
        {
            mToolbar.setTitle(String.format(getString(R.string.order_no_x), String.valueOf(mOrder.getOrderNumber())));
        }
        else
        {
            mToolbar.setTitle(getString(R.string.order));
        }

        setSupportActionBar(mToolbar);
    }

    protected void setOtherViews()
    {
        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mNameTextView = (TextView) findViewById(R.id.itemNameTextView);
    }

    protected void getOrder()
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        RestClientManager.getInstance().getOrder(msOrderID, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                mOrder = ContentManager.getInstance().getCurrentOrder();
                setToolbar();
                setHeaderDetails();
                setViewsVisibility();
                setAddress();
                mProgressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
            }
        });
    }

    protected abstract void setAddress();


    public static String getOrderID()
    {
        return msOrderID;
    }

    protected void cancelOrder()
    {
        ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance("",getString(R.string.are_you_sure_you_want_to_cancel_the_order), new ConfirmationListener()
        {
            @Override
            public void onApprove()
            {
                UpdateOrderStateRequest updateOrderStateRequest = new UpdateOrderStateRequest(OrderState.CANCELED.getRelatedAction());
                RestClientManager.getInstance().updateOrderState(msOrderID, updateOrderStateRequest, new RequestListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        Toast.makeText(mContext, getString(R.string.order_canceled), Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onInternalServerFailure(ErrorResponse error)
                    {

                    }

                    @Override
                    public void onNetworkFailure(RetrofitError error)
                    {

                    }
                });
            }

            @Override
            public void onCancel()
            {
                UIManager.getInstance().displaySnackBar(mImageView, getString(R.string.thanks_god), Snackbar.LENGTH_SHORT);
            }
        });
        confirmationDialog.show(getFragmentManager(), ConfirmationDialog.class.getSimpleName());

    }

    public static boolean isVisible()
    {
        return msVisible;
    }
}
