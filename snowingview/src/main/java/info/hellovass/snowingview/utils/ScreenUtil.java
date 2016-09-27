package info.hellovass.snowingview.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by HelloVass on 16/3/3.
 */
public class ScreenUtil {

  private ScreenUtil() {

  }

  /**
   * 得到屏幕的宽度
   *
   * @param context 上下文
   * @return 屏幕的宽度，单位像素
   */
  public static int getScreenWidth(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return displayMetrics.widthPixels;
  }

  /**
   * 得到屏幕的高度
   *
   * @param context 上下文
   * @return 屏幕的高度，单位像素
   */
  public static int getScreenHeight(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return displayMetrics.heightPixels;
  }
}

