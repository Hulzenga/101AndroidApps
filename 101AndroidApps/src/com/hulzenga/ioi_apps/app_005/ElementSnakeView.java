package com.hulzenga.ioi_apps.app_005;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.hulzenga.ioi_apps.app_005.ElementAdapter.ViewHolder;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Toast;

public class ElementSnakeView extends AdapterView<ElementAdapter> {

    private static final String  TAG                    = "ELEMENTS_VIEW";
    private ElementAdapter       mElementAdapter;

    private int                  mRemovedItemPosition;
    private static final int     ELEMENT_SIZE           = 100;
    private static final int     MIN_PADDING            = 10;

    final int                    mElementMeasureSpec    = MeasureSpec
                                                                .makeMeasureSpec(ELEMENT_SIZE, MeasureSpec.EXACTLY);

    private int                  mCount;
    private int                  mElementsInFirstRow;

    /**
     * distance from the top of the view screen to the top of the first row
     * (including padding)
     */
    private int                  mScrollDistance        = 0;
    private static final int     MIN_SCROLL             = 0;
    private int                  mMaxScroll;

    private int                  mNumberOfColumns;
    private int                  mRows;
    private int                  mPadding;
    private int                  mGridBlock;
    private float                mWidth;
    private float                mHeight;

    // animation state variables
    private boolean              mDoAnimation           = false;
    private int                  mAnimationType         = -1;

    private static final int     ANIMATION_NEW_ELEMENT  = 0;
    private static final int     ANIMATION_NEW_ROW      = 1;
    private static final int     ANIMATION_REMOVAL      = 2;

    private List<Animator>       mChildAnimations       = new ArrayList<Animator>();

    //These two constants should really be screensize dependent
    private static final long    ANIMATION_LENGTH_SHORT = 250L;
    private static final long    ANIMATION_LENGTH_LONG  = 800L;
    private long                 mAStep;

    // touch state variables
    private float                mTouchStartX;
    private float                mTouchStartY;
    private float                mPrevX;
    private float                mPrevY;

    private static final int     TOUCH_NONE             = 0;
    private static final int     TOUCH_CLICK            = 1;
    private static final int     TOUCH_SCROLL           = 2;
    private static final int     TOUCH_DRAG             = 4;
    private int                  mTouchState            = TOUCH_NONE;

    private static final int     SCROLL_THRESHOLD       = 10;

    private Runnable             mLongPressRunnable;

    private Context              mContext;
    private ElementsViewObserver mElementsViewObserver;
    private Queue<View>          mElementViewRecycler   = new LinkedBlockingQueue<View>();

    // callback interface to the ElementsActivity
    public interface ElementsViewObserver {
        public void onAnimationStart();

        public void onAnimationEnd();
    }

    public ElementSnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public ElementAdapter getAdapter() {
        return mElementAdapter;
    }

    @Override
    public void setAdapter(ElementAdapter adapter) {
        mElementAdapter = adapter;
        mElementAdapter.registerDataSetObserver(new ElementDataSetObserver());
    }

    public void registerObserver(ElementsViewObserver listener) {
        mElementsViewObserver = listener;
    }

    @Override
    public View getSelectedView() {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public void setSelection(int position) {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // don't respond to any touch events while an animation is running
        if (mDoAnimation) {
            return false;
        }

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mTouchStartX = x;
            mTouchStartY = y;
            mTouchState = TOUCH_CLICK; // assume click
            startLongPressCheck();
            break;

        case MotionEvent.ACTION_MOVE:
            switch (mTouchState) {
            case TOUCH_CLICK:
                if (!shouldStartScrolling(x, y)) {
                    // didn't cross the scrolling threshold so do nothing
                    break;
                } else {
                    // started scrolling so this can't be a long press
                    removeCallbacks(mLongPressRunnable);

                    // fall through to TOUCH_SCROLL
                    mTouchState = TOUCH_SCROLL;
                }
            case TOUCH_SCROLL:
                scroll(mPrevY - y);
                break;
            default:
                Log.w(TAG, "MotionEvent ACTION_MOVE fired while in unexpected state");
                break;
            }
            requestLayout();
            break;

        case MotionEvent.ACTION_UP:

            // up so no long press
            removeCallbacks(mLongPressRunnable);

            switch (mTouchState) {
            case TOUCH_CLICK:
                clickPosition(x, y);
                break;
            }

            mTouchState = TOUCH_NONE;
            break;
        }

        mPrevX = x;
        mPrevY = y;
        return true;
    }

