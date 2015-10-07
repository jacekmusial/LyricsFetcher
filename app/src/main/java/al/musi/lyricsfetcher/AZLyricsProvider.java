package al.musi.lyricsfetcher;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.List;

import edu.gvsu.masl.asynchttp.HttpConnection;

/**
 * Created by re on 2015-10-07.
 */
public class AZLyricsProvider extends Service {

    private String mTitle;
    private String mArtist;
    private String mLyrics;

    public static final String TAG = "AZLyricsProvider";

    public AZLyricsProvider() {
        this.mTitle = findViewById(R.id.editTextArtist);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.mTitle = mTitle.replaceAll(" ", "");
        this.mArtist = mArtist.replaceAll(" ", "");
        String url = "http://www.azlyrics.com//lyrics/";

        url += mArtist.replaceAll("\\W", "") + "/";
        url += mTitle.replaceAll("\\W", "") + ".html";

        Toast.makeText(getBaseContext(), url, Toast.LENGTH_LONG).show();

    }

    protected void getActualContent(String url) {
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

                        doError();
                        return false;
                    }
                    default: return false;
                }
            }
        });
        new HttpConnection(handler).get(baseURL);
        Log.v(TAG, "Fetching url: " + baseURL);
    }

    private String parse(String response, String url) {
        Document doc = Parser.parse(response, url);

        Element title_el = doc.select("html > head > title").first();
        String title = null;
        if (title_el == null) {
            Log.w(TAG, "No title tag");
        } else {
            title = title_el.text();
            title = title.replace(" LYRICS - ", " - ");
        }

        Elements divs = doc.select("div#main div");
        if (divs.size()<4) {
            Log.e(TAG, "No body div");
            doFail();
            return null;
        }
        Element body = divs.get(3);

        if (body == null) {
            Log.e(TAG, "No lyrics div tag");
            doFail();
            return null;
        }

        String eol = System.getProperty("line.separator");
        List<Node> els = body.childNodes();
        String lyrics = "";
        for (Node node : els) {
            if (node instanceof TextNode) {
                lyrics += ((TextNode) node).text();
            } else {
                lyrics += eol;
            }
        }

        doLoad();
        return "[ AZLyrics - " + (title==null? "NULL":title) + " ]" + eol + lyrics;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
