package al.musi.lyricsfetcher.providers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jlyr.util.Track;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import edu.gvsu.masl.asynchttp.HttpConnection;

public class JamendoProvider extends LyricsProvider {
	
	public static final String TAG = "JLyrJamendoProvider";
	
	public JamendoProvider(Track track) {
		super(track);
	}
	
	public String getSource() {
		return "Jamendo";
	}
	
	@Override
	public void loadLyrics(Handler _handler) {
		mHandler = _handler;

		/* For XML 
		String url = "http://api.jamendo.com" +
					"/get2/id+name+artist_name+text/track/xml/artist_album+album_track/" +
					"?searchquery=" + enc(mTrack.getArtist()+" "+mTrack.getTitle()) +
					"&n=1&order=searchweight_desc";
		*/
		String url = "http://api.jamendo.com" +
				"/get2/text/track/plain/artist_album+album_track/" +
				"?searchquery=" + enc(mTrack.getArtist()+" "+mTrack.getTitle()) +
				"&n=1&order=searchweight_desc";
		
		Handler handler = new Handler() {
			public void handleMessage(Message message) {
				switch (message.what) {
				case HttpConnection.DID_START: {
					Log.i(TAG, "Getting lyrics...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {
					String response = (String) message.obj;
					
					response = response.trim();
					if (response.equals("")) {
						mLyrics = null;
						doFail();
						break;
					}
					
					mLyrics = response;
					doLoad();
					// If the return type were to be xml, then:
					//mLyrics = parse(response);
					break;
				}
				case HttpConnection.DID_ERROR: {
					Exception e = (Exception) message.obj;
					// TODO: try e.toString() maybe it gives more detail about the error
					// Otherwise find a way to use printStackTrace()
					Log.e(TAG, "Error: " + e.getMessage());
					
					doError();
					break;
				}
				}
			}
		};
		new HttpConnection(handler).get(url);
		Log.v(TAG, "Fetching: " + url);
	}
	
	private String parse(String response) {
		Document doc = Jsoup.parse(response, "", Parser.xmlParser());
		
		Element id_el = doc.select("data > track > id").first();
		if (id_el == null) {
			Log.w(TAG, "No ID tag.");
			return null;
		}
		
		Element name_el = doc.select("data > track > name").first();
		String title = "";
		if (name_el != null) {
			title = name_el.text();
		}
		
		Element artist_name_el = doc.select("data > track > artist_name").first();
		String artist = "";
		if (artist_name_el != null) {
			artist = artist_name_el.text();
		}
		
		if (!artist.equalsIgnoreCase(mTrack.getArtist()) || !title.equalsIgnoreCase(mTrack.getTitle())) {
			Log.w(TAG, "The title and/or artist do not match respectively");
		}
		
		Element text_el = doc.select("data > track > text").first();
		String lyrics = null;
		if (text_el != null) {
			lyrics = text_el.text();
		}
		
		String eol = System.getProperty("line.separator");
		
		doLoad();
		return "[ Jamendo - " + title + " - " + artist + " ]" + eol + lyrics;
	}
}
