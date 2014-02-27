package com.hulzenga.ioi_apps.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class FileManager {

    private static final String TAG                 = "FILE_MANAGER";

    /**
     * Public Storage directory of all media created with this app 
     */
    public static final String  APP_MEDIA_DIRECTORY = "101AndroidApps";

    /**
     * Image media type, file named according to: IMG_yyyyMMdd_HHmmss.jpg
     */
    public static final int     MEDIA_TYPE_IMAGE    = 1;
    
    /**
     * Video media type, file named according to: VID_yyyyMMdd_HHmmss.mp4
     */
    public static final int     MEDIA_TYPE_VIDEO    = 2;

    /**
     * creates a new media output file and returns a handle to that file 
     * @param type the type of media file, this must be either {@link #MEDIA_TYPE_IMAGE} or {@link #MEDIA_TYPE_VIDEO}
     * @return
     */
    public static File GetOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                APP_MEDIA_DIRECTORY);

        // create storage diractory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "Failed to create media storage directory");
                return null;
            }
        }
                
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outputMediaFile;
        
        switch(type) {
        case MEDIA_TYPE_IMAGE:
            outputMediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            break;
        case MEDIA_TYPE_VIDEO:
            outputMediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            break;
        default:
            Log.e(TAG, "invalid media type requested");
            return null;            
        }
        
        return outputMediaFile;
    }
}
