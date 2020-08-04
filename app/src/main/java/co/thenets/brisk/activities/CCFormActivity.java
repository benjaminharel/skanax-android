package co.thenets.brisk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.thepinkandroid.cardform.OnCardFormSubmitListener;
import com.thepinkandroid.cardform.view.CardForm;

import co.thenets.brisk.R;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.models.BriskCreditCard;

public class CCFormActivity extends BaseActivity implements OnCardFormSubmitListener
{
    private CardForm mCardForm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);

        setToolbar();
        setOtherViews();
    }


    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.credit_card_details));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setOtherViews()
    {
        mCardForm = (CardForm) findViewById(R.id.card_form);
        mCardForm.setRequiredFields(this, true, true, true, false, getString(R.string.add));
        mCardForm.setOnCardFormSubmitListener(this);
    }

    @Override
    public void onCardFormSubmit()
    {
        if (mCardForm.isValid())
        {
            BriskCreditCard creditCard = new BriskCreditCard(mCardForm.getCardNumber(), mCardForm.getExpirationMonth(), mCardForm.getExpirationYear(), mCardForm.getCvv());
            Intent intent = new Intent();
            intent.putExtra(Params.CC_FORM_EXTRA_DATA, creditCard);
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
