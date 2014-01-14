package com.hulzenga.ioi_apps.app_005;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

public class ElementAnimator {

    public static final long          ANIMATION_LENGTH_SHORT              = 250L;
    public static final long          ANIMATION_LENGTH_LONG               = 1000L;

    private float                      mWidth;
    private float                      mGridBlock;

    private int                        mColumnCount;
    private long                       mAStep;

    private PropertyValuesHolder       mPvhFirstRowInsertion;
    private PropertyValuesHolder       mPvhMoveLeft;
    private PropertyValuesHolder       mPvhMoveRight;
    private PropertyValuesHolder       mPvhMoveUp;
    private PropertyValuesHolder       mPvhMoveDown;

    private PropertyValuesHolder       mPvhStartUp;
    private PropertyValuesHolder       mPvhStartDown;

    private List<PropertyValuesHolder> mPvhMoveLeftForwardInAboveRowList   = new ArrayList<PropertyValuesHolder>();
    private List<PropertyValuesHolder> mPvhMoveLeftForwardInSameRowList    = new ArrayList<PropertyValuesHolder>();
    private List<PropertyValuesHolder> mPvhMoveLeftBackwardInSameRowList   = new ArrayList<PropertyValuesHolder>();
    private List<PropertyValuesHolder> mPvhMoveLeftBackWardInBelowRowList  = new ArrayList<PropertyValuesHolder>();
    private List<PropertyValuesHolder> mPvhMoveRightForwardInAboveRowList  = new ArrayList<PropertyValuesHolder>();
    private List<PropertyValuesHolder> mPvhMoveRightForwardInSameRowList   = new ArrayList<PropertyValuesHolder>();
    private List<PropertyValuesHolder> mPvhMoveRightBackwardInSameRowList  = new ArrayList<PropertyValuesHolder>();
    private List<PropertyValuesHolder> mPvhMoveRightBackwardInBelowRowList = new ArrayList<PropertyValuesHolder>();

    public ElementAnimator(float width, float gridBlock, int columnCount) {
        mWidth = width;
        mGridBlock = gridBlock;
        mColumnCount = columnCount;

        mAStep = ANIMATION_LENGTH_LONG / mColumnCount;

        setupAnimators();
    }

    private void setupAnimators() {
        mPvhFirstRowInsertion = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -mWidth, 0.0f);
        mPvhMoveLeft = PropertyValuesHolder.ofFloat("translationX", +mGridBlock, 0.0f);
        mPvhMoveRight = PropertyValuesHolder.ofFloat("translationX", -mGridBlock, 0.0f);
        mPvhMoveUp = PropertyValuesHolder.ofFloat("translationY", +mGridBlock, 0.0f);
        mPvhMoveDown = PropertyValuesHolder.ofFloat("translationY", -mGridBlock, 0.0f);

        mPvhStartUp = PropertyValuesHolder.ofFloat("translationY", -mGridBlock, -mGridBlock);
        mPvhStartDown = PropertyValuesHolder.ofFloat("translationY", +mGridBlock, +mGridBlock);

