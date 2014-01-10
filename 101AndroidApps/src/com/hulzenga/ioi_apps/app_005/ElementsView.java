package com.hulzenga.ioi_apps.app_005;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

public class ElementsView extends AdapterView<ElementAdapter> {

    private static final String  TAG                    = "ELEMENTS_VIEW";
    private ElementAdapter       mElementAdapter;
    private DataSetObserver      mDataSetObserver;

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
    private int                  mOffset                = 0;
    private static final int     MIN_OFFSET             = 0;
    private int                  mMaxOffset;

    private int                  mNumberOfColumns;
    private int                  mPadding;

    // animation state variables
    private boolean              mDoAnimation           = false;
    private boolean              mRowDownAnimation      = false;

    private List<Animator>       mChildAnimations       = new ArrayList<Animator>();

    private static final long    mPartAnimationDuration = 400;
    private static final long    mFullAnimationDuration = 800;

    // touch state variables
    private float                mPrevX;
    private float                mPrevY;

    private Context              mContext;
    private ElementsViewObserver mElementsViewObserver;
    private Queue<View>          mElementViewRecycler   = new LinkedBlockingQueue<View>();

    // callback interface to the ElementsActivity
    public interface ElementsViewObserver {
        public void onAnimationStart();

        public void onAnimationEnd();
    }

    public ElementsView(Context context, AttributeSet attrs) {
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
        mDataSetObserver = new ElementDataSetObserver();
        mElementAdapter.registerDataSetObserver(mDataSetObserver);
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

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            break;
        case MotionEvent.ACTION_MOVE:
            mOffset += (int) (mPrevY - y);
            requestLayout();
            break;
        case MotionEvent.ACTION_UP:
            break;
        }

        mPrevX = x;
        mPrevY = y;
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mNumberOfColumns = (w - MIN_PADDING) / (ELEMENT_SIZE + MIN_PADDING);
        mPadding = (w - mNumberOfColumns * ELEMENT_SIZE) / (mNumberOfColumns + 1);

        mElementAdapter.notifyDataSetChanged();//
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

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

            if (mDoAnimation) {
                mChildAnimations.add(animateChild(nextChild, position));
            }
        }

        // if needed start animation
        if (mDoAnimation) {

            AnimatorSet set = new AnimatorSet();
            set.setDuration(500);
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
                    mElementsViewObserver.onAnimationEnd();

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub

                }
            });
            set.start();
            mChildAnimations.clear();

            mDoAnimation = false;
        }
    }

    private void positionChild(View child, int position) {

        if (position < mElementsInFirstRow) {
            // first row
            final int left = mPadding + (mPadding + ELEMENT_SIZE) * (mNumberOfColumns - mElementsInFirstRow + position);
            child.layout(left, mPadding - mOffset, left + ELEMENT_SIZE, mPadding + ELEMENT_SIZE - mOffset);
        } else {
            // all other rows
            final int row = (position - mElementsInFirstRow) / mNumberOfColumns + 1;
            final int rowIndex = (position - mElementsInFirstRow) % mNumberOfColumns;
            final int top = mPadding + row * (ELEMENT_SIZE + mPadding) - mOffset;

            int left;
            if (row % 2 == 1) {
                // right to left for odd rows
                left = mPadding + (mNumberOfColumns - rowIndex - 1) * (ELEMENT_SIZE + mPadding);
            } else {
                // left to right for even rows
                left = mPadding + rowIndex * (ELEMENT_SIZE + mPadding);
            }

            child.layout(left, top, left + ELEMENT_SIZE, top + ELEMENT_SIZE);
        }
    }

    private Animator animateChild(View child, int position) {

        ObjectAnimator animation = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, -ELEMENT_SIZE + mPadding, 0);

        return animation;
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
    }

    /**
     * Observer class used to call back to the ElementsView when the underlying
     * ElementAdapter has changed
     */
    class ElementDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            int oldCount = mCount;

            calculateGridDimensions();

            if (oldCount > mCount) {
                // removal
            } else if (oldCount < mCount) {
                // new element added

                if (oldCount + 1 == mCount) {
                    // added one element

                    if (oldCount % mNumberOfColumns == 0) {
                        // shift rows down by 1
                        mDoAnimation = true;
                    } else {

                    }

                } else {
                    // added multiple elements

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
