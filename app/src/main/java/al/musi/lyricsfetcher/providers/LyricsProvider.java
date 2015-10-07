package al.musi.lyricsfetcher.providers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import al.musi.lyricsfetcher.util.Lyrics;
import al.musi.lyricsfetcher.util.Track;

public abstract class LyricsProvider {

	protected Track mTrack = null;
	protected String mLyrics = null;
	protected Handler mHandler = null;

	public static final String TAG = "JLyrProvider";

	public LyricsProvider(Track track) {
		mTrack = track;
	}

	public abstract String getSource();

	public String getLyrics() {
		return mLyrics;
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

	public abstract void loadLyrics(Handler _handler);

	protected static String enc(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "URLEncoder lacks support for UTF-8!?");
			return null;
		}
	}
}
