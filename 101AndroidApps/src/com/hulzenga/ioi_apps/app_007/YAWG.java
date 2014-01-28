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
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
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

public class YAWG extends Activity implements FragmentButtons.OptionSelectionListener {

    private static final String TAG                        = "YET_ANOTHER_WIKIPEDIA_GAME";
    private static final int    DESIRED_GAME_OPTION_BUFFER = 4;
    private static final int    MAX_PARALLEL_DOWNLOADS     = 3;
    private static final int    MIN_LINK_LENGTH            = 4;
    private static final int    MAX_NUMBER_OF_LINKS        = 6;

    private enum Difficulty {

        EASY(R.string.app_007_easy, 3, 10),
        NORMAL(R.string.app_007_normal, 3, 6),
        HARD(R.string.app_007_hard, 4, 5);

        public final int label;
        public final int numberOfOptions;
        public final int numberOfLinks;

        private Difficulty(int label, int numberOfOptions, int numberOfLinks) {
            this.label = label;
            this.numberOfOptions = numberOfOptions;
            this.numberOfLinks = numberOfLinks;
        }
    }

    private TextView         mLinkText;
    private TextView         mProgressBarTextView;

    private FragmentButtons  mFragmentButtons;
    private FragmentLinks    mFragmentLinks;
    private FragmentStatus   mFragmentStatus;

    private List<Button>     mOptionButtons      = new ArrayList<Button>();
    private ProgressBar      mProgressBar;

    // handy to keep around
    private Random           mRandom             = new Random();

    private List<GameOption> mGameOptionBuffer   = new LinkedList<GameOption>();
    private List<GameOption> mGameOptionsInPlay  = new LinkedList<GameOption>();
    private boolean          mLaunchWhenReady    = true;
    private int              mCorrectChoice      = -1;
    private String           mCorrectName;

    private int              mFetchWikiPageCount = 0;
    private Difficulty       mGameDifficulty;
    private TextView         mDifficultyLabelTextView;
    private SoundPool mSoundPool;
    private int mSoundWrong;
    private int mSoundCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_007_activity_yawg);

        mFragmentButtons = (FragmentButtons) getFragmentManager().findFragmentById(R.id.app_007_fragmentButtons);
        mFragmentLinks = (FragmentLinks) getFragmentManager().findFragmentById(R.id.app_007_fragmentLinks);
        mFragmentStatus = (FragmentStatus) getFragmentManager().findFragmentById(R.id.app_007_fragmentStatus);

        mLinkText = (TextView) findViewById(R.id.app_007_linkListText);
        mProgressBar = (ProgressBar) findViewById(R.id.app_007_downloadProgressBar);
        mProgressBarTextView = (TextView) findViewById(R.id.app_007_downloadProgressText);

        mDifficultyLabelTextView = (TextView) findViewById(R.id.app_007_difficultyTextView);

        setDifficulty(Difficulty.HARD);

        startNewGame();
    }
    
    @Override
    protected void onResume() {    
        super.onResume();
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        mSoundCorrect = mSoundPool.load(this, R.raw.app_007_correct, 1);
        mSoundWrong = mSoundPool.load(this, R.raw.app_007_wrong, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundPool.release();
    }
    

    public void setDifficulty(Difficulty gameDifficulty) {
        mGameDifficulty = gameDifficulty;

        mDifficultyLabelTextView.setText(getResources().getString(mGameDifficulty.label));
        mProgressBar.setMax(mGameDifficulty.numberOfOptions);
        mOptionButtons = mFragmentButtons.setNumberOfButtons(mGameDifficulty.numberOfOptions);
    }

    public void startNewGame() {
        lockButtons();
        clearText();

        if (mGameOptionBuffer.size() < mGameDifficulty.numberOfOptions) {
            mLaunchWhenReady = true;
            publishProgress();
            showProgressBar();

            // start up to MAX_PARALLEL_DOWNLOADS of FetchWikiPageTask
            while (mFetchWikiPageCount < MAX_PARALLEL_DOWNLOADS) {
                new FetchWikiPageTask().execute();
            }
        } else {
            launchGame();
        }
    }

    public void launchGame() {
        mLaunchWhenReady = false;
        bringOptionsIntoPlay();
        
        mCorrectChoice = mRandom.nextInt(mGameDifficulty.numberOfOptions);
        showOption(mGameOptionsInPlay.get(mCorrectChoice));

        for (int i = 0; i < mGameDifficulty.numberOfOptions; i++) {
            mOptionButtons.get(i).setText(mGameOptionsInPlay.get(i).mName);            
        }
        releaseButtons();
    }

    public void addGameOption(GameOption option) {
        mGameOptionBuffer.add(option);
        if (mLaunchWhenReady) {
            publishProgress();

            if (mGameOptionBuffer.size() >= mGameDifficulty.numberOfOptions) {
                hideProgressBar();
                launchGame();
            }
        }

        if (mGameOptionBuffer.size() < DESIRED_GAME_OPTION_BUFFER - mFetchWikiPageCount) {
            // launch a new FetchWikiPage task
            new FetchWikiPageTask().execute();
        }
    }

    private void publishProgress() {
        int i = mGameOptionBuffer.size();

        mProgressBar.setProgress(i);
        mLinkText.setText("downloads remaining (" + ((3 - i) >= 0 ? (3 - i) : 0) + "/3)");
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBarTextView.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBarTextView.setVisibility(View.INVISIBLE);
    }

    private void bringBackToBuffer() {
        while (mGameOptionsInPlay.size() > 0) {
            mGameOptionBuffer.add(mGameOptionsInPlay.remove(0));
        }
    }

    private void bringOptionsIntoPlay() {        
        while (mGameOptionsInPlay.size() < mGameDifficulty.numberOfOptions) {
            mGameOptionsInPlay.add(mGameOptionBuffer.remove(0));
        }
    }

    private void showOption(GameOption option) {
        StringBuilder sb = new StringBuilder();

        sb.append("<ol>");
        for (String link : option.getAssociatedLinks()) {
            sb.append("&#8226;" + link + "<br/>");
        }
        sb.append("</ol>");

        mLinkText.setText(Html.fromHtml(sb.toString()));
    }

    private void clearText() {
        mLinkText.setText("");
    }
    
    private void lockButtons() {
        for (Button button : mOptionButtons) {
            button.setClickable(false);
        }
    }

    private void releaseButtons() {
        for (Button button : mOptionButtons) {
            button.setClickable(true);
        }
    }

    public void selectOption(int selection) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (selection == mCorrectChoice) {
            mSoundPool.play(mSoundCorrect, volume, volume, 0, 0, 1);
        } else {
            mSoundPool.play(mSoundWrong, volume, volume, 0, 0, 1);
        }
                
        mGameOptionsInPlay.remove(mCorrectChoice);
        mGameOptionsInPlay.add(mCorrectChoice, mGameOptionBuffer.remove(0));
        startNewGame();
    }

    private class FetchWikiPageTask extends AsyncTask<ProgressBar, Integer, GameOption> {

        private static final String RANDOM_WIKI_PAGE_URL = "http://en.m.wikipedia.org/wiki/Special:Random";

        @Override
        protected void onPreExecute() {            
            super.onPreExecute();
            mFetchWikiPageCount++;
        }
        
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
            mFetchWikiPageCount--;
            
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
        private List<String> mLinks;

        public GameOption(String name, List<String> links) {
            mName = name;
            mLinks = links;
        }

        public String getName() {
            return mName;
        }

        public List<String> getAssociatedLinks() {
            return mLinks;
        }
    }
}
