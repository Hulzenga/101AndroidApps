package com.hulzenga.ioi_apps.app_006;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.hulzenga.ioi_apps.R;

public class CameraFragment extends Fragment {

    private static final String       TAG         = "CameraFragment";

    private GridView                  mCameraGridView;
    private ListAdapter               mAdapter;
    private SettingChangeListener     mSettingChangeListener;

    private List<Map<String, Object>> mCameras    = new ArrayList<Map<String, Object>>();
    private static final String       FACING_ICON = "FACING_ICON";
    private static final String       NUMBER      = "NUMBER";

    public static CameraFragment newInstance(SettingChangeListener settingChangeListener, int[] facing) {
        CameraFragment cameraFragment = new CameraFragment();
        cameraFragment.mSettingChangeListener = settingChangeListener;
        
        int f = 0, b = 0;
        for (int i = 0; i < facing.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (facing[i] == CameraInfo.CAMERA_FACING_FRONT) {
                map.put(FACING_ICON, (Integer) R.drawable.app_006_camera_facing_front);
                map.put(NUMBER, "Front #" + (++f));
            } else if (facing[i] == CameraInfo.CAMERA_FACING_BACK) {
                map.put(FACING_ICON, (Integer) R.drawable.app_006_camera_facing_back);
                map.put(NUMBER, "Back #" + (++b));
            } else {
                Log.w(TAG, "Camera Fragment was called with an invalid facing argument");
            }

            cameraFragment.mCameras.add(map);
        }

        return cameraFragment;
    }    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mCameraGridView = new GridView(getActivity());
        mCameraGridView.setNumColumns(mCameras.size());

        mAdapter = new SimpleAdapter(getActivity(), mCameras, R.layout.app_006_item_icon_description, new String[] {
                FACING_ICON,
                NUMBER }, new int[] { R.id.app_006_icon, R.id.app_006_iconDescription });

        mCameraGridView.setAdapter(mAdapter);
        mCameraGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSettingChangeListener.selectCamera(position);
            }
        });
        return mCameraGridView;
    }
}
