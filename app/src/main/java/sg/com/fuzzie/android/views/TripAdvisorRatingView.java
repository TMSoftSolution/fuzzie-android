package sg.com.fuzzie.android.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.View;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by vincentpaing on 12/13/16.
 */

public class TripAdvisorRatingView extends View {

  int rating = 2;
  int maxRating = 5;
  Bitmap ratedBitmap;
  Bitmap unratedBitmap;
  Bitmap tripAdvisorBitmap;

  public TripAdvisorRatingView(Context context) {
    super(context);
    init();
  }

  public TripAdvisorRatingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public TripAdvisorRatingView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    ratedBitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_trip_rating_full_12dp);
    unratedBitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_trip_rating_empty_12dp);
    tripAdvisorBitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_trip_advisor);
  }

  public void setRating(int rating) {
    this.rating = rating;
    invalidate();
  }

  public void setMaxRating(int maxRating) {
    this.maxRating = maxRating;
    invalidate();
  }

  public int getRating() {
    return rating;
  }

  public int getMaxRating() {
    return maxRating;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int dp3 = (int) ViewUtils.convertDpToPixel(3, getContext());
    int width = (ratedBitmap.getWidth() * maxRating) + tripAdvisorBitmap.getWidth() + (dp3 * maxRating) + (int) ViewUtils.convertDpToPixel(6, getContext());
    int height = tripAdvisorBitmap.getHeight();

    setMeasuredDimension(width, height);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    //Draw Rated bar
    int tripAdvisorWidth = tripAdvisorBitmap.getWidth();
    canvas.drawBitmap(tripAdvisorBitmap, 0, 0, null);

    int dp3 = (int) ViewUtils.convertDpToPixel(3, getContext());
    int x = tripAdvisorWidth + (int) ViewUtils.convertDpToPixel(6, getContext());
    int y = (int) ((tripAdvisorBitmap.getHeight() / 2f) - (ratedBitmap.getHeight() / 2f));
    //Draw rated View
    for (int i = 0; i < rating; i++) {
      canvas.drawBitmap(ratedBitmap, x, y, null);
      x += ratedBitmap.getWidth() + dp3;
    }

    //Draw unrated view
    for (int i = 0; i < maxRating - rating; i++) {
      canvas.drawBitmap(unratedBitmap, x, y, null);
      x += unratedBitmap.getWidth() + dp3;
    }
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
