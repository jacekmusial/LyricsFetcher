package al.musi.lyricsfetcher;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by re on 2015-09-28.
 */
public class WeakRefHandler extends Handler {
    private static WeakReference<Object> mActivity;
    private String query;
    private String expected_url;

    public WeakRefHandler(Object activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        Handler h = new Handler();
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return this.query;
    }

    public void setExpectedUrl(String expected_url) {
        this.expected_url = expected_url;
    }

    public String getExpectedUrl() {
        return this.expected_url;
    }
}