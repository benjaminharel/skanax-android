package co.thenets.brisk.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.OrderReceiptRecyclerAdapter;
import co.thenets.brisk.custom.DividerItemDecoration;
import co.thenets.brisk.dialogs.RatingStoreDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.OrderState;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Order;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.UpdateOrderStateRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestCreateOrderListener;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

public class PayActivity extends BaseActivity implements View.OnClickListener
{
    private RecyclerView mRecyclerView;
    private TextView mNameTextView;
    private ImageView mImageView;
    private TextView mProductsPriceTextView;
    private TextView mDeliveryPriceTextView;
    private TextView mTipTextView;
    private TextView mTotalPriceTextView;
    private Order mOrder;
    private LinearLayout mProgressBarLayout;
    private Button mPayButton;
    private int mTipValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mOrder = (Order) getIntent().getSerializableExtra(Params.ORDER);
        setToolbar();
        setViews();
        setData();
        setItemsAdapter();
    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.receipt_toolbar);
        toolbar.setTitle(String.format(getString(R.string.order_no_x), String.valueOf(mOrder.getOrderNumber())));
        setSupportActionBar(toolbar);
    }

    private void setViews()
    {
        mNameTextView = (TextView) findViewById(R.id.itemNameTextView);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mProductsPriceTextView = (TextView) findViewById(R.id.productsPriceTextView);
        mDeliveryPriceTextView = (TextView) findViewById(R.id.deliveryPriceEditText);
        mTipTextView = (TextView) findViewById(R.id.tipTextView);
        mTotalPriceTextView = (TextView) findViewById(R.id.totalPriceTextView);
        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
        mPayButton = (Button) findViewById(R.id.payButton);
        mPayButton.setOnClickListener(this);

        setTipToggleView();
    }

    private void setTipToggleView()
    {
        final MultiStateToggleButton tipToggleButton = (MultiStateToggleButton) this.findViewById(R.id.tipToggle);
        tipToggleButton.setOnValueChangedListener(new ToggleButton.OnValueChangedListener()
        {
            @Override
            public void onValueChanged(int position)
            {
                switch (position)
                {
                    case 0:
                        mTipValue = 0;
                        break;
                    case 1:
                        mTipValue = Params.TIP_OPTION_1;
                        break;
                    case 2:
                        mTipValue = Params.TIP_OPTION_2;
                        break;
                    case 3:
                        mTipValue = Params.TIP_OPTION_3;
                        break;
                }

                mTipTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mTipValue)));
                mTotalPriceTextView.setText(String.format(getString(R.string.price_value), String.format(Params.CURRENCY_FORMAT, mOrder.getTotalPrice() + mTipValue)));
            }
        });
    }

    private void setData()
    {
        if (ContentManager.getInstance().isAppInSellerMode())
        {
            mNameTextView.setText(mOrder.getCustomer().getName());
            UIManager.getInstance().loadImage(this, mOrder.getCustomer().getImageLink(), mImageView, ImageType.PROFILE);
        }
        else
        {
            mNameTextView.setText(mOrder.getStore().getName());
            UIManager.getInstance().loadImage(this, mOrder.getStore().getPhotoGallery().getMedium(), mImageView, ImageType.STORE);
        }

        mProductsPriceTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mOrder.getProductsSubtotal())));
        mDeliveryPriceTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mOrder.getDeliveryPrice())));
        mTipTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mOrder.getTip())));
        mTotalPriceTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mOrder.getTotalPriceAsString())));
    }

    private void setItemsAdapter()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.LIST_VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        OrderReceiptRecyclerAdapter orderReceiptRecyclerAdapter = new OrderReceiptRecyclerAdapter(this, mOrder);
        mRecyclerView.setAdapter(orderReceiptRecyclerAdapter);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.payButton:
                updateTipAndPay();
                break;
        }
    }

    private void updateTipAndPay()
    {
        if (mTipValue > 0)
        {
            // Update tip on server and just after that, pay
            mProgressBarLayout.setVisibility(View.VISIBLE);

            // Update server about the tip
            RestClientManager.getInstance().tipOrder(mOrder.getID(), mTipValue, new RequestCreateOrderListener()
            {
                @Override
                public void onSuccess(Order order)
                {
                    mOrder = order;
                    mTipTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mOrder.getTip())));
                    mTotalPriceTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mOrder.getTotalPriceAsString())));
                    mProgressBarLayout.setVisibility(View.GONE);
                    pay();
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
        else
        {
            pay();
        }
    }

    private void pay()
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);

        UpdateOrderStateRequest updateOrderStateRequest = new UpdateOrderStateRequest(OrderState.CLOSED.getRelatedAction());
        RestClientManager.getInstance().updateOrderState(mOrder.getID(), updateOrderStateRequest, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                Toast.makeText(getApplicationContext(), getString(R.string.order_paid), Toast.LENGTH_LONG).show();
                mPayButton.setVisibility(View.GONE);
                mProgressBarLayout.setVisibility(View.GONE);
                RatingStoreDialog ratingDialog = RatingStoreDialog.newInstance(mOrder);
                ratingDialog.show(getFragmentManager(), RatingStoreDialog.class.getSimpleName());

                AnalyticsManager.getInstance().purchase(Float.valueOf(mOrder.getTotalPriceAsString()));
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mProgressBarLayout, getString(R.string.error_in_payment_details), Snackbar.LENGTH_SHORT);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mProgressBarLayout, getString(R.string.error_in_payment_details), Snackbar.LENGTH_SHORT);
            }
        });
    }
}
