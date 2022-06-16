package sg.com.fuzzie.android.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.View;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by mac on 6/5/17.
 */

public class IndicatorView extends View {

    boolean select = false;
    Bitmap selectedBitmap;
    Bitmap unSelectedBitmap;
    Bitmap indicatorBitmap;
    int deltaX = 3;
    int deltaY = 0;

    public IndicatorView(Context context) {
        super(context);
        init();
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        selectedBitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_square_indicator_selected);
        unSelectedBitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_square_indicator);
    }

    public void setSelect(boolean select){
        this.select = select;
        invalidate();
    }

    public void setDeltaX(int deltaX){
        this.deltaX = deltaX;
        invalidate();
    }

    public void setDeltaY(int deltaY){
        this.deltaY = deltaY;
        invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int deltaX = (int) ViewUtils.convertDpToPixel(this.deltaX, getContext());
        int width = selectedBitmap.getWidth() + 2 * deltaX;

        int deltaY = (int) ViewUtils.convertDpToPixel(this.deltaY, getContext());
        int height = selectedBitmap.getHeight() + 2 *deltaY;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        indicatorBitmap = select? selectedBitmap : unSelectedBitmap;

        int deltaX = (int) ViewUtils.convertDpToPixel(this.deltaX, getContext());
        canvas.drawBitmap(indicatorBitmap, deltaX, 0, null);

    }

    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
