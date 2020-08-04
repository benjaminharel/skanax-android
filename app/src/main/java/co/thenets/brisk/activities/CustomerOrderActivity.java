package co.thenets.brisk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;
import com.squareup.otto.Subscribe;

import net.wujingchao.android.view.SimpleTagImageView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.CircleView;
import co.thenets.brisk.dialogs.OrderItemsDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.OrderState;
import co.thenets.brisk.events.RefreshOrderActivityEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

public class CustomerOrderActivity extends OrderActivity implements View.OnClickListener
{
    private static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private CircleView mWaitingCircleView;
    private CircleView mInProgressCircleView;
    private CircleView mOtwCircleView;
    private CircleView mDeliveredCircleView;
    private ImageView mWaitingImageView;
    private ImageView mInProgressImageView;
    private ImageView mOtwImageView;
    private ImageView mDeliveredImageView;
    private TextView mWaitingTextView;
    private TextView mInProgressTextView;
    private TextView mOtwTextView;
    private TextView mDeliveredTextView;
    private View mInProgressLineView;
    private View mOtwLineView;
    private View mDeliveredLineView;
    private Button mActionButton;
    private TextView mMissingItemsTextView;
    private LinearLayout mEtaLayout;
    private TextView mEtaTextView;
    private RippleBackground mEtaRippleBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        setViews();
        EventsManager.getInstance().register(this);

        AnalyticsManager.getInstance().orderFlowScreen();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getOrder();
        updateEtaIfNeeded();
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_customer_order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_cart_list:
                OrderItemsDialog orderItemsDialog = OrderItemsDialog.newInstance(mOrder);
                orderItemsDialog.show(getFragmentManager(), OrderItemsDialog.class.getSimpleName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setOtherViews()
    {
        super.setOtherViews();
        mWaitingCircleView = (CircleView) findViewById(R.id.waitingCircleView);
        mInProgressCircleView = (CircleView) findViewById(R.id.inProgressCircleView);
        mOtwCircleView = (CircleView) findViewById(R.id.otwCircleView);
        mDeliveredCircleView = (CircleView) findViewById(R.id.deliveredCircleView);

        mWaitingImageView = (ImageView) findViewById(R.id.waitingImageView);
        mInProgressImageView = (ImageView) findViewById(R.id.inProgressImageView);
        mOtwImageView = (ImageView) findViewById(R.id.otwImageView);
        mDeliveredImageView = (ImageView) findViewById(R.id.deliveredImageView);

        mWaitingTextView = (TextView) findViewById(R.id.waitingTextView);
        mInProgressTextView = (TextView) findViewById(R.id.inProgressTextView);
        mOtwTextView = (TextView) findViewById(R.id.otwTextView);
        mDeliveredTextView = (TextView) findViewById(R.id.deliveredTextView);

        mInProgressLineView = findViewById(R.id.inProgressLineView);
        mOtwLineView = findViewById(R.id.otwLineView);
        mDeliveredLineView = findViewById(R.id.deliveredLineView);

        mCanceledImageView = (SimpleTagImageView) findViewById(R.id.canceledImageView);
        mMissingItemsTextView = (TextView) findViewById(R.id.missingItemsTextView);

        mActionButton = (Button) findViewById(R.id.actionButton);
        mActionButton.setOnClickListener(this);

        mEtaLayout = (LinearLayout) findViewById(R.id.etaLayout);
        mEtaTextView = (TextView) findViewById(R.id.etaTextView);
        mEtaRippleBackground = (RippleBackground) findViewById(R.id.etaRippleBackground);

        mCallButton = (ImageButton) findViewById(R.id.callButton);
        mSmsButton = (ImageButton) findViewById(R.id.smsButton);
        mCallButton.setOnClickListener(this);
        mSmsButton.setOnClickListener(this);
    }

    @Override
    protected void setAddress()
    {
        // Do nothing
    }

    protected void setViewsVisibility()
    {
        switch (mOrder.getState())
        {
            case OPEN:
                setInProgressOff();
                setOtwOff();
                setDeliveredOff();
                break;
            case PROCESSING:
                setInProgressOn();
                setOtwOff();
                setDeliveredOff();
                break;
            case ON_THE_WAY:
                setInProgressOn();
                setOtwOn();
                setDeliveredOff();
                break;
            case DELIVERED:
                setInProgressOn();
                setOtwOn();
                setDeliveredOn();
                break;
            case CANCELED:
                setCancelOn();
                break;
            case DISPUTE:
                setInProgressOn();
                setOtwOn();
                setDeliveredOn();
                break;
        }
    }

    private void setCancelOn()
    {
        mCanceledImageView.setVisibility(View.VISIBLE);
        mActionButton.setVisibility(View.GONE);

        mWaitingCircleView.setFillColor(ContextCompat.getColor(this, R.color.background_gray));
        mWaitingCircleView.setBorderColor(ContextCompat.getColor(this, R.color.light_gray));
        mWaitingTextView.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        mWaitingImageView.setImageResource(R.drawable.ic_approval_off);

        setInProgressOff();
        setOtwOff();
        setDeliveredOff();
    }

    private void setInProgressOn()
    {
        mInProgressCircleView.setFillColor(ContextCompat.getColor(this, R.color.color_primary));
        mInProgressCircleView.setBorderColor(ContextCompat.getColor(this, R.color.color_primary));
        mInProgressLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary));
        mInProgressTextView.setTextColor(ContextCompat.getColor(this, R.color.color_primary));
        mInProgressImageView.setImageResource(R.drawable.ic_in_progress_on);

