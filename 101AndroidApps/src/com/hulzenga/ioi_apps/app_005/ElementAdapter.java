package com.hulzenga.ioi_apps.app_005;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
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
    private Map<Element, Integer> mElementIdMap = new HashMap<Element, Integer>();
    private int                   mElementCount = 0;

    static class ViewHolder {
        ImageView mElementImageView;
        TextView mElementTextView;
    }

    public ElementAdapter(Context context, int layoutViewId, List<Element> elements) {

        super(context, layoutViewId, elements);

        mContext = context;
        mElements = elements;

        setStartIds();
    }

    private void setStartIds() {

        mElementCount = mElements.size();

        for (int id = 0; id < mElementCount; id++) {
            mElementIdMap.put(mElements.get(id), id);
        }
    }

    @Override
    public void add(Element element) {
        mElements.add(element);
        mElementIdMap.put(element, mElementCount++);
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
        Log.d("adapter", "view position: " + position);
        View elementView = convertView;

        ViewHolder viewHolder;
        
        // apply View Holder pattern optimization
        if (elementView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            elementView = inflater.inflate(R.layout.app_005_item_element, null);
            viewHolder = new ViewHolder();
            viewHolder.mElementImageView = (ImageView) elementView.findViewById(R.id.app_005_elementItemImage);
            viewHolder.mElementTextView  = (TextView) elementView.findViewById(R.id.app_005_elementPosition);
            
            elementView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) elementView.getTag();
        }

        viewHolder.mElementTextView.setText("P:"+position);
        return elementView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
