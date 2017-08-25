package urmc.drinkingapp.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Shengqi Suizhu on 2017/8/2.
 * Class for methods that might be used any where in the project
 */

// TODO: 2017/8/2  migrate reusable methods to here
public class Utils {

    //method to fix pictures to be displayed in the app
    public static Bitmap getScaledBitmap(String path, int width, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int sampleSize = 1;
        if(srcHeight > height || srcWidth > width ) {
            if(srcWidth > srcHeight) {
                sampleSize = Math.round(srcHeight / height);
            } else {
                sampleSize = Math.round(srcWidth / width);
            }
        }
        BitmapFactory.Options scaledOptions = new BitmapFactory.Options(); scaledOptions.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile(path, scaledOptions);
    }
}
