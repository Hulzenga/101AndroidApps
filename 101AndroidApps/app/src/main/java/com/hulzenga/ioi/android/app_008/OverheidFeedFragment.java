package com.hulzenga.ioi.android.app_008;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OverheidFeedFragment extends Fragment {

  private static final String TAG = "OverheidFeedFragment";

  private static final String FEED_NAME    = "feed name";
  private static final String FEED_ADDRESS = "feed address";

  private String mFeedAddress;

  private ListView mListView;
  private OverheidFeedAdapter mAdapter;

  public OverheidFeedFragment() {}

  public static Fragment newInstance(OverheidFeed feed) {
    OverheidFeedFragment overheidFeedFragment = new OverheidFeedFragment();
    Bundle bundle = new Bundle();
    bundle.putString(FEED_NAME, feed.getName());
    bundle.putString(FEED_ADDRESS, feed.getAddress());
    overheidFeedFragment.setArguments(bundle);
    return overheidFeedFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    mFeedAddress = getArguments().getString(FEED_ADDRESS);

    mListView = new ListView(getActivity());
    mAdapter = new OverheidFeedAdapter(getActivity(), new ArrayList<OverheidFeedItem>());

    mListView.setAdapter(mAdapter);

    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mAdapter.getItem(position).getLink()));

        startActivity(intent);
      }
    });

    return mListView;
  }


  @Override
  public void onResume() {
    super.onResume();

    new OverheidFeedDownloadTask().execute(mFeedAddress);
  }

  private class OverheidFeedDownloadTask extends AsyncTask<String, Integer, List<OverheidFeedItem>> {

    @Override
    protected List<OverheidFeedItem> doInBackground(String... params) {


      try {
        URL url = new URL(params[0]);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(connection.getInputStream());

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);

        return readFeed(parser);

      } catch (MalformedURLException e) {
        Log.e(TAG, "MalformedURLException: " + e.getMessage());
      } catch (IOException e) {
        Log.e(TAG, "IOException: " + e.getMessage());
      } catch (XmlPullParserException e) {
        Log.e(TAG, "XmlPullParserException: " + e.getMessage());
      }

      //return empty list
      return new ArrayList<OverheidFeedItem>();
    }

    @Override
    protected void onPostExecute(List<OverheidFeedItem> feedItems) {
      mAdapter.addAll(feedItems);
    }

    private List<OverheidFeedItem> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {

      List<OverheidFeedItem> feedItems = new ArrayList<OverheidFeedItem>();

      while (parser.next() != XmlPullParser.END_DOCUMENT) {

        if (parser.getEventType() != XmlPullParser.START_TAG) {
          continue;
        }

        switch (parser.getName()) {
          case "item":
            feedItems.add(readItem(parser));
            break;
          default:

        }
      }

      return feedItems;
    }

    private OverheidFeedItem readItem(XmlPullParser parser) throws IOException, XmlPullParserException  {
      String title = "", link = "", pubDate = "", description = "";

      while (parser.next() != XmlPullParser.END_TAG) {

        if (parser.getEventType() != XmlPullParser.START_TAG) {
          continue;
        }

        switch (parser.getName()) {
          case "title":
            title = readText(parser);
            break;
          case "link":
            link = readText(parser);
            break;
          case "pubDate":
            pubDate = readText(parser);
            break;
          case "description":
            description = readText(parser);
            break;
          default:
            skip(parser);
        }
      }

      return new OverheidFeedItem(title, link, pubDate, description);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException  {

      parser.next();
      String text = parser.getText();
      text = text!= null ? text : "";
      parser.next();

      return text;
    }

    private void skip(XmlPullParser parser) throws IOException, XmlPullParserException  {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        throw new IllegalStateException();
      }
      int depth = 1;
      while (depth != 0) {
        switch (parser.next()) {
          case XmlPullParser.END_TAG:
            depth--;
            break;
          case XmlPullParser.START_TAG:
            depth++;
            break;
        }
      }
    }


  }

}
