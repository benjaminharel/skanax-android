package co.thenets.brisk.activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.OrderReceiptRecyclerAdapter;
import co.thenets.brisk.custom.DividerItemDecoration;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Order;

public class ReceiptActivity extends BaseActivity
{
    private RecyclerView mRecyclerView;
    private TextView mNameTextView;
    private ImageView mImageView;
    private TextView mProductsPriceTextView;
    private TextView mDeliveryPriceTextView;
    private TextView mTipTextView;
    private TextView mTotalPriceTextView;
    private Order mOrder;
    private TextView mCancelTextView;
    private View mCancelLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

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
        mCancelTextView = (TextView) findViewById(R.id.cancelTextView);
        mCancelLayer = findViewById(R.id.cancelLayer);
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

        // Set order canceled views if needed
        switch (mOrder.getState())
        {
            case CANCELED:
                mCancelTextView.setVisibility(View.VISIBLE);
                mCancelLayer.setVisibility(View.VISIBLE);
                break;
            default:
                mCancelTextView.setVisibility(View.GONE);
                mCancelLayer.setVisibility(View.GONE);
                break;
        }
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
}
