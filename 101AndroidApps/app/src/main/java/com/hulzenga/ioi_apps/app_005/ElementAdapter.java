package com.hulzenga.ioi_apps.app_005;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.app_005.ElementAdapter.ElementChangeObserver.ChangeType;

public class ElementAdapter extends ArrayAdapter<Element> {

    private Context               mContext;
    private List<Element>         mElements;
    private Map<Element, Integer> mElementIdMap   = new HashMap<Element, Integer>();
    private int                   mIdCount        = 0;

    private int                   mDraggedElement = -1;

    private ElementChangeObserver mElementChangeObserver;

    private static Bitmap          sEarthElement;
    private static Bitmap          sAirElement;
    private static Bitmap          sFireElement;
    private static Bitmap          sWaterElement;
    private static Bitmap          sEmptyElement;

    // more practical observer for changes in the underlying dataset
    public interface ElementChangeObserver {
        enum ChangeType {
            ELEMENT_ADDED, ELEMENT_REMOVED, STARTED_DRAGGING, STOPPED_DRAGGING, ELEMENTS_SWAPPED
        }

        public void onElementChange(ChangeType type, int... args);
    }

    public ElementAdapter(Context context, int layoutViewId, List<Element> elements) {

        super(context, layoutViewId, elements);

        mContext = context;
        mElements = elements;

        //earth element not loaded, assume all elements not loaded
        if (sEarthElement == null) {
            sEarthElement = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_005_earth_element);
            sAirElement = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_005_air_element);
            sFireElement = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_005_fire_element);
            sWaterElement = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_005_water_element);
            sEmptyElement = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_005_empty_element);
        }

        allocateIds();
    }

    public void registerElementChangeObserver(ElementChangeObserver observer) {
        mElementChangeObserver = observer;
    }

    public List<Element> getAllElements() {
        return mElements;
    }

    private void allocateIds() {
        mIdCount = mElements.size();

        for (int id = 0; id < mIdCount; id++) {
            mElementIdMap.put(mElements.get(id), id);
        }
    }

    @Override
    public void add(Element element) {
        mElements.add(0, element);
        mElementIdMap.put(element, mIdCount++);
        notifyElementChange(ChangeType.ELEMENT_ADDED);
    }

    public void stopDragging() {
        mDraggedElement = -1;
        notifyElementChange(ChangeType.STOPPED_DRAGGING);
    }

    public void startDragging(int position) {
        mDraggedElement = position;
        notifyElementChange(ChangeType.STARTED_DRAGGING, position);
    }

    public void swap(int position1, int position2) {
        mDraggedElement = -1;
        
        Element e1 = getItem(position1);
        Element e2 = getItem(position2);

        mElements.set(position1, e2);
        mElements.set(position2, e1);

        notifyElementChange(ChangeType.ELEMENTS_SWAPPED, position1, position2);
    }

    public void removeItem(int position) {
        mElementIdMap.remove(mElements.get(position));
        mElements.remove(position);
        notifyElementChange(ChangeType.ELEMENT_REMOVED, position);
    }

    @Override
    public Element getItem(int position) {
        return mElements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mElementIdMap.get(mElements.get(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ElementView elementView;
        if (convertView != null) {
            elementView = (ElementView) convertView;
            elementView.setPosition(position);
        } else {
            elementView = new ElementView(mContext, position);
        }

        if (position != mDraggedElement) {
            switch (mElements.get(position).getType()) {
            case EARTH:
                elementView.setImageBitmap(sEarthElement);
                break;
            case AIR:
                elementView.setImageBitmap(sAirElement);
                break;
            case FIRE:
                elementView.setImageBitmap(sFireElement);
                break;
            case WATER:
                elementView.setImageBitmap(sWaterElement);
                break;
            }
        } else {
            elementView.setImageBitmap(sEmptyElement);
        }

        
        return elementView;
    }

    /**
     * notifyElementChange notifies the ElementChangeObserver of changes in the
     * data.
     */
    public void notifyElementChange(ChangeType type, int... args) {
        if (mElementChangeObserver != null) {
            mElementChangeObserver.onElementChange(type, args);
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
