package co.thenets.brisk.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import net.wujingchao.android.view.SimpleTagImageView;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.OrderRecyclerAdapter;
import co.thenets.brisk.custom.CircleView;
import co.thenets.brisk.dialogs.ConfirmationDialog;
import co.thenets.brisk.dialogs.ReportDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.OrderState;
import co.thenets.brisk.enums.RoleType;
import co.thenets.brisk.events.RefreshOrderActivityEvent;
import co.thenets.brisk.events.StoreUpdatedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.ConfirmationListener;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.PaymentMethod;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.UpdateOrderStateRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import co.thenets.brisk.services.UpdateLocationService;
import retrofit.RetrofitError;

public class StoreOrderActivity extends OrderActivity implements View.OnClickListener
{
    private static final int REQ_PICK_CONTACT = 901;
    private RecyclerView mRecyclerView;
    private Button mApproveOrderButton;
    private Button mCancelOrderButton;

    private CircleView mWaitingCircleView;
    private CircleView mInProgressCircleView;
    private CircleView mOtwCircleView;
    private CircleView mDeliveredCircleView;
    private CircleView mPaidCircleView;

    private ImageView mWaitingImageView;
    private ImageView mInProgressImageView;
    private ImageView mOtwImageView;
    private ImageView mDeliveredImageView;
    private ImageView mPaidImageView;

    private View mInProgressLineView;
    private View mOtwLineView;
    private View mDeliveredLineView;
    private View mPaidLineView;

