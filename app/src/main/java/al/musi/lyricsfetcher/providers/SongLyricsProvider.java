package al.musi.lyricsfetcher.providers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jlyr.util.Track;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;

import java.util.List;

import edu.gvsu.masl.asynchttp.HttpConnection;

public class SongLyricsProvider extends DuckDuckGoProvider {
	
	public static final String TAG = "JLyrSongLyricsProvider";
	
	public SongLyricsProvider(Track track) {
		super(track);
	}
	
	public String getSource() {
		return "SongLyrics";
	}
	
	@Override
	public void loadLyrics(Handler _handler) {
		mHandler = _handler;
		
		// TODO: we are relying on DuckDuckGo, we shouldn't:
		// AZLyrics removes spaces: /lyrics/jamesblunt/staythenight.html
		// and maybe punctuation also. See ILyrics they do it in Ruby, but I didn't get it right yet.
		String search_query = "! site:songlyrics.com " + mTrack.getArtist() + " " + mTrack.getTitle();
		search(search_query, "http://www.songlyrics.com/");
	}
	
	protected void getActualContent(String url) {
		final String baseURL = url;
		Handler handler = new Handler() {
			public void handleMessage(Message message) {
				switch (message.what) {
				case HttpConnection.DID_START: {
					Log.i(TAG, "Getting lyrics...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {
					String response = (String) message.obj;
					mLyrics = parse(response, baseURL);
					break;
				}
				case HttpConnection.DID_ERROR: {
					Exception e = (Exception) message.obj;
					// TODO: try e.toString() maybe it gives more detail about the error
					// Otherwise find a way to use printStackTrace()
					Log.e(TAG, "Error: " + e.getMessage());
					
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
