package tools;

import android.content.Context;
import android.util.Log;

/**
 * Created by WilsonHuang on 2016/2/13.
 */
public class MyTools {

    public static void myLog(String msg) {
        Log.i("Wilson", msg);

    }

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
