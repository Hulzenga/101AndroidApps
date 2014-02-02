package com.hulzenga.ioi_apps.app_007;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class WikipediaGameMenu extends DemoActivity {

    private static final String TAG = "REVIEW";
    private boolean mInPickDifficulty = false;
    
    private View mBaseMenuContainer;
    private View mDifficultyContainer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_007_activity_menu);
        
        mBaseMenuContainer = findViewById(R.id.app_007_baseMenuContainer);
        mDifficultyContainer = findViewById(R.id.app_007_difficultyContainer);
        
    }

    public void play(View view) {
        mInPickDifficulty = true;
        
        mBaseMenuContainer.setVisibility(View.GONE);
        mDifficultyContainer.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onBackPressed() {
        if (mInPickDifficulty) {
            mInPickDifficulty = false;
            mBaseMenuContainer.setVisibility(View.VISIBLE);
            mDifficultyContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }        
    }

    public void review(View view) {

    }

    public void pickDifficulty(View view) {
        Intent i = new Intent(this, com.hulzenga.ioi_apps.app_007.WikipediaGame.class);
        switch (view.getId()) {
        case R.id.app_007_easyButton:
            i.putExtra(WikipediaGame.BUNDLE_DIFFICULTY, 0);
            break;
        case R.id.app_007_mediumButton:
            i.putExtra(WikipediaGame.BUNDLE_DIFFICULTY, 1);
            break;
        case R.id.app_007_hardButton:
            i.putExtra(WikipediaGame.BUNDLE_DIFFICULTY, 2);
            break;
        default:
            Log.e(TAG, "Unknown button selected, this should not be possible !");
        }
        
        startActivity(i);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }
}
