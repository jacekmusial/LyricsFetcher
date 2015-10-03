package al.musi.lyricsfetcher;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by re on 2015-09-28.
 */
public class WeakRefHandler extends Handler {
    private final WeakReference<Object> mActivity;

    public WeakRefHandler(Object activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        Handler h = new Handler();
    }

}