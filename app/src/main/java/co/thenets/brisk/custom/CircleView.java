package co.thenets.brisk.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import co.thenets.brisk.R;

/**
 * Created by DAVID-WORK on 20/01/2016.
 */
public class CircleView extends View
{
    private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_BORDER_WIDTH = 6;
    private int mFillColor = DEFAULT_FILL_COLOR;
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private Paint mCirclePaint;
    private Paint mBorderPaint;

    public CircleView(Context context)
    {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // paint object for drawing in onDraw
        mCirclePaint = new Paint();
        mBorderPaint = new Paint();

        // get the attributes specified in attrs.xml using the name we included
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleView, 0, 0);

        try
        {
            //get the text and colors specified using the names in attrs.xml
            mFillColor = typedArray.getInteger(R.styleable.CircleView_fill_color, DEFAULT_FILL_COLOR);
            mBorderColor = typedArray.getInteger(R.styleable.CircleView_border_color, DEFAULT_BORDER_COLOR);
            mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.CircleView_border_width, DEFAULT_BORDER_WIDTH);
        }
        finally
        {
            typedArray.recycle();
        }
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // get half of the width and height as we are working with a circle
        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;

        // get the radius as half of the width or height, whichever is smaller
        // subtract ten so that it has some space around it
        int radius = 0;
        if (viewWidthHalf > viewHeightHalf)
        {
            radius = viewHeightHalf - 10;
        }
        else
        {
            radius = viewWidthHalf - 10;
        }

        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        // set the paint color using the circle color specified
        mCirclePaint.setColor(mFillColor);

        // Draw the circle
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, mCirclePaint);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);

        // set the paint color using the circle color specified
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        // Draw the circle
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, mBorderPaint);
    }

    public int getFillColor()
    {
        return mFillColor;
    }

    public void setFillColor(int newFillColor)
    {
        // update the instance variable
        mFillColor = newFillColor;

        // redraw the view
        invalidate();
        requestLayout();
    }

    public int getBorderColor()
    {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor)
    {
        mBorderColor = borderColor;

        // redraw the view
        invalidate();
        requestLayout();
    }
}
