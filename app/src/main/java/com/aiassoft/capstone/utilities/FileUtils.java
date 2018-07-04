package com.aiassoft.capstone.utilities;

import android.content.Context;

import com.aiassoft.capstone.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gvryn on 04/07/18.
 */

public class FileUtils {

    private FileUtils() {
        throw new AssertionError(R.string.no_instances_for_you);
    }


    public static File createTempFile(Context context, String prefix, String suffix) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = prefix + timeStamp + suffix;

        return new File(context.getCacheDir(), fileName);
    }

    private File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "ve_" + timeStamp + ".jpg";
        File image = new File(context.getCacheDir(), imageFileName);
        //File storageDir = new File(context.getExternalCacheDir(), "Camera");
        //storageDir.getParentFile().mkdirs();
        //File image = File.createTempFile(
        //        imageFileName,  /* prefix */
        //        ".jpg",         /* suffix */
        //        storageDir      /* directory */
        //);

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}