    public void startLongPressCheck() {
        mLongPressRunnable = new Runnable() {

            @Override
            public void run() {
                if (mTouchState == TOUCH_CLICK) {
                    longClickPosition(mTouchStartX, mTouchStartY);
                }
            }
        };
    }

    private boolean shouldStartScrolling(float x, float y) {

        if (Math.abs(x - mTouchStartX) > SCROLL_THRESHOLD || Math.abs(y - mTouchStartY) > SCROLL_THRESHOLD) {
            return true;
        }
        return false;
    }

    private void scroll(float delta) {
        mScrollDistance += (int) (delta);

        if (mScrollDistance < MIN_SCROLL) {
            mScrollDistance = MIN_SCROLL;
        } else if (mScrollDistance > mMaxScroll) {
            mScrollDistance = mMaxScroll;
        }
    }

    private void clickPosition(float x, float y) {
        int position = findPosition(x, y);
        if (position != -1) {
            mElementAdapter.removeItem(position);
        }        
    }

    private void longClickPosition(float x, float y) {
        int position = findPosition(x, y);
        if (position != -1) {
            mElementAdapter.removeItem(position);
        }        
        mTouchState = TOUCH_NONE;
    }

    private int findPosition(float x, float y) {
        Rect hitRectangle = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).getHitRect(hitRectangle);
            if (hitRectangle.contains((int) x, (int) y)) {
                ViewHolder holder = (ViewHolder) getChildAt(i).getTag();
                return (Integer) holder.mElementImageView.getTag();
            }
        }
        return -1;
    }

    private int findAdapterPosition(int childNumber) {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mNumberOfColumns = (w - MIN_PADDING) / (ELEMENT_SIZE + MIN_PADDING);
        mPadding = (w - mNumberOfColumns * ELEMENT_SIZE) / (mNumberOfColumns + 1);
        mGridBlock = mPadding + ELEMENT_SIZE;

        // TODO: find a better place to put this
        mAStep = ANIMATION_LENGTH_LONG / mNumberOfColumns;

        mElementAdapter.notifyDataSetChanged();//
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mRemovedItemPosition = mElementAdapter.getRemovedItemPosition();
        // do nothing if no adapter defined
        if (mElementAdapter == null) {
            return;
        }

        // put all the old views in the recycler and remove them from the layout
        for (int i = 0; i < getChildCount(); i++) {
            mElementViewRecycler.offer(getChildAt(i));
        }
        removeAllViewsInLayout();

        for (int position = 0; position < mCount; position++)
        {
            View nextChild;

            // if possible recycle old view
            if (mElementViewRecycler.size() > 0) {
                nextChild = mElementAdapter.getView(position, mElementViewRecycler.poll(), this);
            } else {
                nextChild = mElementAdapter.getView(position, null, this);
            }

            addMeasureChild(nextChild);
            positionChild(nextChild, position);

        }

        // if needed start animation
        if (mDoAnimation) {

            AnimatorSet set = new AnimatorSet();
            set.playTogether(mChildAnimations);
            set.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    mElementsViewObserver.onAnimationStart();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mChildAnimations.clear();
                    mDoAnimation = false;
                    mElementsViewObserver.onAnimationEnd();

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub

                }
            });
            set.setInterpolator(new LinearInterpolator());
            set.start();
            mChildAnimations.clear();

        }
    }

    private void positionChild(View child, int position) {

        // row and rowIndex value of the child, changed if not on first row,
        // needed for animation
        int row = 0;
        int rowIndex = position;

        if (position < mElementsInFirstRow) {
            final int left = mPadding + mGridBlock * (mNumberOfColumns - mElementsInFirstRow + position);
            child.layout(left, mPadding - mScrollDistance, left + ELEMENT_SIZE, mGridBlock - mScrollDistance);
        } else {
            // all other rows
            row = (position - mElementsInFirstRow) / mNumberOfColumns + 1;
            rowIndex = (position - mElementsInFirstRow) % mNumberOfColumns;
            final int top = mPadding + row * mGridBlock - mScrollDistance;

            int left;
            if (row % 2 == 1) {
                // right to left for odd rows
                left = mPadding + (mNumberOfColumns - rowIndex - 1) * mGridBlock;
            } else {
                // left to right for even rows
                left = mPadding + rowIndex * mGridBlock;
            }

            child.layout(left, top, left + ELEMENT_SIZE, top + ELEMENT_SIZE);
        }

        if (mDoAnimation) {
            animateChild(child, position, row, rowIndex);
        }
    }

    private void animateChild(View child, int position, int row, int rowIndex) {
        Animator animator;
        long duration;
        long delay;
        switch (mAnimationType) {
        case ANIMATION_NEW_ELEMENT:
            if (row == 0 && rowIndex == 0) {
                /*
                 * Animation for new element(s) in the first row
                 */
                animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, -mWidth, 0);
                animator.setDuration(ANIMATION_LENGTH_SHORT);
                mChildAnimations.add(animator);
            }
            break;
        case ANIMATION_REMOVAL:
            if (mCount % mNumberOfColumns == 0) {
                // do row shift up
                if (row % 2 == 0) {
                    /*
                     * Animation for the even rows
                     */

                    // moves in below row
                    duration = mAStep * rowIndex;
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, +(mNumberOfColumns - 2 * rowIndex - 1)
                            * mGridBlock, +(mNumberOfColumns - rowIndex - 1) * mGridBlock);
                    animator.setDuration(duration);
                    mChildAnimations.add(animator);

                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, +mGridBlock, +mGridBlock);
                    animator.setDuration(duration);
                    mChildAnimations.add(animator);

                    delay = duration;

                    // move 1 up
                    duration = mAStep;

                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, +mGridBlock, 0);
                    animator.setDuration(duration);
                    animator.setStartDelay(delay);
                    mChildAnimations.add(animator);

                    delay += duration;

                    // move to final position
                    duration = mAStep * (mNumberOfColumns - rowIndex - 1);
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, +(mNumberOfColumns - rowIndex - 1)
                            * mGridBlock, 0);
                    animator.setDuration(duration);
                    animator.setStartDelay(delay);
                    mChildAnimations.add(animator);

                } else {
                    /*
                     * Animation for the right to left moving odd rows
                     */

                    // moves in below row
                    duration = mAStep * rowIndex;
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, -(mNumberOfColumns - 2 * rowIndex - 1)
                            * mGridBlock, -(mNumberOfColumns - rowIndex - 1) * mGridBlock);
                    animator.setDuration(duration);
                    mChildAnimations.add(animator);

                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, +mGridBlock, +mGridBlock);
                    animator.setDuration(duration);
                    mChildAnimations.add(animator);

                    delay = duration;

                    // move 1 down
                    duration = mAStep;
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, +mGridBlock, 0);
                    animator.setDuration(duration);
                    animator.setStartDelay(delay);
                    mChildAnimations.add(animator);

                    delay += duration;

                    // move to final position
                    duration = mAStep * (mNumberOfColumns - rowIndex - 1);
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, -(mNumberOfColumns - rowIndex - 1)
                            * mGridBlock, 0);
                    animator.setDuration(duration);
                    animator.setStartDelay(delay);
                    mChildAnimations.add(animator);
                }
            } else {
                if (position >= mRemovedItemPosition) {
                    return; // do nothing
                } else {

                    if (rowIndex == 0 && row != 0) {
                        // move 1 down
                        duration = mAStep;
                        animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, -mGridBlock, 0);
                        animator.setDuration(ANIMATION_LENGTH_SHORT);
                        mChildAnimations.add(animator);
                    } else if (row % 2 == 0) {
                     // move 1 right
                        duration = mAStep;
                        animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, -mGridBlock, 0);
                        animator.setDuration(ANIMATION_LENGTH_SHORT);
                        mChildAnimations.add(animator);
                    } else {
                     // move 1 left
                        duration = mAStep;
                        animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, +mGridBlock, 0);
                        animator.setDuration(ANIMATION_LENGTH_SHORT);
                        mChildAnimations.add(animator);
                    }
                }
            }
            break;
        case ANIMATION_NEW_ROW:
            if (row == 0) {
                /*
                 * Animation for new element(s) in the first row
                 */
                if (mCount == 1) {
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, -mWidth, 0);
                    animator.setDuration(ANIMATION_LENGTH_SHORT);
                    mChildAnimations.add(animator);
                } else {
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, -mWidth, 0);
                    animator.setDuration(ANIMATION_LENGTH_LONG);
                    mChildAnimations.add(animator);
                }

            } else {
                if (row % 2 == 0) {
                    /*
                     * Animation for the left to right moving even rows
                     */

                    // moves in above row
                    duration = mAStep * (mNumberOfColumns - rowIndex - 1);
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, (mNumberOfColumns - 2 * rowIndex - 1)
                            * mGridBlock, -rowIndex * mGridBlock);
                    animator.setDuration(duration);
                    mChildAnimations.add(animator);

                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, -mGridBlock, -mGridBlock);
                    animator.setDuration(duration);
                    mChildAnimations.add(animator);

                    delay = duration;

                    // move 1 down
                    duration = mAStep;

                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, -mGridBlock, 0);
                    animator.setDuration(duration);
                    animator.setStartDelay(delay);
                    mChildAnimations.add(animator);

                    delay += duration;

                    // move to final position
                    duration = mAStep * rowIndex;
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, -rowIndex * mGridBlock, 0);
                    animator.setDuration(duration);
                    animator.setStartDelay(delay);
                    mChildAnimations.add(animator);

                } else {
                    /*
                     * Animation for the right to left moving odd rows
                     */

                    // moves in above row
                    duration = mAStep * (mNumberOfColumns - rowIndex - 1);
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, -(mNumberOfColumns - 2 * rowIndex - 1)
                            * mGridBlock, rowIndex * mGridBlock);
                    animator.setDuration(duration);
                    mChildAnimations.add(animator);

                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, -mGridBlock, -mGridBlock);
                    animator.setDuration(duration);
                    mChildAnimations.add(animator);

                    delay = duration;

                    // move 1 down
                    duration = mAStep;
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, -mGridBlock, 0);
                    animator.setDuration(duration);
                    animator.setStartDelay(delay);
                    mChildAnimations.add(animator);

                    delay += duration;

                    // move to final position
                    duration = mAStep * rowIndex;
                    animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, rowIndex * mGridBlock, 0);
                    animator.setDuration(duration);
                    animator.setStartDelay(delay);
                    mChildAnimations.add(animator);
                }
            }
            break;
        }
    }

    private void addMeasureChild(View child) {
        addViewInLayout(child, -1, null, true);

        child.measure(mElementMeasureSpec, mElementMeasureSpec);
    }

    private void calculateGridDimensions() {

        mCount = mElementAdapter.getCount();

        if (mCount <= mNumberOfColumns) {
            mElementsInFirstRow = mCount;
        } else {
            mElementsInFirstRow = mCount % mNumberOfColumns == 0 ? mNumberOfColumns : mCount % mNumberOfColumns;
        }

        mRows = (mCount - mElementsInFirstRow) / mNumberOfColumns + 1;

        // TODO calculate number of rows on screen

        mMaxScroll = (mPadding + mRows * mGridBlock) - (int) mHeight;

        // max scroll must be bigger than MIN_SCROLL
        mMaxScroll = (mMaxScroll < MIN_SCROLL) ? MIN_SCROLL : mMaxScroll;
    }

    /**
     * Observer class used to call back to the ElementSnakeView when the
     * underlying ElementAdapter has changed
     */
    class ElementDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            int oldCount = mCount;

            calculateGridDimensions();

            if (oldCount > mCount) {
                // old element removed
                mDoAnimation = true;
                mAnimationType = ANIMATION_REMOVAL;

            } else if (oldCount < mCount) {
                // new element added

                if (oldCount % mNumberOfColumns == 0) {
                    // shift rows down by 1
                    mDoAnimation = true;
                    mAnimationType = ANIMATION_NEW_ROW;

                } else {
                    // insertion of new element
                    mDoAnimation = true;
                    mAnimationType = ANIMATION_NEW_ELEMENT;

                }
            } else {
                // oldCount == mCount, this shouldn't happen
                Log.w(TAG,
                        " the ElementAdapter called notifyDataSetChanged() with no change in underlying dataset size");
            }
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            // TODO Auto-generated method stub
            super.onInvalidated();
        }

    }

}
