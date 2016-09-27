package info.hellovass.snowingview.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import info.hellovass.snowingview.R;
import info.hellovass.snowingview.utils.DensityUtil;
import info.hellovass.snowingview.utils.RandomUtil;

/**
 * Created by hellovass on 16/9/26.
 *
 * winter is coming...
 */

public class SnowingView extends View implements SensorEventListener {

  private static final String TAG = SnowingView.class.getSimpleName();

  private final static long INVALID_TIME = -1;

  private final static int MSG_CALCULATE = 233;

  private final static int DEFAULT_SNOWFLAKE_BITMAP_VALUE = -1;

  private final static int DEFAULT_SNOWFLAKE_COUNT = 20;

  private final static int LOW_VELOCITY_Y = 150;

  private final static int HIGH_VELOCITY_Y = 2 * LOW_VELOCITY_Y;

  private final static float GRAVITATIONAL_ACCELERATION = 9.81F;

  private final static float MIN_OFFSET_X = 15.0F;

  private final static float MAX_OFFSET_X = 20.0F;

  private Context mContext;

  private int mWidth;

  private int mHeight;

  private float mSnowFlakeBitmapPivotX;

  private float mSnowFlakeBitmapPivotY;

  private Bitmap mSnowFlakeBitmap;

  private long mLastTimeMillis = INVALID_TIME;

  private Matrix mSnowFlakeMatrix;

  private Paint mSnowFlakePaint;

  private SnowFlake[] mSnowFlakes;

  private HandlerThread mCalculatePositionThread;

  private Handler mCalculateHandler;

  private boolean mIsSnowing = false;

  private SensorManager mSensorManager;

  private Sensor mAccelerometerSensor;

  private float mAccelerationXPercentage;

  public SnowingView(Context context) {
    super(context);
    init(context, null);
  }

