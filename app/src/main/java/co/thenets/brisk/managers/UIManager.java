package co.thenets.brisk.managers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Random;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.SplashActivity;
import co.thenets.brisk.activities.StoreMainActivity;
import co.thenets.brisk.custom.CircleTransform;
import co.thenets.brisk.dialogs.NoLocationDialog;
import co.thenets.brisk.dialogs.NoNetworkDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.UiState;
import co.thenets.brisk.general.Utils;


/**
 * Created by DAVID BELOOSESKY on 03/12/2014 - 17:03.
 */
public class UIManager
{
    private final Context mContext;
    private Activity mActivityContext;
    private static UIManager msInstance;
    public static final String LOG_TAG = UIManager.class.getSimpleName();

    private UIManager(Context context)
    {
        mContext = context;
    }

    public static UIManager init(Context context)
    {
        if (msInstance == null)
        {
            msInstance = new UIManager(context);
        }

        return msInstance;
    }

    public static UIManager getInstance()
    {
        return msInstance;
    }


    public void loadImage(Context context, final String imageUrl, ImageView imageView, ImageType imageType, byte[]... placeHolderByteArrayArg)
    {
        byte[] placeHolderByteArray = null;

        if(placeHolderByteArrayArg.length > 0)
        {
            placeHolderByteArray = placeHolderByteArrayArg[0];
        }


        if (placeHolderByteArray != null && !TextUtils.isEmpty(imageUrl))
        {
            // There is a special place holder, like a low res image for example
            Drawable placeholderDrawable = Utils.getDrawableFromByteArray(context, placeHolderByteArray);

            Picasso.with(context)
                    .load(imageUrl)
                    .placeholder(placeholderDrawable)
                    .error(placeholderDrawable)
                    .into(imageView, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            // DO NOTHING
                        }

                        @Override
                        public void onError()
                        {
                            // report the bad image error to server
                            Log.e(LOG_TAG, "Error in display next image: " + imageUrl);
                        }
                    });

        }
        else
        {
            int placeholderDrawable;

            // No special place holder
            switch (imageType)
            {
                case PROFILE:
                    placeholderDrawable = R.drawable.ic_place_holder_profile;
                    break;
                case PRODUCT:
                    placeholderDrawable = R.drawable.ic_place_holder_product;
                    break;
                case STORE:
                    placeholderDrawable = R.drawable.ic_place_holder_store;
                    break;
                case SPLASH_IMAGE:
                    placeholderDrawable = R.drawable.ic_place_holder_user;
                    break;
                default:
                    placeholderDrawable = R.drawable.ic_place_holder;
                    break;
            }

            if (!TextUtils.isEmpty(imageUrl))
            {
                Picasso.with(context)
                        .load(imageUrl)
                        .placeholder(placeholderDrawable)
                        .error(placeholderDrawable)
                        .into(imageView, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            {
                                // DO NOTHING
                            }

                            @Override
                            public void onError()
                            {
                                // report the bad image error to server
                                Log.e(LOG_TAG, "Error in display next image: " + imageUrl);
                            }
                        });
            }
            else
            {
                imageView.setImageResource(placeholderDrawable);
            }
        }
    }

    public void loadImage(Context context, final String imageUrl, ImageView imageView, Callback callback, ImageType imageType)
    {
        int placeholderDrawable;
        switch (imageType)
        {
            case PROFILE:
                placeholderDrawable = R.drawable.ic_place_holder_profile;
                break;
            case PRODUCT:
                placeholderDrawable = R.drawable.ic_place_holder_product;
                break;
            case STORE:
                placeholderDrawable = R.drawable.ic_place_holder_store;
                break;
            case SPLASH_IMAGE:
                placeholderDrawable = R.drawable.ic_place_holder_user;
                break;
            default:
                placeholderDrawable = R.drawable.ic_place_holder;
                break;
        }

        if (!TextUtils.isEmpty(imageUrl))
        {
            Picasso.with(context)
                    .load(imageUrl)
                    .placeholder(placeholderDrawable)
                    .error(placeholderDrawable)
                    .into(imageView, callback);
        }
        else
        {
            imageView.setImageResource(placeholderDrawable);
        }
    }

    public void loadCircleImage(Context context, final String imageUrl, ImageView imageView)
    {
        Picasso.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_face_white_48dp)
                .transform(new CircleTransform())
                .error(R.drawable.ic_face_white_48dp)
                .into(imageView, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {
                        // DO NOTHING
                    }

                    @Override
                    public void onError()
                    {
                        // report the bad image error to server
                        Log.e(LOG_TAG, "Error in display next image: " + imageUrl);
                    }
                });
    }

    public void displaySnackBar(View view, String value, int duration)
    {
        Snackbar snackbar = Snackbar.make(view, value, duration);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_accent));
        snackbar.show();
    }
    public void displaySnackBarWithPrimary(View view, String value, int duration)
    {
        Snackbar snackbar = Snackbar.make(view, value, duration);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_primary));
        snackbar.show();
    }

    public void displaySnackBarError(View view, String value, int duration)
    {
        Snackbar snackbar = Snackbar.make(view, value, duration);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
        snackbar.show();
    }

    public void moveToState(Activity activity, UiState newState)
    {
        Intent intent = null;
        switch (newState)
        {
            case CUSTOMER:
                ContentManager.getInstance().setIsAppInSellerMode(false);
                intent = new Intent(activity, SplashActivity.class);
                break;
            case STORE:
                ContentManager.getInstance().setIsAppInSellerMode(true);
                intent = new Intent(activity, StoreMainActivity.class);
                break;
        }
        activity.startActivity(intent);
        activity.finish();
    }

    public void applyRollOutDropOutAnimation(final View targetView)
    {
        YoYo.with(Techniques.RollOut)
                .duration(500)
                .interpolate(new AccelerateInterpolator())
                .withListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        YoYo.with(Techniques.DropOut)
                                .duration(700)
                                .interpolate(new AccelerateDecelerateInterpolator())
                                .playOn(targetView);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                })
                .playOn(targetView);
    }

    public void applyHingeDropOutAnimation(final View targetView)
    {
        YoYo.with(Techniques.Hinge)
                .duration(300)
                .withListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        YoYo.with(Techniques.DropOut)
                                .duration(700)
                                .interpolate(new AccelerateDecelerateInterpolator())
                                .playOn(targetView);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                })
                .playOn(targetView);
    }


    public void applyTaDaAnimation(final View targetView)
    {
        YoYo.with(Techniques.Tada)
                .duration(600)
                .playOn(targetView);
    }

    public void applyBounceAnimation(final View targetView)
    {
        YoYo.with(Techniques.Bounce)
                .duration(800)
                .playOn(targetView);
    }

    public void hideKeyboard(View view, Activity activity)
    {
        try
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Exception ex)
        {
            Log.e(LOG_TAG, ex.toString());
        }
    }

    public int getRandomHashtagColor(Context context)
    {
        int[] hashtagColors = context.getResources().getIntArray(R.array.hashtag_colors);
        int randomColor = hashtagColors[new Random().nextInt(hashtagColors.length)];
        return randomColor;
    }

    public int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int height = metrics.heightPixels;
        return height;
    }

    public int getActionBarHeight(Context context)
    {
        // Calculate ActionBar height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        return actionBarHeight;
    }

    public int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setActivityContext(Activity activityContext)
    {
        mActivityContext = activityContext;
    }

    public void displayNoNetworkDialog()
    {
        if (mActivityContext != null)
        {
            NoNetworkDialog noNetworkDialog = NoNetworkDialog.newInstance();
            noNetworkDialog.setCancelable(false);
            noNetworkDialog.show((mActivityContext).getFragmentManager(), NoNetworkDialog.class.getSimpleName());
        }
    }

    public void displayNoLocationDialog()
    {
        if (mActivityContext != null)
        {
            // Check to not open the Dialog twice
            Fragment prevNoLocationDialog = mActivityContext.getFragmentManager().findFragmentByTag(NoLocationDialog.class.getSimpleName());
            if (prevNoLocationDialog == null)
            {
                // There is no active fragment with tag "NoLocationDialog"
                NoLocationDialog noLocationDialog = NoLocationDialog.newInstance();
                noLocationDialog.setCancelable(false);
                noLocationDialog.show((mActivityContext).getFragmentManager(), NoLocationDialog.class.getSimpleName());
            }
        }
    }
}




