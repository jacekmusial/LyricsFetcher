package al.musi.lyricsfetcher.providers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jlyr.util.Lyrics;
import com.jlyr.util.Track;

public class DummyProvider extends LyricsProvider {
	
	public static final String TAG = "JLyrDummyProvider";
	
	public DummyProvider(Track track) {
		super(track);
	}
	
	public String getSource() {
		return "Dummy";
	}
	
	@Override
	public void loadLyrics(Handler _handler) {
		mHandler = _handler;
		
		Log.i(TAG, "Dummy will fail now!");
		
		mLyrics = null;
		Message message = Message.obtain(mHandler, Lyrics.DID_FAIL);
		mHandler.sendMessage(message);
	}
}
