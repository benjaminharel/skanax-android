package co.thenets.brisk.activities;

import android.os.Bundle;
import android.view.MenuItem;

import co.thenets.brisk.R;
import co.thenets.brisk.dialogs.ConfirmationDialog;
import co.thenets.brisk.interfaces.ConfirmationListener;

public class AddNewProductActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_done:
                ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance("",getString(R.string.did_you_finish_with_adding_new_products), new ConfirmationListener()
                {
                    @Override
                    public void onApprove()
                    {
                        finish();
                    }

                    @Override
                    public void onCancel()
                    {
                        // Do nothing
                    }
                });
                confirmationDialog.show(getFragmentManager(), ConfirmationDialog.class.getSimpleName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
