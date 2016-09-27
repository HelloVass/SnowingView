package info.hellovass.snowingview.widgets;

/**
 * Created by hellovass on 16/9/26.
 */

public class SnowFlake {

  private float mPositionX;

  private float mPositionY;

  private float mVelocityY;

  private int mTransparency;

  private float mScale;

  private SnowFlake() {
    //  private
  }

  public SnowFlake(Builder builder) {
    mPositionX = builder.mPositionX;
    mPositionY = builder.mPositionY;
    mVelocityY = builder.mVelocityY;
    mTransparency = builder.mTransparency;
    mScale = builder.mScale;
  }

  public float getPositionX() {
    return mPositionX;
  }

  public void setPositionX(float positionX) {
    mPositionX = positionX;
  }

  public float getPositionY() {
    return mPositionY;
  }

  public void setPositionY(float positionY) {
    mPositionY = positionY;
  }

  public float getVelocityY() {
    return mVelocityY;
  }

  public void setVelocityY(float velocityY) {
    mVelocityY = velocityY;
  }

  public int getTransparency() {
    return mTransparency;
  }

  public void setTransparency(int transparency) {
    mTransparency = transparency;
  }

  public float getScale() {
    return mScale;
  }

  public void setScale(float scale) {
    mScale = scale;
  }

  public static class Builder {

    private float mPositionX;

    private float mPositionY;

    private float mVelocityY;

    private int mTransparency;

    private float mScale;

    public Builder setPositionX(float positionX) {
      mPositionX = positionX;
      return this;
    }

    public Builder setPositionY(float positionY) {
      mPositionY = positionY;
      return this;
    }

    public Builder setVelocityY(float velocityY) {
      mVelocityY = velocityY;
      return this;
    }

    public Builder setTransparency(int transparency) {
      mTransparency = transparency;
      return this;
    }

    public Builder setScale(float scale) {
      mScale = scale;
      return this;
    }

    public SnowFlake create() {
      return new SnowFlake(this);
    }
  }
}
