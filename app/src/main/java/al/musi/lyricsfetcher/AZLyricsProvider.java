package al.musi.lyricsfetcher;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

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
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mTitle = (String) extras.getString("title");
            mArtist = (String) extras.getString("artist");
        }

        getActualContent();

        Log.d(TAG, "sending a broadcast");
        Intent intent1 = new Intent("lyricSearching");
        intent1.putExtra("message", mLyrics);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

        return super.onStartCommand(intent, flags, startId);
    }

    protected void getActualContent() {
        if (mTitle.length() < 0 || mArtist.length() < 0) { return; }

        String title = mTitle.replaceAll(" ", "").replaceAll("\\W", "");
        String artist= mArtist.replaceAll(" ", "").replaceAll("\\W", "");

        Toast.makeText(AZLyricsProvider.this, artist+" "+title, Toast.LENGTH_LONG).show();

        String u = "http://www.azlyrics.com/lyrics/";
        String url = u + artist + title;

        //Toast.makeText(getBaseContext(), url, Toast.LENGTH_LONG).show();
        //Log.d(TAG, "url: " + url);

       /*final String baseURL = url;
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
        Log.v(TAG, "Fetching url: " + baseURL);*/
        //new HttpConnection(handler).get(baseURL);

    }

    private String parse(String response, String url) {
        //..its just works
        String onlyLyrics = response.substring(
                response.indexOf("Sorry about that. -->")+22,
                response.length());

        String a = onlyLyrics.substring(0, onlyLyrics.indexOf("</div>"));
        String b = a.replaceAll("\"", "").replaceAll("<br>", "\n");

        return b;
    }
}
