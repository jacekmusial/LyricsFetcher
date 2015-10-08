package al.musi.lyricsfetcher;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import edu.gvsu.masl.asynchttp.HttpConnection;

/**
 * Created by re on 2015-10-07.
 */
public class AZLyricsProvider extends Service {

    private String mTitle;
    private String mArtist;
    private String mLyrics;

    public static final String TAG = "AZLyricsProvider";

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //getActualContent();
        Log.d(TAG, "sending a broadcast");
        Intent intent1 = new Intent("lyricSearching");
        //mIntent.putExtra("message", mLyrics);
        intent1.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

        return super.onStartCommand(intent, flags, startId);
    }

    protected void getActualContent() {
        this.mTitle = mTitle.replaceAll(" ", "");
        this.mArtist = mArtist.replaceAll(" ", "");
        String url = "http://www.azlyrics.com//lyrics/";

        url += mArtist.replaceAll("\\W", "") + "/";
        url += mTitle.replaceAll("\\W", "") + ".html";

        Toast.makeText(getBaseContext(), url, Toast.LENGTH_LONG).show();
        Log.d(TAG, "url: " + url);

        final String baseURL = url;
        Handler handler = new Handler(new Handler.Callback()  {
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case HttpConnection.DID_START: {
                        Log.i(TAG, "Getting lyrics...");
                        return true;
                    }
                    case HttpConnection.DID_SUCCEED: {
                        String response = (String) message.obj;
                        mLyrics = parse(response, baseURL);
                        return true;
                    }
                    case HttpConnection.DID_ERROR: {
                        Exception e = (Exception) message.obj;
                        // TODO: try e.toString() maybe it gives more detail about the error
                        // Otherwise find a way to use printStackTrace()
                        Log.e(TAG, "Error: " + e.getMessage());
                        mLyrics = null;
                        return false;
                    }
                    default: return false;
                }
            }
        });
        //new HttpConnection(handler).get(baseURL);
        Log.v(TAG, "Fetching url: " + baseURL);
    }

    private String parse(String response, String url) {
        //..its just works
        String onlyLyrics =response.substring(
                response.indexOf("Sorry about that. -->")+22,
                response.length());

        onlyLyrics = onlyLyrics.substring(0, onlyLyrics.indexOf("</div>"));
        onlyLyrics = onlyLyrics.replaceAll("\"", "").replaceAll("<br>", "\n");

        return onlyLyrics;
    }
}
