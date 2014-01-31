package com.hulzenga.ioi_apps.app_007;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.util.ConstraintEnforcer;
import com.hulzenga.ioi_apps.util.DeveloperTools;

public class WikipediaGame extends DemoActivity implements ButtonsFragment.OptionSelectionListener, StatusFragment.TimeOutListener {

    private static final String TAG                        = "YET_ANOTHER_WIKIPEDIA_GAME";
    private static final int    DESIRED_GAME_OPTION_BUFFER = 6;
    private static final int    MAX_PARALLEL_DOWNLOADS     = 3;
    private static final int    MIN_LINK_LENGTH            = 4;
    private static final int    MAX_NUMBER_OF_LINKS        = 6;

    private static final int    MAX_DOWNLOAD_RETRIES       = 3;
    private static int          mRetriesLeft               = MAX_DOWNLOAD_RETRIES;

    private enum Difficulty {

        EASY(R.string.app_007_easy, 3, 10, 0),
        NORMAL(R.string.app_007_normal, 3, 6, 1),
        HARD(R.string.app_007_hard, 4, 5, 2);

        public final int label;
        public final int numberOfOptions;
        public final int numberOfLinks;
        public final int penalyPoints;

        private Difficulty(int label, int numberOfOptions, int numberOfLinks, int penaltyPoints) {
            this.label = label;
            this.numberOfOptions = numberOfOptions;
            this.numberOfLinks = numberOfLinks;
            this.penalyPoints = penaltyPoints;
        }
    }

    private TextView           mLinkText;
    private TextView           mProgressBarTextView;

    private ButtonsFragment    mButtonsFragment;
    private LinksFragment      mLinksFragment;
    private StatusFragment     mStatusFragment;

    private List<Button>       mButtons            = new ArrayList<Button>();
    private ProgressBar        mProgressBar;

    // handy to keep around
    private Random             mRandom             = new Random();

    private List<Wiki>         mWikiBuffer         = new LinkedList<Wiki>();

    // this should really be an array, but a map just looks nicer
    private Map<Integer, Wiki> mWikisInPlay        = new HashMap<Integer, Wiki>();
    private boolean            mPlayWhenReady      = true;
    private int                mCorrectChoice      = -1;

