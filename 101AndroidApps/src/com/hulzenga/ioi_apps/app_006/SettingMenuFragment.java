package com.hulzenga.ioi_apps.app_006;

import java.util.ArrayList;
import java.util.List;

import com.hulzenga.ioi_apps.R;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SettingMenuFragment extends Fragment {

    public static List<String> settings = new ArrayList<String>();
    static {
        settings.add("Focus");
        settings.add("Image size");
        settings.add("Scene mode");
        settings.add("ISO");
        settings.add("White balance");
        settings.add("Timer");
    }

    public static SettingMenuFragment newInstance() {
        SettingMenuFragment fragment = new SettingMenuFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView view = new ListView(getActivity());
        view.setBackgroundResource(R.drawable.app_006_item_background);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, settings);
        
        view.setAdapter(adapter);
        
        return view;        
    }
}
