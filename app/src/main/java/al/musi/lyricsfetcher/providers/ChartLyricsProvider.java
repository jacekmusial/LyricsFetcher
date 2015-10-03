package al.musi.lyricsfetcher.providers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jlyr.util.Track;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;

import java.util.List;

import edu.gvsu.masl.asynchttp.HttpConnection;

public class ChartLyricsProvider extends LyricsProvider {
	
	public static final String TAG = "JLyrChartLyricsProvider";
	
	public ChartLyricsProvider(Track track) {
		super(track);
	}
	
	public String getSource() {
		return "ChartLyrics";
	}
	
	@Override
	public void loadLyrics(Handler _handler) {
		mHandler = _handler;
		
		String baseURL = "http://api.chartlyrics.com/apiv1.asmx/SearchLyricDirect?" +
					"artist=" + enc(mTrack.getArtist()) + "&" + 
					"song=" + enc(mTrack.getTitle());
		Handler handler = new Handler() {
			public void handleMessage(Message message) {
				switch (message.what) {
				case HttpConnection.DID_START: {
					Log.i(TAG, "Getting lyrics...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {
					String response = (String) message.obj;
					mLyrics = parse(response);
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
	
	private String parse(String response) {
		
		String eol = System.getProperty("line.separator");
		
		Document doc = Jsoup.parse(response, "", Parser.xmlParser());
		
		Element lyrics_el = doc.select("GetLyricResult > Lyric").first();
		String lyrics = null;
		if (lyrics_el == null) {
			Log.w(TAG, "No Lyrics tag.");
			doFail();
			return null;
		} else {
			List<Node> els = lyrics_el.childNodes();
			lyrics = "";
			for (Node node : els) {
				if (node instanceof TextNode) {
					lyrics += ((TextNode) node).getWholeText();
				} else {
					lyrics += eol;
				}
			}
		}
		
		Element name_el = doc.select("GetLyricResult > LyricSong").first();
		String title = "";
		if (name_el != null) {
			title = name_el.text();
		}
		
		Element artist_name_el = doc.select("GetLyricResult > LyricArtist").first();
		String artist = "";
		if (artist_name_el != null) {
			artist = artist_name_el.text();
		}
		
		if (!artist.equalsIgnoreCase(mTrack.getArtist()) || !title.equalsIgnoreCase(mTrack.getTitle())) {
			Log.w(TAG, "The title and/or artist do not match respectively");
		}

		doLoad();
		
		return "[ ChartLyrics - " + (artist==null? "NULL" : artist) + " - " + (title==null? "NULL" : title) + " ]" + eol + lyrics;
	}
}
