package com.hulzenga.ioi.android.util;

/**
 * ConstraintEnforcer is a utility class containing methods to enforce certain
 * constraints upon given values
 */
public class ConstraintEnforcer {

  // private class, no instantiation
  private ConstraintEnforcer() {
  }

  /**
   * Constrains a value up to a given upper bound
   *
   * @param value      the value to be constrained
   * @param upperBound the upper bound constraint
   * @return
   */
  public static int upperBound(int value, int upperBound) {
    if (value > upperBound) {
      return upperBound;
    } else {
      return value;
    }
  }

  /**
   * Constrains a value up to a given upper bound
   *
   * @param value      the value to be constrained
   * @param upperBound the upper bound constraint
   * @return
   */
  public static float upperBound(float value, float upperBound) {
    if (value > upperBound) {
      return upperBound;
    } else {
      return value;
    }
  }

  /**
   * Constrains a value to be no less then a given lower bound
   *
   * @param lowerBound the lower bound constraint
   * @param value      the value to be constrained
   * @return
   */
  public static int lowerBound(int lowerBound, int value) {
    if (value < lowerBound) {
      return lowerBound;
    } else {
      return value;
    }
  }

  /**
   * Constrains a value to be no less then a given lower bound
   *
   * @param lowerBound the lower bound constraint
   * @param value      the value to be constrained
   * @return
   */
  public static float lowerBound(float lowerBound, float value) {
    if (value < lowerBound) {
      return lowerBound;
    } else {
      return value;
    }
  }

  /**
   * Constrains a value to fall between a given upper and lower bound
   *
   * @param lowerBound the lower bound constraint
   * @param value      the value to be constrained
   * @param upperBound the upper bound constraint
   * @return
   */
  public static int doubleBound(int lowerBound, int value, int upperBound) {
    if (value < lowerBound) {
      return lowerBound;
    } else if (value > upperBound) {
      return upperBound;
    } else {
      return value;
    }
  }

  /**
   * Constrains a value to fall between a given upper and lower bound
   *
   * @param lowerBound the lower bound constraint
   * @param value      the value to be constrained
   * @param upperBound the upper bound constraint
   * @return
   */
  public static float doubleBound(float lowerBound, float value, float upperBound) {
    if (value < lowerBound) {
      return lowerBound;
    } else if (value > upperBound) {
      return upperBound;
    } else {
      return value;
    }
  }
}