  public SnowingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public SnowingView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public SnowingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mSensorManager.unregisterListener(this);
    notifyCalculateThreadStop();
    mCalculatePositionThread.quit();
  }

  /**
   * 开始下雪动画
   */
  public void startFall() {
    mIsSnowing = true;
    setVisibility(VISIBLE);
    mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
  }

  /**
   * 停止下雪动画
   */
  public void stopFall() {
    mIsSnowing = false;
    setVisibility(GONE);
    notifyCalculateThreadStop();
    mSensorManager.unregisterListener(this);
  }

  /**
   * 是否正在下雪
   *
   * @return true表示正在下雪
   */
  public boolean isSnowing() {
    return mIsSnowing;
  }

  /**
   * 加速度改变时会回调这个方法
   */
  @Override public void onSensorChanged(SensorEvent event) {
    float accelerationX = event.values[SensorManager.DATA_X];
    mAccelerationXPercentage = accelerationX / GRAVITATIONAL_ACCELERATION;
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  private void init(Context context, AttributeSet attrs) {

    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SnowingView);
    applyAttrsFromXML(array);
    array.recycle();

    mContext = context;
    initSensorManager();
    initCalculateThread();
    initCalculateHandler();
    initSnowFlakeMatrix();
    initSnowFlakePaint();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    mWidth = getMeasuredWidth();
    mHeight = getMeasuredHeight();

    createSnowFlakes();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    for (SnowFlake snowFlake : mSnowFlakes) {
      mSnowFlakeMatrix.setTranslate(0, 0);
      mSnowFlakeMatrix.postScale(snowFlake.getScale(), snowFlake.getScale(), mSnowFlakeBitmapPivotX,
          mSnowFlakeBitmapPivotY);
      mSnowFlakeMatrix.postTranslate(snowFlake.getPositionX(), snowFlake.getPositionY());
      mSnowFlakePaint.setColor(snowFlake.getTransparency());
      canvas.drawBitmap(mSnowFlakeBitmap, mSnowFlakeMatrix, mSnowFlakePaint);
    }

    mCalculateHandler.sendEmptyMessage(MSG_CALCULATE);
  }

  /**
   * 从XML文件中读取自定义的字段并赋值给成员
   *
   * @param array TypedArray
   */
  private void applyAttrsFromXML(TypedArray array) {
    mSnowFlakeBitmap = BitmapFactory.decodeResource(getResources(),
        array.getResourceId(R.styleable.SnowingView_src, DEFAULT_SNOWFLAKE_BITMAP_VALUE));
    mSnowFlakeBitmapPivotX = mSnowFlakeBitmap.getWidth() / 2.0F;
    mSnowFlakeBitmapPivotY = mSnowFlakeBitmap.getHeight() / 2.0F;
  }

  /**
   * 初始化传感器
   */
  private void initSensorManager() {

    if (isInEditMode()) {
      return;
    }

    mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
  }

  /**
   * 初始化工作线程
   */
  private void initCalculateThread() {
    mCalculatePositionThread = new HandlerThread("calculate_thread");
    mCalculatePositionThread.start();
  }

  /**
   * 初始化Handler
   */
  private void initCalculateHandler() {

    mCalculateHandler = new Handler(mCalculatePositionThread.getLooper()) {

      @Override public void handleMessage(Message msg) {
        super.handleMessage(msg);

        long currentTimeMillis = System.currentTimeMillis();

        if (mLastTimeMillis != INVALID_TIME) {

          float deltaTime = (currentTimeMillis - mLastTimeMillis) / 1000.0F;

          for (SnowFlake snowFlake : mSnowFlakes) {

            float x = snowFlake.getPositionX() + randomOffsetX();
            float y = snowFlake.getPositionY() + snowFlake.getVelocityY() * deltaTime;

            snowFlake.setPositionX(x);
            snowFlake.setPositionY(y);

            if (outOfRange(x, y)) {
              snowFlake.setPositionX(randomPositionX());
              snowFlake.setPositionY(resetPositionY());
            }
          }
        }

        mLastTimeMillis = currentTimeMillis;
        postInvalidate();
      }
    };
  }

  /**
   * 通知HandlerThread停止执行
   */
  private void notifyCalculateThreadStop() {
    mCalculateHandler.removeMessages(MSG_CALCULATE);
  }

  /**
   * 初始化雪花矩阵
   */
  private void initSnowFlakeMatrix() {
    mSnowFlakeMatrix = new Matrix();
  }

  /**
   * 初始化雪花画笔
   */
  private void initSnowFlakePaint() {
    mSnowFlakePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  }

  /**
   * 创建雪花数组
   */
  private void createSnowFlakes() {

    mSnowFlakes = new SnowFlake[DEFAULT_SNOWFLAKE_COUNT];

    for (int index = 0; index < mSnowFlakes.length; index++) {

      SnowFlake snowFlake = new SnowFlake.Builder().setPositionX(randomPositionX())
          .setPositionY(randomPositionY())
          .setVelocityY(randomVelocityY())
          .setTransparency(randomTransparency())
          .setScale(randomScale())
          .create();

      mSnowFlakes[index] = snowFlake;
    }
  }

  /**
   * 随机可能的X坐标
   *
   * @return 雪花的X坐标
   */
  private float randomPositionX() {
    return RandomUtil.nextFloat(mWidth + 2 * mSnowFlakeBitmap.getWidth())
        - mSnowFlakeBitmap.getWidth();
  }

  /**
   * 随机可能的Y坐标
   *
   * @return 雪花的Y坐标
   */
  private float randomPositionY() {
    return RandomUtil.nextFloat(mHeight + 2 * mSnowFlakeBitmap.getHeight())
        - mSnowFlakeBitmap.getHeight();
  }

  /**
   * 将雪花的Y坐标重置
   *
   * @return 雪花的Y坐标
   */
  private float resetPositionY() {
    return -mSnowFlakeBitmap.getHeight();
  }

  /**
   * 随机雪花在Y轴方向上的速度(2dp/s-4dp/s)
   *
   * @return y轴方向上的速度
   */
  private float randomVelocityY() {

    return RandomUtil.nextFloat(DensityUtil.dip2px(mContext, LOW_VELOCITY_Y),
        DensityUtil.dip2px(mContext, HIGH_VELOCITY_Y));
  }

  /**
   * 随机雪花的透明度
   *
   * @return 雪花的透明度
   */
  private int randomTransparency() {
    return RandomUtil.nextInt(10, 255) << 24;
  }

  /**
   * 随机雪花的缩放比例
   *
   * @return 雪花的缩放比
   */
  private float randomScale() {
    return RandomUtil.nextFloat(0.5F, 2.0F);
  }

  /**
   * 随机X轴的偏移量
   *
   * @return x轴上的偏移量
   */
  private float randomOffsetX() {
    return RandomUtil.nextFloat(DensityUtil.dip2px(mContext, MIN_OFFSET_X),
        DensityUtil.dip2px(mContext, MAX_OFFSET_X)) * -mAccelerationXPercentage;
  }

  /**
   * 是否超出View的范围
   *
   * @return true表示超出范围
   */
  private boolean outOfRange(float x, float y) {

    if (x < -mSnowFlakeBitmap.getWidth() || x > mWidth + mSnowFlakeBitmap.getWidth()) {
      return true;
    }

    if (y > mHeight + mSnowFlakeBitmap.getHeight()) {
      return true;
    }

    return false;
  }
}
