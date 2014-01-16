package com.hulzenga.ioi_apps.app_005;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;

import com.hulzenga.ioi_apps.app_005.ElementAdapter.ElementChangeObserver;

public class ElementSnakeView extends AdapterView<ElementAdapter> implements ElementChangeObserver {

    private static final String      TAG                                = "ELEMENTS_VIEW";
    private ElementAdapter           mElementAdapter;

    private int                      mRemovedItemPosition;
    private static final int         ELEMENT_SIZE                       = 100;
    private static final int         MIN_PADDING                        = 15;

    final int                        mElementMeasureSpec;

    private int                      mCount;
    private int                      mElementsInFirstRow;
    private int                      mVisibleRowCount;
    private int                      mDraggedElement;
    /**
     * distance from the top of the view screen to the top of the first row
     * (including padding)
     */
    private int                      mScrollDistance                    = 0;
    private static final int         MIN_SCROLL                         = 0;
    private int                      mMaxScroll;

    private int                      mColumnCount;
    private int                      mRowCount;
    private int                      mPadding;
    private int                      mGridBlock;
    private float                    mWidth;
    private float                    mHeight;

    private int                      mSwapPosition0;
    private int                      mSwapPosition1;

    /*
     * Animation variables
     */
    private ElementAnimator          mElementAnimator;

    private boolean                  mDoAnimation                       = false;
    private int                      mAnimationType                     = -1;

    private static final int         ANIMATION_NEW_ELEMENT              = 0;
    private static final int         ANIMATION_NEW_ELEMENT_SHIFT_ROW    = 1;
    private static final int         ANIMATION_REMOVE_ELEMENT           = 2;
    private static final int         ANIMATION_REMOVE_ELEMENT_SHIFT_ROW = 3;
    private static final int         ANIMATION_DRAG_FLIP                = 4;
    private static final int         ANIMATION_SWAP_ELEMENTS            = 5;

    private List<Animator>           mChildAnimations                   = new ArrayList<Animator>();

    private Interpolator             mInterpolator                      = new LinearInterpolator();
    private AnimatorListener         mReleaseOnEndListener;
    private AnimatorListener         mRequestLayoutOnEndListener;

    /*
     * Touch state variables
     */
    private float                    mTouchStartX;
    private float                    mTouchStartY;
    private float                    mPrevX;
    private float                    mPrevY;

    private static final int         TOUCH_NONE                         = 0;
    private static final int         TOUCH_CLICK                        = 1;
    private static final int         TOUCH_SCROLL                       = 2;
    private int                      mTouchState                        = TOUCH_NONE;

    private static final int         SCROLL_THRESHOLD                   = 10;

    private Runnable                 mLongPressRunnable;

    private ElementAnimationCallback mAnimationCallback;
    private Queue<View>              mElementViewRecycler               = new LinkedBlockingQueue<View>();

    // callback interface to the ElementActivity
    public interface ElementAnimationCallback {
        public void onAnimationStart();

        public void onAnimationEnd();
    }

    public ElementSnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /*
         * setup variables which are too long to setup above
         */
        mElementMeasureSpec = MeasureSpec.makeMeasureSpec(ELEMENT_SIZE, MeasureSpec.EXACTLY);

