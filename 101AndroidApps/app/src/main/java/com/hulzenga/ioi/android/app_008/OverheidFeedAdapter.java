package com.hulzenga.ioi.android.app_008;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hulzenga.ioi.android.R;

import java.util.List;

/**
 * Created by jouke on 19-4-14.
 */
public class OverheidFeedAdapter extends ArrayAdapter<OverheidFeedItem> {

  public OverheidFeedAdapter(Context context, List<OverheidFeedItem> list) {
    super(context, R.layout.app_008_item_feed, list);
  }

  private static class FeedItemHolder {
    TextView titleView;
    TextView pubDateView;
    TextView descriptionView;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.app_008_item_feed, null);

      FeedItemHolder holder = new FeedItemHolder();
      holder.titleView = (TextView) convertView.findViewById(R.id.app_008_item_feed_title);
      holder.pubDateView = (TextView) convertView.findViewById(R.id.app_008_item_feed_pub_date);
      holder.descriptionView = (TextView) convertView.findViewById(R.id.app_008_item_feed_description);

      convertView.setTag(holder);
    }

    FeedItemHolder holder = (FeedItemHolder) convertView.getTag();
    holder.titleView.setText(Html.fromHtml(getItem(position).getTitle()));
    holder.pubDateView.setText(Html.fromHtml(getItem(position).getPubDate()));
    holder.descriptionView.setText(Html.fromHtml(getItem(position).getDescription()));

    return convertView;
  }
}
