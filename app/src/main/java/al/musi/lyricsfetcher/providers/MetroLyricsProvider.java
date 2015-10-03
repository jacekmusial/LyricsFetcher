package al.musi.lyricsfetcher.providers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import edu.gvsu.masl.asynchttp.HttpConnection;

import util.Track;

public class MetroLyricsProvider extends LyricsProvider {
	
	public static final String TAG = "JLyrMetroLyricsProvider";
	
	public MetroLyricsProvider(Track track) {
		super(track);
	}
	
	public String getSource() {
		return "MetroLyrics";
	}
	
	@Override
	public void loadLyrics(Handler _handler) {
		mHandler = _handler;
		
		String punctuation = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
		String clean_artist = mTrack.getArtist();
		String clean_title = mTrack.getTitle();
		for (int i=0; i<punctuation.length(); i++) {
			char c = punctuation.charAt(i);
			clean_title = clean_title.replace(String.valueOf(c), "");
			clean_artist = clean_artist.replace(String.valueOf(c), "");
		}
		clean_title = clean_title.replace(" ", "-");
		clean_artist = clean_artist.replace(" ", "-");
		
		final String baseURL = "http://www.metrolyrics.com/" + enc(clean_title) + "-lyrics-" + enc(clean_artist) + ".html";
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
			doFail();
            return null;
		} else {
			title = title_el.text();
			int end = title.indexOf(" LYRICS");
			if (end == -1) {
				Log.w(TAG, "Invalid title tag: " + title);
				doFail();
	            return null;
			} else {
				title = title.substring(0, end);
			}
		}
		
		Elements els = doc.select("div#lyrics-body > p > span, div#lyrics-body > p > br");

		String eol = System.getProperty("line.separator");
		String lyrics = "";
		for (Element el : els) {
			String tag = el.tagName();
			if (tag == "br") {
				lyrics += eol;
			} else if (tag == "span") {
				if (el.children().size() == 0) {
					lyrics += el.text() + eol;
				}
			}
		}

		doLoad();
		return "[ MetroLyrics - " + (title==null? "NULL":title) + " ]" + eol + lyrics;
	}
}
