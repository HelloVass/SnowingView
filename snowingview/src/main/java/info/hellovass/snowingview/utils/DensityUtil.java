package info.hellovass.snowingview.utils;

import android.content.Context;

/**
 * Created by HelloVass on 16/1/15.
 *
 * 分辨率转换工具
 */
public class DensityUtil {

  private DensityUtil() {

  }

  /**
   * 将 px 转换为 dp
   *
   * @param context 上下文
   * @param px 像素
   * @return dp
   */
  public static int px2dip(Context context, float px) {

    float scale = context.getResources().getDisplayMetrics().density;
    return (int) (px / scale + 0.5f);
  }

  /**
   * 将 dp 转换为 px
   *
   * @param context 上下文
   * @param dp 像素
   * @return px
   */
  public static int dip2px(Context context, float dp) {
    float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }
}
