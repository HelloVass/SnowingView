package info.hellovass.snowingview.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import info.hellovass.snowingview.widgets.SnowingView;

/**
 * Created by HelloVass on 16/8/15.
 */
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
          mSnowingView.startFall();
        } else {
          mSnowingView.stopFall();
        }
      }
    });
  }
}
