package al.musi.lyricsfetcher;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import al.musi.lyricsfetcher.util.Lyrics;
import edu.gvsu.masl.asynchttp.HttpConnection;

public class LyricsSearch extends IntentService {

    private String mTitle;
    private String mArtist;
    private String mLyrics;
    private Button mBtn;
    private Handler mHandler;

    private String mQuery;

    public static final String TAG = "JLyrSearch";

    public LyricsSearch() { super("LyricSearch"); }

    public LyricsSearch(String mArtist, String mTitle, String mQuery) {
        super("LyricSearch");
        this.mArtist = mArtist;
        this.mTitle = mTitle;
        this.mQuery = mQuery;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String str = Resources.getSystem().getString(R.string.service_start);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        search(mQuery)
    }

    public void search(final String query, final String expected_url) {
        final String baseURL = "http://www.duckduckgo.com/?q=" + enc(query);
        Handler handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case HttpConnection.DID_START: {
                        Log.i(TAG, "Getting lyrics link...");
                        break;
                    }
                    case HttpConnection.DID_SUCCEED: {
                        String response = (String) message.obj;
                        Document doc = Parser.parse(response, baseURL);
                        Element redirect_meta = doc.select("meta[http-equiv=refresh]").first();
                        if (redirect_meta == null) {
                            Log.w(TAG, "DuckDuckGo did not find a link.");
                            doFail();
                        } else {
                            String content = redirect_meta.attr("content");
                            String url = content.substring(content.indexOf("uddg=")+5);
                            try {
                                url = URLDecoder.decode(url, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                Log.e(TAG, "URLEncoder lacks support for UTF-8!?");
                                doFail();
                                break;
                            }
                            if (url.startsWith(expected_url)) {
                                mLyrics = parse(response, baseURL);
                            } else {
                                Log.w(TAG, "DuckDuckGo got a wrong link: " + url);
                                doFail();
                            }
                        }
                        break;
                    }
                    case HttpConnection.DID_ERROR: {
                        Exception e = (Exception) message.obj;
                        Log.e(TAG, "Error: " + e.toString());
                        mLyrics = null;
                        Toast.makeText(getBaseContext(), "can't download", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };
        new HttpConnection(handler).get(baseURL);
        Log.v(TAG, "Fetching url: " + baseURL);
    }

    protected static String enc(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "URLEncoder lacks support for UTF-8!?");
            return null;
        }
    }

    protected void doError() {
        Message msg = Message.obtain(mHandler, Lyrics.DID_ERROR);
        mHandler.sendMessage(msg);
    }

    protected void doFail() {
        Message msg = Message.obtain(mHandler, Lyrics.DID_FAIL);
        mHandler.sendMessage(msg);
    }

    protected void doLoad() {
        Message msg = Message.obtain(mHandler, Lyrics.DID_LOAD);
        mHandler.sendMessage(msg);
    }

    private String parse(String response, String url) {
        Document doc = Parser.parse(response, url);

        Element title_el = doc.select("html > head > title").first();
        String title = null;
        if (title_el == null) {
            Log.w(TAG, "No title tag");
        } else {
            title = title_el.text();
            title = title.replace(" LYRICS", "");
        }

        Element p = doc.select("p#songLyricsDiv").first();
        if (p == null) {
            Log.e(TAG, "No lyrics paragraph");
            doFail();
            return null;
        }

        String eol = System.getProperty("line.separator");
        List<Node> els = p.childNodes();
        String lyrics = "";
        for (Node node : els) {
            if (node instanceof TextNode) {
                lyrics += ((TextNode) node).text();
            } else {
                lyrics += eol;
            }
        }

        doLoad();
        return "[ SongLyrics - " + (title==null? "NULL":title) + " ]" + eol + lyrics;
    }
}