        // moves left in above row indexed by final rowIndex
        for (int i = 0; i < mColumnCount; i++) {
            PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, (mColumnCount - 2 * i - 1)
                    * mGridBlock, -i * mGridBlock);
            mPvhMoveLeftForwardInAboveRowList.add(pvh);
        }

        // moves left forward in same row indexed by final rowIndex
        for (int i = 0; i < mColumnCount; i++) {
            PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, i * mGridBlock, 0);
            mPvhMoveLeftForwardInSameRowList.add(pvh);

        }

        // moves left backward same row indexed by final rowIndex
        for (int i = 0; i < mColumnCount; i++) {
            PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, +(mColumnCount - i - 1)
                    * mGridBlock, 0);
            mPvhMoveLeftBackwardInSameRowList.add(pvh);

        }

        // moves left in below row indexed by final rowIndex
        for (int i = 0; i < mColumnCount; i++) {
            PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, +(mColumnCount - 2 * i - 1)
                    * mGridBlock, +(mColumnCount - i - 1) * mGridBlock);
            mPvhMoveLeftBackWardInBelowRowList.add(pvh);

        }

        // moves right in above row indexed by final rowIndex
        for (int i = 0; i < mColumnCount; i++) {
            PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -(mColumnCount - 2 * i - 1)
                    * mGridBlock, i * mGridBlock);
            mPvhMoveRightForwardInAboveRowList.add(pvh);

        }

        // moves forward right in same row indexed by final rowIndex
        for (int i = 0; i < mColumnCount; i++) {
            PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -i * mGridBlock, 0);
            mPvhMoveRightForwardInSameRowList.add(pvh);
        }

        // moves backward right in same row indexed by final rowIndex
        for (int i = 0; i < mColumnCount; i++) {
            PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -(mColumnCount - i - 1)
                    * mGridBlock, 0);
            mPvhMoveRightBackwardInSameRowList.add(pvh);
        }

        // moves right in below row indexed by final rowIndex
        for (int i = 0; i < mColumnCount; i++) {
            PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -(mColumnCount - 2 * i - 1)
                    * mGridBlock, -(mColumnCount - i - 1) * mGridBlock);
            mPvhMoveRightBackwardInBelowRowList.add(pvh);
        }

    }

    public Animator doNothingAnimator(View view) {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 1.0f).setDuration(ANIMATION_LENGTH_SHORT);
    }
    
    /*
     * First row insertion animators
     */
    public Animator firstRowShortInsertion(View view) {
        return ObjectAnimator.ofPropertyValuesHolder(view, mPvhFirstRowInsertion).setDuration(ANIMATION_LENGTH_SHORT);
    }

    public Animator firstRowLongInsertion(View view) {
        return ObjectAnimator.ofPropertyValuesHolder(view, mPvhFirstRowInsertion).setDuration(ANIMATION_LENGTH_LONG);
    }

    /*
     * Single step animators
     */
    public Animator moveLeft(View view) {
        return ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveLeft).setDuration(ANIMATION_LENGTH_SHORT);
    }

    public Animator moveRight(View view) {
        return ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveRight).setDuration(ANIMATION_LENGTH_SHORT);
    }

    public Animator moveUp(View view) {
        return ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveUp).setDuration(ANIMATION_LENGTH_SHORT);
    }

    public Animator moveDown(View view) {
        return ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveDown).setDuration(ANIMATION_LENGTH_SHORT);
    }

    /*
     * Row shift Animators
     */

    private List<Animator> mAnimatorReturnList = new ArrayList<Animator>();

    public List<Animator> moveEvenRowDown(View view, int rowIndex) {

        mAnimatorReturnList.clear();

        // move left in above row
        long duration = mAStep * (mColumnCount - rowIndex - 1);

        Animator animator = ObjectAnimator.ofPropertyValuesHolder(view,
                mPvhMoveLeftForwardInAboveRowList.get(rowIndex),
                mPvhStartUp);
        animator.setDuration(duration);
        mAnimatorReturnList.add(animator);

        long delay = duration;

        // move 1 down
        duration = mAStep;

        animator = ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveDown);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        mAnimatorReturnList.add(animator);

        delay += duration;

        // move right final position
        duration = mAStep * rowIndex;

        animator = ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveRightForwardInSameRowList.get(rowIndex));
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        mAnimatorReturnList.add(animator);

        return mAnimatorReturnList;
    }

    public List<Animator> moveOddRowDown(View view, int rowIndex) {

        mAnimatorReturnList.clear();

        // move right in above row
        long duration = mAStep * (mColumnCount - rowIndex - 1);

        Animator animator = ObjectAnimator.ofPropertyValuesHolder(view,
                mPvhMoveRightForwardInAboveRowList.get(rowIndex),
                mPvhStartUp);
        animator.setDuration(duration);
        mAnimatorReturnList.add(animator);

        long delay = duration;

        // move 1 down
        duration = mAStep;

        animator = ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveDown);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        mAnimatorReturnList.add(animator);

        delay += duration;

        // move left final position
        duration = mAStep * rowIndex;

        animator = ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveLeftForwardInSameRowList.get(rowIndex));
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        mAnimatorReturnList.add(animator);

        return mAnimatorReturnList;
    }

    public List<Animator> moveEvenRowUp(View view, int rowIndex) {

        mAnimatorReturnList.clear();

        // move left in below row
        long duration = mAStep * rowIndex;

        Animator animator = ObjectAnimator.ofPropertyValuesHolder(view,
                mPvhMoveLeftBackWardInBelowRowList.get(rowIndex),
                mPvhStartDown);
        animator.setDuration(duration);
        mAnimatorReturnList.add(animator);

        long delay = duration;

        // move 1 down
        duration = mAStep;

        animator = ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveUp);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        mAnimatorReturnList.add(animator);

        delay += duration;

        // move right to final position
        duration = mAStep * (mColumnCount - rowIndex - 1);

        animator = ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveLeftBackwardInSameRowList.get(rowIndex));
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        mAnimatorReturnList.add(animator);

        return mAnimatorReturnList;
    }

    public List<Animator> moveOddRowUp(View view, int rowIndex) {

        mAnimatorReturnList.clear();

        // move right in below row
        long duration = mAStep * rowIndex;

        Animator animator = ObjectAnimator.ofPropertyValuesHolder(view,
                mPvhMoveRightBackwardInBelowRowList.get(rowIndex),
                mPvhStartDown);
        animator.setDuration(duration);
        mAnimatorReturnList.add(animator);

        long delay = duration;

        // move 1 down
        duration = mAStep;

        animator = ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveUp);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        mAnimatorReturnList.add(animator);

        delay += duration;

        // move left to final position
        duration = mAStep * (mColumnCount - rowIndex - 1);

        animator = ObjectAnimator.ofPropertyValuesHolder(view, mPvhMoveRightBackwardInSameRowList.get(rowIndex));
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        mAnimatorReturnList.add(animator);

        return mAnimatorReturnList;
    }
}
