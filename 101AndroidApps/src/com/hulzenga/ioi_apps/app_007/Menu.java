package com.hulzenga.ioi_apps.app_007;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class Menu extends DemoActivity {

    private static final String TAG               = "Menu";
    private boolean             mInPickDifficulty = false;

    private View                mBaseMenuContainer;
    private View                mDifficultyContainer;
    private ImageView           mBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_007_activity_menu);

        mBaseMenuContainer = findViewById(R.id.app_007_baseMenuContainer);
        mDifficultyContainer = findViewById(R.id.app_007_difficultyContainer);
        mBackground = (ImageView) findViewById(R.id.app_007_background);
    }

    public void play(View view) {
        mInPickDifficulty = true;

        mBackground.animate().translationX(-100f).setDuration(getResources().getInteger(R.integer.animation_medium));
        // Animator anim = ObjectAnimator.ofFloat(mDifficultyContainer,
        // View.TRANSLATION_X, 100f, 0f);
        mBaseMenuContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.left_slide_out));
        mBaseMenuContainer.setVisibility(View.GONE);

        mDifficultyContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.right_slide_in));
        mDifficultyContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (mInPickDifficulty) {
            mInPickDifficulty = false;

            mBackground.animate().translationX(0).setDuration(getResources().getInteger(R.integer.animation_medium));
            
            mBaseMenuContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.left_slide_in));
            mBaseMenuContainer.setVisibility(View.VISIBLE);

            mDifficultyContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.right_slide_out));
            mDifficultyContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public void review(View view) {

    }

    public void pickDifficulty(View view) {
        Intent i = new Intent(this, com.hulzenga.ioi_apps.app_007.Game.class);
        switch (view.getId()) {
        case R.id.app_007_easyButton:
            i.putExtra(Game.BUNDLE_DIFFICULTY, 0);
            break;
        case R.id.app_007_mediumButton:
            i.putExtra(Game.BUNDLE_DIFFICULTY, 1);
            break;
        case R.id.app_007_hardButton:
            i.putExtra(Game.BUNDLE_DIFFICULTY, 2);
            break;
        default:
            Log.e(TAG, "Unknown button selected, this should not be possible !");
        }

        startActivity(i);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }
}
