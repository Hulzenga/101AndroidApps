package com.hulzenga.ioi.android.app_007;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hulzenga.ioi.android.AppActivity;
import com.hulzenga.ioi.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Review extends AppActivity {

  private static final String NAME    = "name";
  private static final String ADDRESS = "adress";
  private static final String CORRECT = "corect";

  private ListView    mWikiListView;
  private ListAdapter mAdapter;

  private List<Wiki> playedWikis;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    playedWikis = Wiki.loadWikis(this, WikiGameActivity.SAVED_AND_PLAYED_WIKIS, false);

    List<Map<String, Object>> wikiList = new ArrayList<Map<String, Object>>();
    for (Wiki wiki : playedWikis) {
      Map<String, Object> item = new HashMap<String, Object>();
      if (wiki.isCorrect()) {
        item.put(CORRECT, R.drawable.app_007_correct);
      } else {
        item.put(CORRECT, R.drawable.app_007_wrong);
      }
      item.put(NAME, wiki.getName());
      item.put(ADDRESS, wiki.getAddress());
      wikiList.add(item);
    }

    mAdapter = new SimpleAdapter(this, wikiList, R.layout.app_007_item_review, new String[]{CORRECT, NAME},
        new int[]{R.id.app_007_correctImage, R.id.app_007_reviewName});

    mWikiListView = new ListView(this);
    mWikiListView.setAdapter(mAdapter);

    mWikiListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(playedWikis.get(position).getAddress()));
        startActivity(i);
      }
    });
    setContentView(mWikiListView);
  }
}
