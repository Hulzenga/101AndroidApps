package com.hulzenga.ioi_apps.app_005;

import java.util.Random;

public class Element {

  private Random mRandom = new Random();

  private ClassicalElement mType;

  public enum ClassicalElement {
    EARTH, AIR, FIRE, WATER
  }

  public Element() {
    switch (mRandom.nextInt(4)) {
      case 0:
        mType = ClassicalElement.EARTH;
        break;
      case 1:
        mType = ClassicalElement.AIR;
        break;
      case 2:
        mType = ClassicalElement.FIRE;
        break;
      case 3:
        mType = ClassicalElement.WATER;
        break;
    }
  }

  public ClassicalElement getType() {
    return mType;
  }

  public void setType(ClassicalElement mType) {
    this.mType = mType;
  }
}