    private Intent mUpdateLocationServiceIntent;
    private TextView mAddressTextView;
    private TextView mPaymentMethodTextView;
    private ImageView mPaymentMethodImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_order);

        setViews();
        EventsManager.getInstance().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_store_order_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == R.id.action_order_transfer)
        {
            // Show transfer order Dialog
            askForTransferAnOrder();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void askForTransferAnOrder()
    {
        String title = getString(R.string.order_transfer);
        String message = getString(R.string.order_transfer_description);
        ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance(title, message, new ConfirmationListener()
        {
            @Override
            public void onApprove()
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQ_PICK_CONTACT);
            }

            @Override
            public void onCancel()
            {
                // Do nothing
            }
        });
        confirmationDialog.show(getFragmentManager(), ConfirmationDialog.class.getSimpleName());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getOrder();
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    protected void setOtherViews()
    {
        super.setOtherViews();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mApproveOrderButton = (Button) findViewById(R.id.positiveOrderButton);
        mCancelOrderButton = (Button) findViewById(R.id.negativeOrderButton);
        mWaitingCircleView = (CircleView) findViewById(R.id.waitingCircleView);
        mInProgressCircleView = (CircleView) findViewById(R.id.inProgressCircleView);
        mOtwCircleView = (CircleView) findViewById(R.id.otwCircleView);
        mDeliveredCircleView = (CircleView) findViewById(R.id.deliveredCircleView);
        mPaidCircleView = (CircleView) findViewById(R.id.paidCircleView);

        mWaitingImageView = (ImageView) findViewById(R.id.waitingImageView);
        mInProgressImageView = (ImageView) findViewById(R.id.inProgressImageView);
        mOtwImageView = (ImageView) findViewById(R.id.otwImageView);
        mDeliveredImageView = (ImageView) findViewById(R.id.deliveredImageView);
        mPaidImageView = (ImageView) findViewById(R.id.paidImageView);

        mInProgressLineView = findViewById(R.id.inProgressLineView);
        mOtwLineView = findViewById(R.id.otwLineView);
        mDeliveredLineView = findViewById(R.id.deliveredLineView);
        mPaidLineView = findViewById(R.id.paidLineView);

        mApproveOrderButton.setOnClickListener(this);
        mCancelOrderButton.setOnClickListener(this);
        mCanceledImageView = (SimpleTagImageView) findViewById(R.id.canceledImageView);

        mCallButton = (ImageButton) findViewById(R.id.callButton);
        mSmsButton = (ImageButton) findViewById(R.id.smsButton);
        mAddressTextView = (TextView) findViewById(R.id.addressTextView);
        mCallButton.setOnClickListener(this);
        mSmsButton.setOnClickListener(this);
        mAddressTextView.setOnClickListener(this);

        mPaymentMethodTextView = (TextView) findViewById(R.id.paymentMethodTextView);
        mPaymentMethodImageView = (ImageView) findViewById(R.id.paymentMethodImageView);
    }

    @Override
    protected void setAddress()
    {
        mAddressTextView.setText(mOrder.getAddress().getFullAddressForDisplay(this));
    }

    protected void setViewsVisibility()
    {
        setOrderItemList();
        switch (mOrder.getState())
        {
            case OPEN:
                mApproveOrderButton.setText(getString(R.string.approve));
                mCancelOrderButton.setText(getString(R.string.cancel));
                mCancelOrderButton.setVisibility(View.VISIBLE);
                break;
            case PROCESSING:
                setInProgressOn();
                break;
            case ON_THE_WAY:
                setInProgressOn();
                setOtwOn();
                break;
            case DELIVERED:
                setInProgressOn();
                setOtwOn();
                setDeliveredOn();
                break;
            case CLOSED:
                setInProgressOn();
                setOtwOn();
                setDeliveredOn();
                setPaidOn();
                break;
            case CANCELED:
                setCancelOn();
                break;
            case DISPUTE:
                setDisputeOn();
                break;
        }
    }

    private void setInProgressOn()
    {
        mApproveOrderButton.setText(getString(R.string.on_the_way));
        mCancelOrderButton.setVisibility(View.GONE);

        mInProgressCircleView.setFillColor(ContextCompat.getColor(this, R.color.color_primary));
        mInProgressCircleView.setBorderColor(ContextCompat.getColor(this, R.color.color_primary));
        mInProgressLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary));
        mInProgressImageView.setImageResource(R.drawable.ic_in_progress_on);
    }

    private void setInProgressOff()
    {
        mInProgressCircleView.setFillColor(ContextCompat.getColor(this, R.color.background_gray));
        mInProgressCircleView.setBorderColor(ContextCompat.getColor(this, R.color.light_gray));
        mInProgressLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        mInProgressImageView.setImageResource(R.drawable.ic_in_progress_off);
    }

    private void setOtwOn()
    {
        mApproveOrderButton.setText(getString(R.string.delivered));
        mCancelOrderButton.setVisibility(View.GONE);

        mOtwCircleView.setFillColor(ContextCompat.getColor(this, R.color.color_primary));
        mOtwCircleView.setBorderColor(ContextCompat.getColor(this, R.color.color_primary));
        mOtwLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary));
        mOtwImageView.setImageResource(R.drawable.ic_otw_on);
    }

    private void setOtwOff()
    {
        mOtwCircleView.setFillColor(ContextCompat.getColor(this, R.color.background_gray));
        mOtwCircleView.setBorderColor(ContextCompat.getColor(this, R.color.light_gray));
        mOtwLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        mOtwImageView.setImageResource(R.drawable.ic_otw_off);
    }

    private void setDeliveredOn()
    {
        mCancelOrderButton.setText(getString(R.string.dispute));
        mCancelOrderButton.setVisibility(View.VISIBLE);
        mApproveOrderButton.setVisibility(View.GONE);

        mDeliveredCircleView.setFillColor(ContextCompat.getColor(this, R.color.color_primary));
        mDeliveredCircleView.setBorderColor(ContextCompat.getColor(this, R.color.color_primary));
        mDeliveredLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary));
        mDeliveredImageView.setImageResource(R.drawable.ic_delivered_on);
    }

    private void setDeliveredOff()
    {
        mDeliveredCircleView.setFillColor(ContextCompat.getColor(this, R.color.background_gray));
        mDeliveredCircleView.setBorderColor(ContextCompat.getColor(this, R.color.light_gray));
        mDeliveredLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        mDeliveredImageView.setImageResource(R.drawable.ic_delivered_off);
    }

    private void setPaidOn()
    {
        mCancelOrderButton.setVisibility(View.GONE);
        mApproveOrderButton.setVisibility(View.GONE);

        mPaidCircleView.setFillColor(ContextCompat.getColor(this, R.color.color_primary));
        mPaidCircleView.setBorderColor(ContextCompat.getColor(this, R.color.color_primary));
        mPaidLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary));
        mPaidImageView.setImageResource(R.drawable.ic_paid_on);
    }

    private void setCancelOn()
    {
        mCanceledImageView.setVisibility(View.VISIBLE);
        mCanceledImageView.setTagText(getString(R.string.canceled));
        mApproveOrderButton.setVisibility(View.GONE);
        mCancelOrderButton.setVisibility(View.GONE);

        mWaitingCircleView.setFillColor(ContextCompat.getColor(this, R.color.background_gray));
        mWaitingCircleView.setBorderColor(ContextCompat.getColor(this, R.color.light_gray));
        mWaitingImageView.setImageResource(R.drawable.ic_approval_off);

        setInProgressOff();
        setOtwOff();
        setDeliveredOff();
    }

    private void setDisputeOn()
    {
        mCanceledImageView.setVisibility(View.VISIBLE);
        mCanceledImageView.setTagText(getString(R.string.dispute));

        setInProgressOn();
        setOtwOn();
        setDeliveredOn();

        // The order matters, buttons visibility should be here, after all others!
        mApproveOrderButton.setVisibility(View.GONE);
        mCancelOrderButton.setVisibility(View.GONE);
    }

    private void setOrderItemList()
    {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        OrderRecyclerAdapter orderRecyclerAdapter = new OrderRecyclerAdapter(this, mOrder, RoleType.STORE);
        mRecyclerView.setAdapter(orderRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                switch (mOrder.getState())
                {
                    case OPEN:
                    case PROCESSING:
                        if (mRecyclerView.getAdapter().getItemCount() > 1)
                        {
                            ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance("", getString(R.string.are_you_sure_you_want_to_remove_this_item_from_list), new ConfirmationListener()
                            {
                                @Override
                                public void onApprove()
                                {
                                    // Update server on remove item, and if succeeded update also the UI
                                    mProgressBarLayout.setVisibility(View.VISIBLE);
                                    final String orderItemIDForDelete = mOrder.getOrderItemList().get(viewHolder.getAdapterPosition()).getID();
                                    RestClientManager.getInstance().removeItemFromOrder(msOrderID, orderItemIDForDelete, new RequestListener()
                                    {
                                        @Override
                                        public void onSuccess()
                                        {
                                            // Remove succeeded, update the model and the UI
                                            mOrder.removeOrderItem(orderItemIDForDelete);
                                            ((OrderRecyclerAdapter) mRecyclerView.getAdapter()).removeAt(viewHolder.getAdapterPosition());
                                            mProgressBarLayout.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onInternalServerFailure(ErrorResponse error)
                                        {
                                            mProgressBarLayout.setVisibility(View.GONE);
                                            mRecyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                                            UIManager.getInstance().displaySnackBarError(mRecyclerView, getString(R.string.cant_delete_last_item_please_cancel_order), Snackbar.LENGTH_SHORT);
                                        }

                                        @Override
                                        public void onNetworkFailure(RetrofitError error)
                                        {
                                            mProgressBarLayout.setVisibility(View.GONE);
                                            mRecyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                                            UIManager.getInstance().displaySnackBarError(mRecyclerView, getString(R.string.cant_delete_last_item_please_cancel_order), Snackbar.LENGTH_SHORT);
                                        }
                                    });
                                }

                                @Override
                                public void onCancel()
                                {
                                    mRecyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            });
                            confirmationDialog.show(getFragmentManager(), ConfirmationDialog.class.getSimpleName());
                        }
                        else
                        {
                            // It's the last item, can't delete last item
                            mRecyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                            UIManager.getInstance().displaySnackBarError(mRecyclerView, getString(R.string.cant_delete_last_item_please_cancel_order), Snackbar.LENGTH_SHORT);
                        }
                        break;

                    default:
                        // Can't delete item on this state
                        mRecyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                        UIManager.getInstance().displaySnackBarError(mRecyclerView, getString(R.string.cant_delete_item_on_this_state), Snackbar.LENGTH_SHORT);
                        break;
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void setHeaderDetails()
    {
        if (mOrder != null)
        {
            mNameTextView.setText(mOrder.getCustomer().getName());
            UIManager.getInstance().loadImage(this, mOrder.getCustomer().getImageLink(), mImageView, ImageType.PROFILE);

            PaymentMethod paymentMethod = mOrder.getPaymentMethod();

            switch (paymentMethod.getMethodType())
            {
                case CASH:
                    mPaymentMethodTextView.setText(String.format(getString(R.string.paying_with_cash), mOrder.getTotalPriceAsString()));
                    mPaymentMethodImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_paying_cash));
                    break;
                case CREDIT_CARD:
                    mPaymentMethodTextView.setText(String.format(getString(R.string.paying_with_credit_card), mOrder.getTotalPriceAsString()));
                    mPaymentMethodImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_paying_credit));
                    break;
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.positiveOrderButton:
                updateOrderState();
                break;
            case R.id.negativeOrderButton:
                switch (mOrder.getState())
                {
                    case OPEN:
                        cancelOrder();
                        break;
                    case DELIVERED:
                        ReportDialog reportDialog = ReportDialog.newInstance(mOrder);
                        reportDialog.show(getFragmentManager(), ReportDialog.class.getSimpleName());
                        break;
                }
                break;
            case R.id.callButton:
                Utils.openDialer(this, mOrder.getCustomer().getPhone());
                break;
            case R.id.smsButton:
                Utils.sendSMS(this, mOrder.getCustomer().getPhone(), "");
                break;
            case R.id.addressTextView:
                Utils.navigateToWithWaze(this, mOrder.getAddress().getAddressToNavigate());
                break;
        }
    }

    private void transferOrder(final String name, String number)
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        RestClientManager.getInstance().transferOrder(mOrder.getID(), number, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                mProgressBarLayout.setVisibility(View.INVISIBLE);
                UIManager.getInstance().displaySnackBar(mApproveOrderButton, "Order Transferred to: " + name, Snackbar.LENGTH_LONG);
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.INVISIBLE);
                UIManager.getInstance().displaySnackBar(mApproveOrderButton, "Order NOT Transferred", Snackbar.LENGTH_LONG);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.INVISIBLE);
                UIManager.getInstance().displaySnackBar(mApproveOrderButton, "Order NOT Transferred", Snackbar.LENGTH_LONG);
            }
        });
    }

    private void updateOrderState()
    {
        UpdateOrderStateRequest updateOrderStateRequest = new UpdateOrderStateRequest(mOrder.getState().getRelatedAction());
        RestClientManager.getInstance().updateOrderState(msOrderID, updateOrderStateRequest, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                moveToNextState();
                setViewsVisibility();
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

    private void moveToNextState()
    {
        switch (mOrder.getState())
        {
            case OPEN:
                mOrder.setState(OrderState.PROCESSING);
                break;
            case PROCESSING:
                // The order on OTW state
                mOrder.setState(OrderState.ON_THE_WAY);

                // Start service for update the server about current location
                mUpdateLocationServiceIntent = new Intent(this, UpdateLocationService.class);
                mUpdateLocationServiceIntent.putExtra(Params.ORDER, mOrder);
                startService(mUpdateLocationServiceIntent);
                break;
            case ON_THE_WAY:
                mOrder.setState(OrderState.DELIVERED);
                break;
            case DELIVERED:
                break;
        }
    }

    @Subscribe
    public void orderStateChanged(RefreshOrderActivityEvent refreshOrderActivityEvent)
    {
        getOrder();
    }


    @Subscribe
    public void onStoreUpdated(StoreUpdatedEvent storeUpdatedEvent)
    {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_PICK_CONTACT)
        {
            if (resultCode == RESULT_OK)
            {
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                transferOrder(name, number);
            }
        }
    }
}
