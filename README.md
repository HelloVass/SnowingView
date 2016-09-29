# SnowingView 

## winter is coming...

## 介绍
一个**看起来还不错**的下雪动画。

## 功能&&实现
1. 使用 Matrix 产生随机大小、透明度的雪花
2. 使用 HandlerThread 来计算雪花的下一个位置
3. 使用**加速度传感器**判断用户在 X 轴的倾斜方向，使雪花产生左右飘动的效果

## 缺点&改进
1. 还未用 LeakCanary 检测是否有内存泄漏的情况
2. 暂时还不支持 **wrap_content**
3. 可能会采用 SurfaceView 来绘制提高效率

## 实际效果
![SnowingView效果](./design/SnowingView.gif)

## 使用（如果有人想用的话）

### Step1

在布局中添加 `SnowingView`

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/black"
    >
    
  <info.hellovass.snowingview.widgets.SnowingView
      android:id="@+id/snowing_view"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:visibility="gone"
      app:src="@drawable/ic_snowflake"
      />

  <Switch
      android:id="@+id/sw_snowing"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:layout_gravity="center"
      android:text="winter is coming..."
      android:textColor="@android:color/white"
      android:textSize="16sp"
      />
      
</FrameLayout>
```

### Step2
在 Activity 中，调用 SnowingView 的 `startFall()` 或者 `stopFall()` 方法。

```java
public class SampleActivity extends AppCompatActivity {

  private SnowingView mSnowingView;

  private Switch mSwitch;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);

    mSwitch = (Switch) findViewById(R.id.sw_snowing);
    mSnowingView = (SnowingView) findViewById(R.id.snowing_view);

    mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
          mSnowingView.startFall();  // 开始下雪
        } else {
          mSnowingView.stopFall(); // 停止下雪
        }
      }
    });
  }
}
```

## 参考
- 之前在开发者看到的 [Android下雪动画](http://www.devtf.cn/?p=1268)，但是，我并没有看懂作者的[雪花下落算法](http://www.openprocessing.org/sketch/84771)
