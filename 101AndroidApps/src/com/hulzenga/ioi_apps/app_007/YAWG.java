package com.hulzenga.ioi_apps.app_007;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hulzenga.ioi_apps.R;

public class YAWG extends Activity {

    private static final String TAG                        = "YET_ANOTHER_WIKIPEDIA_GAME";
    private static final int    DESIRED_GAME_OPTION_BUFFER = 9;
    private static final int    MAX_PARALLEL_DOWNLOADS     = 3;
    private static final int    MIN_LINK_LENGTH            = 4;
    private static final int    MAX_NUMBER_OF_LINKS        = 6;

    private TextView            mTextView;
    private TextView            mProgressBarTextView;
    private Button              mOptionButton1;
    private Button              mOptionButton2;
    private Button              mOptionButton3;
    private ProgressBar         mProgressBar;

    // handy to keep around
    private Random              mRandom                    = new Random();

    private List<GameOption>    mGameOptions               = new LinkedList<GameOption>();
    private boolean             mLaunchWhenReady           = true;
    private int                 mCorrectChoice             = -1;
    private String              mCorrectName;

    private int                 mActiveDownloadersCount    = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_007_activity_yawg);

        mTextView = (TextView) findViewById(R.id.app_007_linkListText);
        mProgressBar = (ProgressBar) findViewById(R.id.app_007_downloadProgressBar);
        mProgressBar.setMax(3);
        mProgressBarTextView = (TextView) findViewById(R.id.app_007_downloadProgressText);
        mOptionButton1 = (Button) findViewById(R.id.button1);
        mOptionButton2 = (Button) findViewById(R.id.button2);
        mOptionButton3 = (Button) findViewById(R.id.button3);

        startGame();
    }

    public void startGame() {
        lockButtons();
        clearScreen();

        if (mGameOptions.size() < 3) {
            mLaunchWhenReady = true;
            publishProgress();
            showProgress();

            for (int i = 0; i < MAX_PARALLEL_DOWNLOADS; i++) {
                new FetchWikiPageTask().execute();
                mActiveDownloadersCount++;
            }
        } else {
            launchGame();

            // start up to 2 new downloading tasks to replace discarded game
            // options
            while (mActiveDownloadersCount < 2) {
                new FetchWikiPageTask().execute();
                mActiveDownloadersCount++;
            }
        }
    }

    public void launchGame() {
        mLaunchWhenReady = false;

        GameOption correctOption = pickAndPopOption();
        mCorrectName = correctOption.getName();
        showOption(correctOption);

        mCorrectChoice = mRandom.nextInt(3) + 1;
        switch (mCorrectChoice) {
        case 1:
            mOptionButton1.setText(mCorrectName);
            mOptionButton2.setText(pickAndPopOption().getName());
            mOptionButton3.setText(randomOption().getName());
            break;
        case 2:
            mOptionButton2.setText(mCorrectName);
            mOptionButton3.setText(pickAndPopOption().getName());
            mOptionButton1.setText(randomOption().getName());
            break;
        case 3:
            mOptionButton3.setText(mCorrectName);
            mOptionButton1.setText(pickAndPopOption().getName());
            mOptionButton2.setText(randomOption().getName());
            break;

        }
        releaseButtons();
    }

    public void addGameOption(GameOption option) {
        mGameOptions.add(option);
        if (mLaunchWhenReady) {
            publishProgress();

            if (mGameOptions.size() >= 3) {
                hideProgress();
                launchGame();
            }
        }

        if (mGameOptions.size() < DESIRED_GAME_OPTION_BUFFER) {
            // add a new game option to the buffer
            new FetchWikiPageTask().execute();
        } else {
            mActiveDownloadersCount--;
        }
    }

    private void publishProgress() {
        int i = mGameOptions.size();

        mProgressBar.setProgress(i);
        mTextView.setText("downloads remaining (" + ((3 - i) >= 0 ? (3 - i) : 0) + "/3)");
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBarTextView.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBarTextView.setVisibility(View.INVISIBLE);
    }

    private GameOption randomOption() {
        return mGameOptions.get(mRandom.nextInt(mGameOptions.size()));
    }

    private GameOption pickAndPopOption() {
        return mGameOptions.remove(mRandom.nextInt(mGameOptions.size()));
    }

    private void showOption(GameOption option) {
        StringBuilder sb = new StringBuilder();

        sb.append("<ol>");
        for (String link : option.getAssociatedLinks()) {
            sb.append("&#8226;" + link + "<br/>");
        }
        sb.append("</ol>");

        mTextView.setText(Html.fromHtml(sb.toString()));
    }

    private void clearScreen() {
        mTextView.setText("");
        mOptionButton1.setEnabled(false);
        mOptionButton2.setEnabled(false);
        mOptionButton3.setEnabled(false);
    }

    private void lockButtons() {
        mOptionButton1.setText("");
        mOptionButton2.setText("");
        mOptionButton3.setText("");
    }

    private void releaseButtons() {
        mOptionButton1.setEnabled(true);
        mOptionButton2.setEnabled(true);
        mOptionButton3.setEnabled(true);
    }

    public void selectOption1(View view) {
        checkSelection(1);
    }

    public void selectOption2(View view) {
        checkSelection(2);
    }

    public void selectOption3(View view) {
        checkSelection(3);
    }

    public void checkSelection(int selection) {
        if (selection == mCorrectChoice) {
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
            
        } else {
            Toast.makeText(this, "Wrong", Toast.LENGTH_SHORT).show();
        }
        startGame();
    }

    private class FetchWikiPageTask extends AsyncTask<ProgressBar, Integer, GameOption> {

        private static final String RANDOM_WIKI_PAGE_URL = "http://en.m.wikipedia.org/wiki/Special:Random";

        @Override
        protected GameOption doInBackground(ProgressBar... params) {

            // the to be returned string builder
            String name = null;
            List<String> links = new ArrayList<String>(MAX_NUMBER_OF_LINKS);

            // make reference to the urlConnection here so it can be referenced
            // in the finally block below
            HttpURLConnection urlConnection = null;

            try {

                Document doc = Jsoup.connect(RANDOM_WIKI_PAGE_URL).timeout(10000).get();
                String fullname = doc.title();
                name = fullname.substring(0, fullname.length() - 35);

                // select the main content div
                Element content = doc.select("div#content > div").first();

                // remove open sections (see also, references etc.)
                content.select("div.section_heading openSection").remove();

                List<String> linkPool = new LinkedList<String>();
                String link;
                for (Element element : content.select("a[href]:not([href^=#])")) {
                    link = element.text();

                    /*
                     * do not include any non descriptive, blank links (such as
                     * image links) or links whose text is a hyperlink
                     */
                    if (link.length() >= MIN_LINK_LENGTH && !link.contains("http://")) {
                        linkPool.add(link);
                    }
                }

                while (links.size() < MAX_NUMBER_OF_LINKS && linkPool.size() > 0) {
                    int r = mRandom.nextInt(linkPool.size());

                    links.add(linkPool.get(r));
                    linkPool.remove(r);
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "malformed URL, this should not be possible");
            } catch (IOException e) {
                Log.e(TAG, "IO Exception");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return new GameOption(name, links);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(GameOption result) {
            if (result.getName() != null && result.getAssociatedLinks().size() > 0) {
                addGameOption(result);
            } else {

                // try again TODO: add user feedback
                new FetchWikiPageTask().execute();
            }
        }
    }

    private class GameOption {
        private String       mName;
        private List<String> mAssociatedLinks;

        public GameOption(String name, List<String> associatedLinks) {
            mName = name;
            mAssociatedLinks = associatedLinks;
        }

        public String getName() {
            return mName;
        }

        public List<String> getAssociatedLinks() {
            return mAssociatedLinks;
        }
    }
}
