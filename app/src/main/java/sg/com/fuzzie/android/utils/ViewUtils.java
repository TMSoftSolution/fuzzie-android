package sg.com.fuzzie.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;

/**
 * Created by vincentpaing on 12/13/16.
 */

public class ViewUtils {

  public static void toggle(View... v) {
    for (View view : v) {
      view.setEnabled(!view.isEnabled());
    }
  }

  public static void visible(View... v) {
    for (View view : v) {
      view.setVisibility(View.VISIBLE);
    }
  }

  public static void visible(MenuItem... items) {
    for (MenuItem menuItem : items) {
      menuItem.setVisible(true);
    }
  }

  public static void invisible(View... v) {
    for (View view : v) {
      view.setVisibility(View.INVISIBLE);
    }
  }

  public static void gone(View... v) {
    for (View view : v) {
      view.setVisibility(View.GONE);
    }
  }

  public static void gone(MenuItem... items) {
    for (MenuItem menuItem : items) {
      menuItem.setVisible(false);
    }
  }

  public static void swap(View hide, View show) {
    hide.setVisibility(View.INVISIBLE);
    show.setVisibility(View.VISIBLE);
  }

  public static void setVisibiliy(View view, boolean isVisible) {
    if (isVisible) {
      view.setVisibility(View.VISIBLE);
    } else {
      view.setVisibility(View.GONE);
    }
  }



  /**
   * This method converts dp unit to equivalent pixels, depending on device density.
   *
   * @param dp A value in dp (density independent pixels) unit. Which we need to convert into
   * pixels
   * @param context Context to get resources and device specific display metrics
   * @return A float value to represent px equivalent to dp depending on device density
   */
  public static float convertDpToPixel(float dp, Context context) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return px;
  }

  /**
   * This method converts device specific pixels to density independent pixels.
   *
   * @param px A value in px (pixels) unit. Which we need to convert into db
   * @param context Context to get resources and device specific display metrics
   * @return A float value to represent dp equivalent to px value
   */
  public static float convertPixelsToDp(float px, Context context) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return dp;
  }

  public static int getScreenWidth(Context context){
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    return size.x;
  }

  public static float getScreenWidthWithDp(Context context){
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    return convertPixelsToDp((float)(size.x), context);
  }

  public static int getLikesCellCount(Context context, int length){

    int cellCount =(int) ((getScreenWidthWithDp(context) - 90 - 10 * length) / 50);

    return cellCount;
  }

  public static int getTicketCellCount(Context context){
    int cellCount =(int) ((getScreenWidthWithDp(context)) / 70);

    return cellCount;
  }

  public static void slideUp(View view, long duration){

    TranslateAnimation animate = new TranslateAnimation(0,0, view.getHeight(), 0);
    animate.setDuration(duration);
    animate.setFillAfter(true);
    view.startAnimation(animate);
    view.setVisibility(View.VISIBLE);

  }

  public static void slideDown(View view, long duration){

    TranslateAnimation animate = new TranslateAnimation(0,0, 0, view.getHeight());
    animate.setDuration(duration);
    animate.setFillAfter(true);
    view.startAnimation(animate);
    view.setVisibility(View.GONE);

  }

}
