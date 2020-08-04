package co.thenets.brisk.general;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.regex.Pattern;

import co.thenets.brisk.R;

/**
 * Created by Roei on 21-Dec-14.
 */
public class Utils
{
    private static final String LOG_TAG = Utils.class.getSimpleName();

    public static void hideKeyboardWithContext(Context context, View view)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(View view, Activity activity)
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

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static boolean isLocationPermissionGiven(Context context)
    {
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED && hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }

        return true;
    }

    public static boolean isDeviceLocationTurnsOn(Context context)
    {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            // location provider isn't enabled
            return false;
        }
        else
        {
            // location provider enabled
            return true;
        }
    }


    public static boolean isMNC()
    {
        /*
         TODO: In the Android M Preview release, checking if the platform is M is done through
         the codename, not the version code. Once the API has been finalised, the following check
         should be used: */
        // return Build.VERSION.SDK_INT >= Build.VERSION_CODES.MNC

        return "MNC".equals(Build.VERSION.CODENAME);
    }

    /**
     * Writes Serializable object into a file
     */
    public static void writeObjectToFile(Context context, Object object, String filename)
    {
        ObjectOutputStream objectOut = null;
        try
        {
            FileOutputStream fileOut = context.openFileOutput(filename, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            fileOut.getFD().sync();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (objectOut != null)
            {
                try
                {
                    objectOut.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Reads a Serializable object from a file
     */
    public static Object readObjectFromFile(Context context, String filename)
    {
        ObjectInputStream objectIn = null;
        Object object = null;
        try
        {
            FileInputStream fileIn = context.getApplicationContext().openFileInput(filename);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        }
        catch (FileNotFoundException e)
        {
            // Do nothing
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (objectIn != null)
            {
                try
                {
                    objectIn.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return object;
    }

    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int dpToPx(Context context, float dp)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + 0.5f);
    }


    public static boolean isEmailValid(String email)
    {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static String getDateAndTimeFromEpoch(long timeStamp)
    {
        Date time = new Date(timeStamp * 1000);
        String date = DateFormat.format("dd/MM/yy HH:mm", time).toString();
        return date;
    }

    public static String formatDouble(double d)
    {
        if (d == (long) d)
        {
            return String.format("%d", (long) d);
        }
        else
        {
            return String.format("%s", d);
        }
    }

    public static double roundDouble(double value)
    {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        return Double.parseDouble(decimalFormat.format(value));
    }

    public static byte[] getByteArrayFromImageView(ImageView imageView)
    {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Drawable getDrawableFromByteArray(Context context, byte[] byteArray)
    {
        Drawable drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        return drawable;
    }


    public static int convertDecColorToHex(int decColor)
    {
        String hexColor = Integer.toHexString(decColor);
        return Color.parseColor("#" + hexColor);
    }

    public static int distance(Location from, Location to)
    {
        return (int) from.distanceTo(to);
    }

    public static Bitmap convertBitmapToGrayScale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static byte[] fileToByteArray(File file)
    {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try
        {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bytes;
    }

    public static void openDialer(Context context, String phone)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    public static void sendSMS(Context context, String phone, String body)
    {
        Uri uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        if (!TextUtils.isEmpty(body))
        {
            intent.putExtra("sms_body", body);
        }
        context.startActivity(intent);
    }

    public static void sendMail(Context context, String to, String subject, String body)
    {
        Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
        intentEmail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentEmail.setData(Uri.parse(context.getString(R.string.mailto) + to));
        intentEmail.putExtra(Intent.EXTRA_EMAIL, to);
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, subject);
        intentEmail.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(intentEmail);
    }

    public static void navigateToWithWaze(Context context, String address)
    {
        try
        {
            String url = "waze://?q=" + address;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException ex)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
            context.startActivity(intent);
        }
    }

    public static void shareMessage(Context context, String body)
    {
        Intent intentMessage = new Intent(Intent.ACTION_SEND);
        intentMessage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentMessage.setType("text/plain");
        intentMessage.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(intentMessage);
    }




}
