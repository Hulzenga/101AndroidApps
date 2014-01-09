package com.hulzenga.ioi_apps.app_005;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

public class ElementsGridView extends AdapterView<ElementAdapter> {

    private ElementAdapter   mElementAdapter;

    private static final int ELEMENT_SIZE = 100;
    private static final int MIN_PADDING  = 10;

    private int              mNumberOfColumns;
    private int              mPadding;

    public ElementsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public ElementAdapter getAdapter() {
        return mElementAdapter;
    }

    @Override
    public void setAdapter(ElementAdapter adapter) {
        mElementAdapter = adapter;
        removeAllViewsInLayout();
        requestLayout();

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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mNumberOfColumns = (w - MIN_PADDING) / (ELEMENT_SIZE + MIN_PADDING);
        mPadding = (w-mNumberOfColumns*ELEMENT_SIZE)/(mNumberOfColumns+1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        
        invalidate();
        
        if (mElementAdapter == null) {
            return;
        }
        
        int position = 0;
        while (position < mElementAdapter.getCount()) {
            View nextChild = mElementAdapter.getView(position, null, this);
            addMeasureChild(nextChild);
            positionChild(nextChild, position);
            position++;
        }

        
        //positionItems();
    }
    
    private void positionChild(View child, int position) {
        
        final int inFirstRow = mElementAdapter.getCount() % mNumberOfColumns;
        
        //if first row
        if (position < inFirstRow) {
            final int left = mPadding+(mNumberOfColumns-position-1)*(ELEMENT_SIZE+mPadding);
            child.layout(left, mPadding, left+ELEMENT_SIZE, mPadding+ELEMENT_SIZE);
        } else {
            final int row = (position-inFirstRow)/mNumberOfColumns+1;
            final int rowIndex = (position-inFirstRow) % mNumberOfColumns;
            
            final int left = mPadding+(mNumberOfColumns-rowIndex-1)*(ELEMENT_SIZE+mPadding);
            final int top  = row*(ELEMENT_SIZE+mPadding);
            child.layout(left, top, left+ELEMENT_SIZE, top+ELEMENT_SIZE);            
        }
    }

    private void addMeasureChild(View child) {
        LayoutParams params = child.getLayoutParams();
        addViewInLayout(child, -1, params, true);

        final int mspec = MeasureSpec.makeMeasureSpec(ELEMENT_SIZE, MeasureSpec.EXACTLY);
        child.measure(mspec, MeasureSpec.UNSPECIFIED);
    }
    
    private void positionItems() {
        int top = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            final int w = child.getMeasuredWidth();
            final int h = child.getMeasuredHeight();

            child.layout(0, top, w, top + w);
            top += h;
        }
    }

}
