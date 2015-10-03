package al.musi.lyricsfetcher.providers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jlyr.util.Track;

import edu.gvsu.masl.asynchttp.HttpConnection;

public class LyrDbProvider extends LyricsProvider {
	
	public static final String TAG = "JLyrLyrDBProvider";
	
	public LyrDbProvider(Track track) {
		super(track);
	}
	
	public String getSource() {
		return "LyrDB";
	}
	
	@Override
	public void loadLyrics(Handler _handler) {
		mHandler = _handler;
		
		String firstUrl = "http://webservices.lyrdb.com/lookup.php?q=" +
					enc(mTrack.getArtist() + "|" + mTrack.getTitle()) + 
					"&for=match&agent=llyrics";
		
		Handler handler = new Handler() {
			public void handleMessage(Message message) {
				switch (message.what) {
				case HttpConnection.DID_START: {
					Log.i(TAG, "Getting lyrics id...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {
					String response = (String) message.obj;
					int end = response.indexOf("\\");
					if (end == -1) {
						Log.i(TAG, "LyrDB id not found in: " + response);
						mLyrics = null;
						
						doFail();
						return;
					}
					
					String lyricsid = response.substring(0, end);
					Log.i(TAG, "LyrDB id is: " + lyricsid);
					getSecondUrl(lyricsid);
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
		new HttpConnection(handler).get(firstUrl);
		Log.v(TAG, "Fetching first url: " + firstUrl);
	}
	
	private void getSecondUrl(String lyricsid) {
		String secondUrl = "http://www.lyrdb.com/getlyr.php?q=" + enc(lyricsid);
		
		Handler handler = new Handler() {
			public void handleMessage(Message message) {
				switch (message.what) {
				case HttpConnection.DID_START: {
					Log.i(TAG, "Getting lyrics...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {
					String response = (String) message.obj;
					mLyrics = response;
					
					doLoad();
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
		new HttpConnection(handler).get(secondUrl);
		Log.v(TAG, "Fetching second url: " + secondUrl);
	}
}
