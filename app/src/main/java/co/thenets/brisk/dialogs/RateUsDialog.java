package co.thenets.brisk.dialogs;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import co.thenets.brisk.R;

public class RateUsDialog extends DialogFragment
{
    private View mRootView;
    private RatingBar mRatingBar;
    private TextView mRatingBarText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        setViews();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(mRootView)
                .setTitle(R.string.love_it)
                .setMessage(R.string.we_are_more_than_interested_to_hear_your_opinion_about_our_application)
                //Add action button
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (mRatingBar.getRating() >= 4)
                        {
                            RateUsOnStoreDialog rateUsOnStoreDialog = new RateUsOnStoreDialog();
                            rateUsOnStoreDialog.show(getActivity().getFragmentManager(), RateUsOnStoreDialog.class.getSimpleName());
                        }

                        else
                        {
                            Toast.makeText(getActivity(), getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return builder.create();
    }

    private void setViews()
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mRootView = inflater.inflate(R.layout.dialog_rate_us, null);
        mRatingBar = (RatingBar) mRootView.findViewById(R.id.ratingBar);
        mRatingBarText = (TextView) mRootView.findViewById(R.id.ratingBarTextView);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                if (rating == 1)
                {
                    mRatingBarText.setText(getString(R.string.its_crap));
                }
                else if (rating == 2)
                {
                    mRatingBarText.setText(getString(R.string.disliked_it));
                }
                else if (rating == 3)
                {
                    mRatingBarText.setText(getString(R.string.its_fine));
                }
                else if (rating == 4)
                {
                    mRatingBarText.setText(getString(R.string.i_love_it));
                }
                else if (rating == 5)
                {
                    mRatingBarText.setText(getString(R.string.one_of_a_kind));
                }
            }
        });
    }
}
