package com.hulzenga.ioi_apps.app_006;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.hulzenga.ioi_apps.R;

public class CameraFragment extends Fragment {

    private static final String        TAG           = "CameraFragment";

    private GridView                   mCameraGridView;
    private ListAdapter                mAdapter;
    private LinearLayout               cameraSelectLayout;
    private List<Map<String, Integer>> mCameras      = new ArrayList<Map<String, Integer>>();

    private static final String        FACING        = "FACING";
    private static final String        CAMERA_NUMBER = "CAMERA_NUMBER";

    
    
    public static CameraFragment newInstance(int[] facing) {
        CameraFragment cameraFragment = new CameraFragment();
        
        Map<String, Integer> map = new HashMap<String, Integer>();
        
        for (int i = 0; i < facing.length; i++) {            
            if (facing[i] == CameraInfo.CAMERA_FACING_FRONT) {
                map.put(FACING, R.drawable.app_006_camera_facing_front_);
            } else if (facing[i] == CameraInfo.CAMERA_FACING_FRONT) {
                map.put(FACING, R.drawable.app_006_camera_facing_back);
            } else {
                Log.w(TAG, "Camera Fragment was called with an invalid facing argument");
            }
            map.put(CAMERA_NUMBER, i);
            cameraFragment.mCameras.add(map);
        }
        
        return cameraFragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mCameraGridView = new GridView(getActivity());
        mCameraGridView.setNumColumns(mCameras.size());
        mAdapter = new SimpleAdapter(getActivity(), mCameras, R.layout.app_006_item_camera, new String[] { FACING,
                CAMERA_NUMBER }, new int[] { R.id.app_006_cameraFacingIcon, R.id.app_006_cameraNumber });

        mCameraGridView.setAdapter(mAdapter);

        return mCameraGridView;
    }

}
