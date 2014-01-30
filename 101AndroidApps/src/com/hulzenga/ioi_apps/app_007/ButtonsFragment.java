package com.hulzenga.ioi_apps.app_007;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;

import com.hulzenga.ioi_apps.R;

public class ButtonsFragment extends Fragment {

    private static final String     TAG = "FRAGMENT_BUTTONS";

    private LinearLayout            mButtonContainer;
    private OptionSelectionListener mOptionSelectionListener;

    public interface OptionSelectionListener {
        public void selectWiki(int selection);
    }

    public List<Button> setNumberOfButtons(int numberOfButtons) {

        if (mButtonContainer != null) {
            mButtonContainer.removeAllViews();
        } else {
            Log.e(TAG, "setNumberOfButtons() was called before View was created");
            return null;
        }

        List<Button> buttons = new ArrayList<Button>();
        LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);

        for (int i = 0; i < numberOfButtons; i++) {
            final int index = i;
            Button button = new Button(mButtonContainer.getContext());
            button.setLayoutParams(params);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    
                    mOptionSelectionListener.selectWiki(index);                    
                }
            });

            buttons.add(button);
            mButtonContainer.addView(button);
        }

        mButtonContainer.requestLayout();
        return buttons;
    }

    @Override
    public void onAttach(Activity activity) {        
        super.onAttach(activity);
        
        try {
            mOptionSelectionListener = (OptionSelectionListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, activity.toString() + " must implement OptionSelectionListener");
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.app_007_fragment_buttons, container, false);
        mButtonContainer = (LinearLayout) fragmentView.findViewById(R.id.app_007_optionButtonLayout);

        return fragmentView;
    }

}
