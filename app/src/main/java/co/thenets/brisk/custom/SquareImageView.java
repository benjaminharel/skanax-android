package co.thenets.brisk.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by DAVID on 06/03/2016.
 */
public class SquareImageView extends ImageView
{

    private static final String LOG_TAG = SquareImageView.class.getSimpleName();

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
