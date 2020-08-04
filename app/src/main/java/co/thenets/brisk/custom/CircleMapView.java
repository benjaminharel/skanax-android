package co.thenets.brisk.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import co.thenets.brisk.R;

/**
 * Created by Roei on 09-Nov-15.
 */
public class CircleMapView extends View
{
    //circle and text colors
    private int mCircleCol, mLabelCol;
    //label text
    private String mCircleText;
    //paint for drawing custom view
    private Paint mCirclePaint;

    private int mRadius;

    private Paint mBorderPaint, mSmallCirclePaint, mTransparentCircle;


    public CircleMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // paint small circle
        mBorderPaint = new Paint();
        mSmallCirclePaint = new Paint();
        mTransparentCircle = new Paint();
        //paint object for drawing in onDraw
        mCirclePaint = new Paint();

        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CircleMapView, 0, 0);

        try

        {
            //get the text and colors specified using the names in attrs.xml
            mCircleText = a.getString(R.styleable.CircleMapView_circleLabel);
            mCircleCol = a.getInteger(R.styleable.CircleMapView_circleColor, 0);//0 is default
            mLabelCol = a.getInteger(R.styleable.CircleMapView_labelColor, 0);
        }

        finally

        {
            a.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;

        //get the radius as half of the width or height, whichever is smaller
        int radius = 0;
        if (viewWidthHalf > viewHeightHalf)
        {
            radius = viewHeightHalf - 30;
        }
        else
        {
            radius = viewWidthHalf - 30;
        }

        if (radius != 0)
        {
            mRadius = radius;
        }

        mTransparentCircle.setStyle(Paint.Style.FILL);
        mTransparentCircle.setAntiAlias(false);

        mTransparentCircle.setColor(Color.argb(180, 255, 255, 255));
//        mTransparentCircle.setColor();

        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, mTransparentCircle);

        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(4);
        mCirclePaint.setAntiAlias(true);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_primary));

        mBorderPaint.setStrokeWidth(2);
        mBorderPaint.setAntiAlias(true);

        canvas.drawCircle(viewWidthHalf, viewHeightHalf, 25, mBorderPaint);

        mSmallCirclePaint.setStyle(Paint.Style.FILL);
        mSmallCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_primary));
        mSmallCirclePaint.setAntiAlias(true);
        mSmallCirclePaint.setAlpha(255);
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, 20, mSmallCirclePaint);

        //set the paint color using the circle color specified
        mCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_primary));

        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, mCirclePaint);

        //set the text color using the color specified
        mBorderPaint.setColor(mLabelCol);

        //set text properties
        mSmallCirclePaint.setTextAlign(Paint.Align.CENTER);

        mSmallCirclePaint.setTextSize(50);

        //draw the text using the string attribute and chosen properties
        canvas.drawText(mCircleText, viewWidthHalf, viewHeightHalf - 60, mSmallCirclePaint);
    }

    public int getRadius()
    {
        return mRadius;
    }

    public void setCircleColor(int newColor)
    {
        //update the instance variable
        mCircleCol = newColor;
        //redraw the view
        invalidate();
        requestLayout();
    }

    public void setLabelColor(int newColor)
    {
        //update the instance variable
        mLabelCol = newColor;
        //redraw the view
        invalidate();
        requestLayout();
    }

    public void setLabelText(String newLabel)
    {
        //update the instance variable
        mCircleText = newLabel;
        //redraw the view
        invalidate();
        requestLayout();
    }
}