    private int                mFetchWikiTastCount = 0;
    private Difficulty         mDifficulty;
    private TextView           mDifficultyLabelTextView;
    private SoundPool          mSoundPool;
    private int                mSoundWrong;
    private int                mSoundCorrect;
    private SharedPreferences  mSharedPreferences;
    private AudioManager       mAudioManager;
    private int                mShortAnimationLength;
    private int                mMediumAnimationLength;
    private int                mLongAnimationLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_007_activity_wikipedia_game);

        mSharedPreferences = getSharedPreferences("com.hulzenga.ioi_apps.app_007", Context.MODE_PRIVATE);
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mShortAnimationLength = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mMediumAnimationLength = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mLongAnimationLength = getResources().getInteger(android.R.integer.config_longAnimTime);

        mButtonsFragment = (ButtonsFragment) getFragmentManager().findFragmentById(R.id.app_007_buttonsFragment);
        mLinksFragment = (LinksFragment) getFragmentManager().findFragmentById(R.id.app_007_linksFragment);
        mStatusFragment = (StatusFragment) getFragmentManager().findFragmentById(R.id.app_007_statusFragment);        
        
        mLinkText = (TextView) findViewById(R.id.app_007_linkListText);
        mProgressBar = (ProgressBar) findViewById(R.id.app_007_downloadProgressBar);
        mProgressBarTextView = (TextView) findViewById(R.id.app_007_downloadProgressText);

        mDifficultyLabelTextView = (TextView) findViewById(R.id.app_007_difficultyTextView);

        setDifficulty(Difficulty.HARD);

        nextQuestion();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // setup the SoundPool
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        mSoundCorrect = mSoundPool.load(this, R.raw.app_007_correct, 1);
        mSoundWrong = mSoundPool.load(this, R.raw.app_007_wrong, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // release SoundPoolresources
        mSoundPool.release();
    }

    private void setDifficulty(Difficulty gameDifficulty) {
        mDifficulty = gameDifficulty;

        mDifficultyLabelTextView.setText(getResources().getString(mDifficulty.label));
        mProgressBar.setMax(mDifficulty.numberOfOptions);
        mButtons = mButtonsFragment.setNumberOfButtons(mDifficulty.numberOfOptions);
    }

    private int bufferSpaceRemaining() {
        return DESIRED_GAME_OPTION_BUFFER - (mWikiBuffer.size() + mWikisInPlay.size() + mFetchWikiTastCount);
    }

    private boolean isBufferBigEnough() {
        return mWikiBuffer.size() + mWikisInPlay.size() >= mDifficulty.numberOfOptions;
    }

    private void nextQuestion() {

        // calculate the difference between (the desired buffer size) and
        // (all currently allocated buffers plus running fetch tasks)
        int diff = bufferSpaceRemaining();

        // spawn new FetchWikiTask until either max parallel download count or
        // desired future buffer size is reached
        while (diff > 0 && mFetchWikiTastCount < MAX_PARALLEL_DOWNLOADS) {
            new FetchWikiTask().execute();
            diff--;
        }

        if (!isBufferBigEnough()) {
            // Not enough Wikis in the buffers, so lock up the UI and download
            // more
            lockButtons();
            clearText();
            mPlayWhenReady = true;
            publishProgress();
            showProgressBar();

        } else {
            ShowNextQuestion();
        }
    }

    private void ShowNextQuestion() {
        mPlayWhenReady = false;

        List<Animator> animations = new ArrayList<Animator>();

        for (int i = 0; i < mDifficulty.numberOfOptions; i++) {
            if (!mWikisInPlay.containsKey(i)) {
                mWikisInPlay.put(i, mWikiBuffer.remove(0));
                mButtons.get(i).setText(mWikisInPlay.get(i).mName);
                animations.add(ObjectAnimator.ofFloat(mButtons.get(i), View.ALPHA, 0.0f, 1.0f));
            }
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animations);
        set.setDuration(mShortAnimationLength);
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // do nothing
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // do nothing
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                releaseButtons();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // do nothing
            }
        });
        set.start();

        mCorrectChoice = mRandom.nextInt(mDifficulty.numberOfOptions);
        showWikiLinks(mWikisInPlay.get(mCorrectChoice));
    }

    public void onTimeOut(int score) {
        
    }
    
    public void selectWiki(int selection) {
        // lock buttons to avoid simultaneous input
        lockButtons();

        float volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        AnimatorSet set = new AnimatorSet();
        List<Animator> animations = new ArrayList<Animator>();

        final Button correctButton = mButtons.get(mCorrectChoice);
        final Drawable correctBg = correctButton.getBackground().mutate();
        final Button wrongButton = mButtons.get(selection);
        final Drawable wrongBg = wrongButton.getBackground();
        
        DeveloperTools.makeBackgroundColored(correctButton, Color.GREEN);

        if (selection == mCorrectChoice) {
            mStatusFragment.addPoint();
            
            // correct guess sound
            mSoundPool.play(mSoundCorrect, volume, volume, 0, 0, 1);

            // fade out corect button
            animations.add(ObjectAnimator.ofFloat(mButtons.get(mCorrectChoice), View.ALPHA, 1.0f, 0.0f));
            set.setDuration(mShortAnimationLength);
        } else {
            mStatusFragment.penaltyPoints(mDifficulty.penalyPoints);
            // wrong guess sound
            mSoundPool.play(mSoundWrong, volume, volume, 0, 0, 1);

            DeveloperTools.makeBackgroundColored(wrongButton, Color.RED);
            animations.add(ObjectAnimator.ofFloat(mButtons.get(mCorrectChoice), View.ALPHA, 0.7f, 1.0f, 0.7f, 1.0f));
            
            // rotate the correct button to draw attention to it
            animations.add(ObjectAnimator.ofFloat(mButtons.get(mCorrectChoice), View.SCALE_X, 1.0f, 1.15f, 1.0f));
            animations.add(ObjectAnimator.ofFloat(mButtons.get(mCorrectChoice), View.SCALE_Y, 1.0f, 1.15f, 1.0f));
            set.setDuration(mLongAnimationLength);
        }
        
        
        set.playTogether(animations);
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // do nothing
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // do nothing
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO: update score

                // return the buttons background to their original state
                correctButton.setBackgroundDrawable(correctBg);
                wrongButton.setBackgroundDrawable(wrongBg);
                
                // replace the correct choice with a new wiki from the buffer
                mWikisInPlay.remove(mCorrectChoice);

                nextQuestion();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // do nothing
            }
        });

        set.start();
    }

    private void addWikiToBuffer(Wiki option) {
        mWikiBuffer.add(option);
        if (mPlayWhenReady) {
            publishProgress();

            if (isBufferBigEnough()) {
                hideProgressBar();
                ShowNextQuestion();
            }
        }

        if (bufferSpaceRemaining() > 0) {
            // launch a new FetchWikiPage task
            new FetchWikiTask().execute();
        }
    }

    /**
     * show the download progress by setting the Progressbar and ProgressbarText
     * accordingly
     */
    private void publishProgress() {
        int i = mWikiBuffer.size();

        mProgressBar.setProgress(i);
        mLinkText.setText("downloads remaining (" + ConstraintEnforcer.lowerBound(0, i) + "/"
                + mDifficulty.numberOfOptions + ")");
    }

    /**
     * Make the ProgressBar and ProgressBarTextView visible
     */
    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBarTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the ProgressBar and ProgressBarTextView by making them invisible
     */
    private void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBarTextView.setVisibility(View.INVISIBLE);
    }

    private void bringWikisBackToBuffer() {

        for (Integer key : mWikisInPlay.keySet()) {
            mWikiBuffer.add(mWikisInPlay.get(key));
        }
    }

    private void showWikiLinks(Wiki wiki) {
        StringBuilder sb = new StringBuilder();

        sb.append("<ol>");
        for (String link : wiki.getAssociatedLinks()) {
            sb.append("&#8226;" + link + "<br/>");
        }
        sb.append("</ol>");

        mLinkText.setText(Html.fromHtml(sb.toString()));
    }

    private void clearText() {
        mLinkText.setText("");
    }

    private void lockButtons() {
        for (Button button : mButtons) {
            button.setClickable(false);
        }
    }

    private void releaseButtons() {
        for (Button button : mButtons) {
            button.setClickable(true);
        }
    }

    private class FetchWikiTask extends AsyncTask<ProgressBar, Integer, Wiki> {

        private static final String RANDOM_WIKI_PAGE_URL = "http://en.m.wikipedia.org/wiki/Special:Random";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFetchWikiTastCount++;
        }

        @Override
        protected Wiki doInBackground(ProgressBar... params) {

            String name = null;
            List<String> links = new ArrayList<String>(MAX_NUMBER_OF_LINKS);

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
                Log.e(TAG, "IO Exception: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception while trying to download/parse random wiki page: " + e.getMessage());
            }

            return new Wiki(name, links);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Wiki result) {
            mFetchWikiTastCount--;

            if (result.getName() != null && result.getAssociatedLinks().size() > 0) {
                mRetriesLeft = MAX_DOWNLOAD_RETRIES;
                addWikiToBuffer(result);
            } else {
                mRetriesLeft--;

                // if there are no retries left and there are not enough Wikis
                // in the buffer to play a new round the app finishes
                if (mRetriesLeft <= 0 && !isBufferBigEnough()) {
                    Toast.makeText(WikipediaGame.this, getResources().getString(R.string.app_007_downloadFailure),
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    new FetchWikiTask().execute();
                }
            }
        }
    }

    private class Wiki {
        private String       mName;
        private List<String> mLinks;

        public Wiki(String name, List<String> links) {
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