        if (mOrder.getMissingItemsCounter() > 0)
        {
            String missingItemLabel = "";
            int existsItems = mOrder.getOrderItemList().size() - mOrder.getMissingItemsCounter();
            if (existsItems == 1)
            {
                missingItemLabel = String.format(getString(R.string.x_item_out_of_y_items_is_available),
                        String.valueOf(existsItems),
                        String.valueOf(mOrder.getOrderItemList().size()));
            }
            else
            {
                missingItemLabel = String.format(getString(R.string.x_items_out_of_y_items_are_available),
                        String.valueOf(existsItems),
                        String.valueOf(mOrder.getOrderItemList().size()));
            }

            mMissingItemsTextView.setText(missingItemLabel);
            mMissingItemsTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            mMissingItemsTextView.setVisibility(View.GONE);
        }
    }

    private void setInProgressOff()
    {
        mInProgressCircleView.setFillColor(ContextCompat.getColor(this, R.color.background_gray));
        mInProgressCircleView.setBorderColor(ContextCompat.getColor(this, R.color.light_gray));
        mInProgressLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        mInProgressTextView.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        mInProgressImageView.setImageResource(R.drawable.ic_in_progress_off);
        mMissingItemsTextView.setVisibility(View.GONE);
    }

    private void setOtwOn()
    {
        mOtwCircleView.setFillColor(ContextCompat.getColor(this, R.color.color_primary));
        mOtwCircleView.setBorderColor(ContextCompat.getColor(this, R.color.color_primary));
        mOtwLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary));
        mOtwTextView.setTextColor(ContextCompat.getColor(this, R.color.color_primary));
        mOtwImageView.setImageResource(R.drawable.ic_otw_on);

        mActionButton.setText(getString(R.string.continue_to_payment));
        mActionButton.setBackgroundResource(R.drawable.bg_cornered_ripple);

        mEtaTextView.setText(String.valueOf(mOrder.getEta()));
        mOtwImageView.setVisibility(View.INVISIBLE);
        mEtaLayout.setVisibility(View.VISIBLE);
        mEtaRippleBackground.startRippleAnimation();
    }

    private void setOtwOff()
    {
        mOtwCircleView.setFillColor(ContextCompat.getColor(this, R.color.background_gray));
        mOtwCircleView.setBorderColor(ContextCompat.getColor(this, R.color.light_gray));
        mOtwLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        mOtwTextView.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        mOtwImageView.setImageResource(R.drawable.ic_otw_off);
    }

    private void setDeliveredOn()
    {
        mDeliveredCircleView.setFillColor(ContextCompat.getColor(this, R.color.color_primary));
        mDeliveredCircleView.setBorderColor(ContextCompat.getColor(this, R.color.color_primary));
        mDeliveredLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary));
        mDeliveredTextView.setTextColor(ContextCompat.getColor(this, R.color.color_primary));
        mDeliveredImageView.setImageResource(R.drawable.ic_delivered_on);

        mActionButton.setText(getString(R.string.continue_to_payment));
        mActionButton.setBackgroundResource(R.drawable.bg_cornered_ripple);

        mEtaLayout.setVisibility(View.GONE);
        mOtwImageView.setVisibility(View.VISIBLE);

        mEtaRippleBackground.stopRippleAnimation();
    }

    private void setDeliveredOff()
    {
        mDeliveredCircleView.setFillColor(ContextCompat.getColor(this, R.color.background_gray));
        mDeliveredCircleView.setBorderColor(ContextCompat.getColor(this, R.color.light_gray));
        mDeliveredLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        mDeliveredTextView.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        mDeliveredImageView.setImageResource(R.drawable.ic_delivered_off);
    }

    protected void setHeaderDetails()
    {
        if (mOrder != null)
        {
            mNameTextView.setText(mOrder.getStore().getName());
            UIManager.getInstance().loadImage(this, mOrder.getStore().getPhotoGallery().getSmall(), mImageView, ImageType.STORE);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.actionButton:
                if(mOrder != null)
                {
                    switch (mOrder.getState())
                    {
                        case OPEN:
                        case PROCESSING:
                            cancelOrder();
                            break;
                        case ON_THE_WAY:
                        case DELIVERED:
                        case DISPUTE:
                            // Open order summary and payment activity
                            Intent intent = new Intent(mContext, PayActivity.class);
                            intent.putExtra(Params.ORDER, mOrder);
                            startActivity(intent);
                            finish();
                            break;
                    }
                }
                break;
            case R.id.callButton:
                if(mOrder != null)
                {
                    Utils.openDialer(this, mOrder.getStore().getPhone());
                }
                break;
            case R.id.smsButton:
                if(mOrder != null)
                {
                    Utils.sendSMS(this, mOrder.getStore().getPhone(), "");
                }
                break;
        }
    }

    private void updateEtaIfNeeded()
    {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask()
        {
            synchronized public void run()
            {
                if ((mOrder != null) && mOrder.getState() == OrderState.ON_THE_WAY)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            updateEta();
                        }
                    });
                }
            }
        }, ONE_MINUTE / 2, ONE_MINUTE);
    }

    private void updateEta()
    {
        RestClientManager.getInstance().getOrder(msOrderID, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                mOrder = ContentManager.getInstance().getCurrentOrder();
                mEtaTextView.setText(String.valueOf(mOrder.getEta()));
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

    @Subscribe
    public void orderStateChanged(RefreshOrderActivityEvent refreshOrderActivityEvent)
    {
        getOrder();
    }
}
