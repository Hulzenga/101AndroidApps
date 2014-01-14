package com.hulzenga.ioi_apps.app_005;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hulzenga.ioi_apps.R;

public class ElementAdapter extends ArrayAdapter<Element> {

    private Context               mContext;
    private List<Element>         mElements;
    private Map<Element, Integer> mElementIdMap   = new HashMap<Element, Integer>();
    private int                   mIdCount        = 0;
    private int                   mRemovedItemPosition;

    private int                   mDraggedElement = -1;
    
    private static Bitmap         sEarthElement;
    private static Bitmap         sAirElement;
    private static Bitmap         sFireElement;
    private static Bitmap         sWaterElement;

    static class ViewHolder {
        ImageView mElementImageView;
        TextView  mElementTextView;
    }

    public ElementAdapter(Context context, int layoutViewId, List<Element> elements) {

        super(context, layoutViewId, elements);

        mContext = context;
        mElements = elements;

        sEarthElement = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_005_earth_element);
        sAirElement = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_005_air_element);
        sFireElement = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_005_fire_element);
        sWaterElement = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_005_water_element);

        setStartIds();
    }

    private void setStartIds() {
        mIdCount = mElements.size();

        for (int id = 0; id < mIdCount; id++) {
            mElementIdMap.put(mElements.get(id), id);
        }
    }

    @Override
    public void add(Element element) {
        mElements.add(0, element);
        mElementIdMap.put(element, mIdCount++);
        notifyDataSetChanged();
    }

    public void swap(int position1, int position2) {
        Element e1 = getItem(position1);
        Element e2 = getItem(position2);

        mElements.set(position1, e2);
        mElements.set(position2, e1);

        notifyDataSetChanged();
    }

    public int getRemovedItemPosition() {
        return mRemovedItemPosition;
    }

    public void removeItem(int position) {
        mElements.remove(position);
        mRemovedItemPosition = position;
        notifyDataSetChanged();
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
        } else {
            elementView = new ElementView(mContext);
        }

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

        return elementView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
