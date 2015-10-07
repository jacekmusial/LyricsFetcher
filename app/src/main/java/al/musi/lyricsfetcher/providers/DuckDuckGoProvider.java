package al.musi.lyricsfetcher.providers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import al.musi.lyricsfetcher.util.Track;
import edu.gvsu.masl.asynchttp.HttpConnection;

public abstract  class DuckDuckGoProvider extends LyricsProvider {

    public DuckDuckGoProvider(Track track) {
        super(track);
    }

    public static final String TAG = "JLyrDuckDuckGoProvider";

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
                                getActualContent(url);
                            } else {
                                Log.w(TAG, "DuckDuckGo got a wrong link: " + url);
                                doFail();
                            }
                        }
                        break;
                    }
                    case HttpConnection.DID_ERROR: {
                        Exception e = (Exception) message.obj;
                        // TODO: try e.toString() maybe it gives more detail about the error
                        // Otherwise find a way to use printStackTrace()
                        Log.e(TAG, "Error: " + e.toString());

                        mLyrics = null;

                        doError();
                        break;
                    }
                }
            }
        };
        new HttpConnection(handler).get(baseURL);
        Log.v(TAG, "Fetching url: " + baseURL);
    }

    abstract protected void getActualContent(String url);
}
