package com.hulzenga.ioi_apps.app_007;


import com.hulzenga.ioi_apps.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentStatus extends Fragment{

    public void setScore() {
        
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        return inflater.inflate(R.layout.app_007_fragment_status, container, false);
    }
}