        mReleaseOnEndListener = new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationCallback.onAnimationStart();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mChildAnimations.clear();
                mDoAnimation = false;
                mAnimationCallback.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        };

        // TODO: rewrite so main animation follows the following logic
        // This is the path I should have taken from the beginning:
        // layout after animation, Not animation after layout
        mRequestLayoutOnEndListener = new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationCallback.onAnimationStart();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mChildAnimations.clear();
                requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        };

    }

    @Override
    public ElementAdapter getAdapter() {
        return mElementAdapter;
    }

    @Override
    public void setAdapter(ElementAdapter adapter) {
        mElementAdapter = adapter;
        mElementAdapter.registerElementChangeObserver(this);
    }

    public void registerAnimationCallback(ElementAnimationCallback listener) {
        mAnimationCallback = listener;
    }

    @Override
    public View getSelectedView() {
        return getChildAt(mSelectedPosition - mFirstChildPosition);
    }

    private int mSelectedPosition;

    @Override
    public void setSelection(int position) {
        mSelectedPosition = position;
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

            break;

        case MotionEvent.ACTION_UP:

            // up, so no long press
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

    private boolean mDropSucces = false;

    @Override
    public boolean onDragEvent(DragEvent event) {

        switch (event.getAction()) {
        case DragEvent.ACTION_DRAG_STARTED:
            Log.d(TAG, "started");
            break;
        case DragEvent.ACTION_DRAG_ENTERED:
            Log.d(TAG, "entered");
            break;
        case DragEvent.ACTION_DRAG_LOCATION:
            Log.d(TAG, "location");
            break;
        case DragEvent.ACTION_DROP:            
            int position = findPosition(event.getX(), event.getY());
            if (position != -1) {
                mDropSucces = true;
                mElementAdapter.swap((Integer) event.getLocalState(), position);
            }
            Log.d(TAG, "drop");
            break;
        case DragEvent.ACTION_DRAG_EXITED:
            Log.d(TAG, "exited");
            break;
        case DragEvent.ACTION_DRAG_ENDED:
            if (mDropSucces) {
                mDropSucces = false;
            } else {
                mElementAdapter.stopDragging();
            }
            Log.d(TAG, "ended");
            break;
        }

        return true;
    }

    private void startLongPressCheck() {
        mLongPressRunnable = new Runnable() {

            @Override
            public void run() {
                if (mTouchState == TOUCH_CLICK) {
                    longClickPosition(mTouchStartX, mTouchStartY);
                }
            }
        };

        postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
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

        requestLayout();
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

            ElementView elementView = (ElementView) getChildAt(position - mFirstChildPosition);

            ClipData data = ClipData.newPlainText("", "");

            View.DragShadowBuilder sb = new ElementView.DragShadowBuilder(elementView, mElementAdapter
                    .getItem(position).getType());

            elementView.startDrag(data, sb, (Integer) position, 0);
            mElementAdapter.startDragging(position);
        }
        mTouchState = TOUCH_NONE;
    }

    private int findPosition(float x, float y) {
        Rect hitRectangle = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).getHitRect(hitRectangle);
            if (hitRectangle.contains((int) x, (int) y)) {
                return i + mFirstChildPosition;
            }
        }
        return -1;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mColumnCount = (w - MIN_PADDING) / (ELEMENT_SIZE + MIN_PADDING);
        mPadding = (w - mColumnCount * ELEMENT_SIZE) / (mColumnCount + 1);
        mGridBlock = mPadding + ELEMENT_SIZE;
        mVisibleRowCount = (int) Math.ceil(mHeight / ((float) mGridBlock));

        mElementAnimator = new ElementAnimator(mWidth, mGridBlock, mColumnCount);
        mElementAdapter.notifyDataSetChanged();//
    }

    private int mFirstChildPosition;
    private int mLastChildPosition;

    // set bounds so as to draw one row before and after the visible screen
    private void setDrawingBounds() {

        if (mScrollDistance < 2 * mGridBlock) {
            mFirstChildPosition = 0;
        } else {
            mFirstChildPosition = mElementsInFirstRow + ((mScrollDistance / mGridBlock) - 1) * mColumnCount;
        }
        mLastChildPosition = mFirstChildPosition + (mVisibleRowCount + 2) * mColumnCount;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // do nothing if no adapter defined
        if (mElementAdapter == null) {
            return;
        }

        setDrawingBounds();

        // dirty views need to be cleaned before being put in the recycler
        cleanViews();

        // put all the old views in the recycler
        for (int i = 0; i < getChildCount(); i++) {
            mElementViewRecycler.offer(getChildAt(i));
        }

        // remove all views from layout
        removeAllViewsInLayout();

        for (int position = mFirstChildPosition; position < mCount && position < mLastChildPosition; position++)
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

            // I can't tell if there is a nice way to recycle an Animatorset,
            // so a new one must be created
            @SuppressLint("DrawAllocation")
            AnimatorSet set = new AnimatorSet();

            set.addListener(mReleaseOnEndListener);
            set.setInterpolator(mInterpolator);

            if (mChildAnimations.size() > 0) {
                set.playTogether(mChildAnimations);
            } else {
                // an animation is going on off screen, but nothing happens on
                // screen, use empty animator to keep program flow as expected
                set.play(mElementAnimator.doNothingAnimator(getChildAt(0)));
            }

            set.start();
        }
    }

    private void positionChild(View child, int position) {

        // row and rowIndex value of the child, changed if not on first row,
        // needed for animation
        int row = 0;
        int rowIndex = position;

        if (position < mElementsInFirstRow) {
            final int left = mPadding + mGridBlock * (mColumnCount - mElementsInFirstRow + position);
            child.layout(left, mPadding - mScrollDistance, left + ELEMENT_SIZE, mGridBlock - mScrollDistance);
        } else {
            // all other rows
            row = (position - mElementsInFirstRow) / mColumnCount + 1;
            rowIndex = (position - mElementsInFirstRow) % mColumnCount;
            final int top = mPadding + row * mGridBlock - mScrollDistance;

            int left;
            if (row % 2 == 1) {
                // right to left for odd rows
                left = mPadding + (mColumnCount - rowIndex - 1) * mGridBlock;
            } else {
                // left to right for even rows
                left = mPadding + rowIndex * mGridBlock;
            }

            child.layout(left, top, left + ELEMENT_SIZE, top + ELEMENT_SIZE);
        }

        if (mDoAnimation) {
            animateView(child, position, row, rowIndex);
        }
    }

    private void animateView(View view, int position, int row, int rowIndex) {
        switch (mAnimationType) {
        case ANIMATION_NEW_ELEMENT:
            if (row == 0 && rowIndex == 0) {
                /*
                 * Animation for new element(s) in the first row
                 */
                mChildAnimations.add(mElementAnimator.firstRowShortInsertion(view));
            }
            break;
        case ANIMATION_NEW_ELEMENT_SHIFT_ROW:
            if (row == 0) {
                /*
                 * Animation for new element(s) in the first row
                 */
                mChildAnimations.add(mElementAnimator.firstRowLongInsertion(view));
            } else {
                if (row % 2 == 0) {
                    /*
                     * Animation for the even rows
                     */
                    mChildAnimations.addAll(mElementAnimator.moveEvenRowDown(view, rowIndex));
                } else {
                    /*
                     * Animation for the right to left moving odd rows
                     */
                    mChildAnimations.addAll(mElementAnimator.moveOddRowDown(view, rowIndex));
                }
            }
            break;
        case ANIMATION_REMOVE_ELEMENT:
            if (position >= mRemovedItemPosition) {
                return; // do nothing
            } else {
                if (rowIndex == 0 && row != 0) {
                    // move 1 down
                    mChildAnimations.add(mElementAnimator.moveDown(view));
                } else if (row % 2 == 0) {
                    // move 1 right
                    mChildAnimations.add(mElementAnimator.moveRight(view));
                } else {
                    // move 1 left
                    mChildAnimations.add(mElementAnimator.moveLeft(view));
                }
            }
            break;
        case ANIMATION_REMOVE_ELEMENT_SHIFT_ROW:
            if (row % 2 == 0) {
                /*
                 * Animation for the even rows
                 */
                mChildAnimations.addAll(mElementAnimator.moveEvenRowUp(view, rowIndex));
            } else {
                /*
                 * Animation for the right to left moving odd rows
                 */
                mChildAnimations.addAll(mElementAnimator.moveOddRowUp(view, rowIndex));
            }
            break;
        case ANIMATION_DRAG_FLIP:
            if (position == mDraggedElement) {
                mChildAnimations.add(ObjectAnimator.ofFloat(view, View.ROTATION_Y, 90.0f, 0.0f).setDuration(
                        ElementAnimator.ANIMATION_LENGTH_SHORT));
            }
            break;
        case ANIMATION_SWAP_ELEMENTS:
            if (position == mSwapPosition0 || position == mSwapPosition1) {
                mChildAnimations.add(ObjectAnimator.ofFloat(view, View.ROTATION_Y, 90.0f, 0.0f).setDuration(
                        ElementAnimator.ANIMATION_LENGTH_SHORT));
            }
        }
    }

    private void addMeasureChild(View child) {
        addViewInLayout(child, -1, null, true);

        child.measure(mElementMeasureSpec, mElementMeasureSpec);
    }

    private void calculateGridDimensions() {

        mCount = mElementAdapter.getCount();

        if (mCount <= mColumnCount) {
            mElementsInFirstRow = mCount;
        } else {
            mElementsInFirstRow = mCount % mColumnCount == 0 ? mColumnCount : mCount % mColumnCount;
        }

        mRowCount = (mCount - mElementsInFirstRow) / mColumnCount + 1;

        mMaxScroll = (mPadding + mRowCount * mGridBlock) - (int) mHeight;

        // max scroll must be bigger than MIN_SCROLL
        mMaxScroll = (mMaxScroll < MIN_SCROLL) ? MIN_SCROLL : mMaxScroll;
    }

    @Override
    public void onElementChange(ChangeType type, int... args) {
        int oldCount = mCount;

        calculateGridDimensions();

        switch (type) {
        case ELEMENT_ADDED:
            mDoAnimation = true;
            if (oldCount % mColumnCount != 0 || oldCount == 0) {
                // insertion of new element
                mAnimationType = ANIMATION_NEW_ELEMENT;
            } else {
                // shift rows down by 1
                mAnimationType = ANIMATION_NEW_ELEMENT_SHIFT_ROW;
            }
            requestLayout();
            break;
        case ELEMENT_REMOVED:
            mDoAnimation = true;
            mRemovedItemPosition = args[0];

            if (mCount % mColumnCount == 0) {
                mAnimationType = ANIMATION_REMOVE_ELEMENT_SHIFT_ROW;
            } else {
                mAnimationType = ANIMATION_REMOVE_ELEMENT;
            }
            requestLayout();
            break;
        case ELEMENTS_SWAPPED:
            mDoAnimation = true;
            mAnimationType = ANIMATION_SWAP_ELEMENTS;
            mSwapPosition0 = args[0];
            mSwapPosition1 = args[1];
            mCleanTheseViews.add(getChildAt(mSwapPosition0 - mFirstChildPosition));
            mCleanTheseViews.add(getChildAt(mSwapPosition1 - mFirstChildPosition));

            getChildAt(mSwapPosition0 - mFirstChildPosition).animate().rotationY(90.0f)
                    .setDuration(ElementAnimator.ANIMATION_LENGTH_SHORT).setListener(mRequestLayoutOnEndListener);
            getChildAt(mSwapPosition1 - mFirstChildPosition).animate().rotationY(90.0f)
                    .setDuration(ElementAnimator.ANIMATION_LENGTH_SHORT).setListener(mRequestLayoutOnEndListener);
            break;
        case STARTED_DRAGGING:
            mDoAnimation = true;
            mAnimationType = ANIMATION_DRAG_FLIP;
            mDraggedElement = args[0];
            mCleanTheseViews.add(getChildAt(mDraggedElement - mFirstChildPosition));
            getChildAt(mDraggedElement - mFirstChildPosition).animate().rotationY(90.0f)
                    .setDuration(ElementAnimator.ANIMATION_LENGTH_SHORT).setListener(mRequestLayoutOnEndListener);
            break;
        case STOPPED_DRAGGING:
            mDoAnimation = true;
            mAnimationType = ANIMATION_DRAG_FLIP;
            mCleanTheseViews.add(getChildAt(mDraggedElement - mFirstChildPosition));
            getChildAt(mDraggedElement - mFirstChildPosition).animate().rotationY(90.0f)
                    .setDuration(ElementAnimator.ANIMATION_LENGTH_SHORT).setListener(mRequestLayoutOnEndListener);
            break;
        }
    }

    /**
     * Views that need to be cleaned before they can be recycled
     */
    List<View> mCleanTheseViews = new ArrayList<View>();

    public void cleanViews() {
        if (mCleanTheseViews.size() > 0) {
            for (View v : mCleanTheseViews) {
                v.setRotationY(0.0f);
            }
            mCleanTheseViews.clear();
        }
    }
}
