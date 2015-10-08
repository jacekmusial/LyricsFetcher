package al.musi.lyricsfetcher;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

        String artist= mArtist.replaceAll(" ", "").replaceAll("\\W", "") + "/";
        String title = mTitle.replaceAll(" ", "").replaceAll("\\W", "") + ".html";

        String u = "http://www.azlyrics.com/lyrics/" + artist + title;
        Log.d(TAG, "url: " + u);

        final String baseURL = u;
        try {
            URL url = new URL(baseURL);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "??!?!");
            try {
                InputStream in = new BufferedInputStream(httpConnection.getInputStream());
                //InputStream in = address.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                Log.d(TAG, "@@@");
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Log.d(TAG, result.toString());
                mLyrics = parse(result.toString(), baseURL);
            } finally {
                httpConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Fetching url: " + baseURL);
    }

    private String parse(String response, String url) {
        //..its just works
        String onlyLyrics = response.substring(
                response.indexOf("Sorry about that. -->")+22,
                response.length()-1);
        onlyLyrics = onlyLyrics.substring(0, onlyLyrics.indexOf("</div>"));
        onlyLyrics = onlyLyrics.replaceAll("\"", "").replaceAll("<br>", "\n");
        return onlyLyrics;
    }
}
