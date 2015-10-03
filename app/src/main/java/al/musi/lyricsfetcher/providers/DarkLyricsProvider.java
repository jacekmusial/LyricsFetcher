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

import edu.gvsu.masl.asynchttp.HttpConnection;

public class DarkLyricsProvider extends LyricsProvider {
	
	public static final String TAG = "JLyrDarkLyricsProvider";
	
	public DarkLyricsProvider(Track track) {
		super(track);
	}
	
	public String getSource() {
		return "DarkLyrics";
	}
	
	@Override
	public void loadLyrics(Handler _handler) {
		mHandler = _handler;
		
		String artist = mTrack.getArtist().toLowerCase();
		artist = artist.replaceAll("[^a-zA-Z]", "");
		String first_letter = String.valueOf(artist.charAt(0));

		final String baseURL = "http://www.darklyrics.com/" + enc(first_letter) + "/" + enc(artist) + ".html";
		Handler handler = new Handler() {
			public void handleMessage(Message message) {
				switch (message.what) {
				case HttpConnection.DID_START: {
					Log.i(TAG, "Getting artist link...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {
					
					String title = mTrack.getTitle();
					title = title.replaceAll("\\(.*\\)", "");
					
					String response = (String) message.obj;
					Document doc = Parser.parse(response, baseURL);
					
					Element anchor = doc.select("div.cont div.album a:contains(" + title + ")").first();
					if (anchor == null) {
						Log.w(TAG, "Did not find the track anchor");
						doFail();
						break;
					}
					
					String url = anchor.absUrl("href");
					if (url.startsWith("http://www.darklyrics.com/lyrics/")) {
						getActualContent(url);
					} else {
						Log.w(TAG, "We got a wrong link: " + url);
						doFail();
						break;
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
	
	private void getActualContent(String url) {
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
			title = title.split("-")[0];
			title = title.replace(" LYRICS", "");
			title = title.trim();
		}
		
		String anchor_id = url.substring(url.indexOf("#")+1);
		
		Element anchor = doc.select("div.cntwrap div.cont div.lyrics h3 a[name=" + anchor_id + "]").first();
		if (anchor == null) {
			Log.e(TAG, "No lyrics header");
			doFail();
			return null;
		}
		
		Element h3 = anchor.parent();
		if (h3 == null) {
			Log.e(TAG, "No lyrics header");
			doFail();
			return null;
		}

		String eol = System.getProperty("line.separator");
		String lyrics = "";
		Node node = h3.nextSibling();
		while (node != null) {
			node = node.nextSibling();
			if (node instanceof TextNode) {
				lyrics += ((TextNode) node).text();
			} else {
				Element el = (Element) node;
				if (el == null) {
					break;
				}
				String tag = el.tagName();
				if (tag == "h3") {
					break;
				} else if (tag == "br") {
					lyrics += eol;
				}
			}
		}

		doLoad();
		return "[ DarkLyrics - " + (title==null? "NULL":title) + " ]" + eol + lyrics;
	}
}
