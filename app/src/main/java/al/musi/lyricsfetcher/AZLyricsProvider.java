package al.musi.lyricsfetcher;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
            mTitle = extras.getString("title");
            mArtist = extras.getString("artist");

            mLyrics = getActualContent();

            Log.v(TAG, "response: " + mLyrics);

            Intent intent1 = new Intent("lyricSearching");
            intent1.putExtra("message", mLyrics);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    protected String getActualContent() {
        if (mTitle.length() < 0 || mArtist.length() < 0) { return null; }

        /**
        * remove whitespaces, and all NON-words from artist&title
         * to look like this:
         * http://www.azlyrics.com/lyrics/metallica/entersandman.html
         *                               ^artist^   ^title^
        */
        String artist= mArtist.replaceAll(" ", "").replaceAll("\\W", "") + "/";
        String title = mTitle.replaceAll(" ", "").replaceAll("\\W", "") + ".html";

        String u = "http://www.azlyrics.com/lyrics/" + artist + title;
        final String url = u;

        Log.d(TAG, "url: " + u);

        String response = null;
        HttpConnection httpConnection = new HttpConnection(url);

        try {
            response = httpConnection.getUrl();
            Log.v(TAG, "Fetching url: " + u);
            response = parse(response);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return response != null ? response : "" ;
    }

    private String parse(String response) {
        /**
         * lyrics on azlyrics look like that:
         * (e.g http://www.azlyrics.com/lyrics/hopsin/illmindofhopsin7.html)
         * <div>
         *     <!-- Usage of azlyrics.com content by any third-party lyrics provider
         *     is prohibited by our licensing agreement. Sorry about that. -->
         *     (OPTIONAL) <i>[intro:outro:something]</i>
         *     <br>
         *     "
         *     line of lyric from song"
         *     <br>
         *     "
         *     line of lyric from song"
         *     ...
         *     (OPTIONAL) <i> [intro:outro:something]</i>
         * </div>
         *
         */
        int startPosOfLyrics = response.indexOf("Sorry about that. -->")+21;
        if (startPosOfLyrics <= 0) {
            throw  new UnsupportedOperationException();
        }
        String lyricsAndCrap = response.substring(startPosOfLyrics, response.length());
        int endPosOfLyrics = lyricsAndCrap.indexOf("</div>");

        Log.v(TAG, "startPosOfLyrics: " + startPosOfLyrics + ", endPosOfLyrics: " + endPosOfLyrics);

        String onlyLyrics = lyricsAndCrap.substring(0, endPosOfLyrics);
        //if (onlyLyrics.indexOf("<i>") != 0 ) {
        onlyLyrics = onlyLyrics.replaceAll("<i>", "").replaceAll("</i>", "");
        //}
        onlyLyrics = onlyLyrics.replaceAll("\"", "").replaceAll(
                "<br>", System.getProperty("line.separator"));
        //onlyLyrics = onlyLyrics.substring(0, onlyLyrics.indexOf("</div>"));
        Log.v(TAG, onlyLyrics);

        return onlyLyrics;
    }
}