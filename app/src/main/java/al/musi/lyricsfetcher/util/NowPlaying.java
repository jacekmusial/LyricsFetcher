package al.musi.lyricsfetcher.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jlyr.WeakRefHandler;

import java.util.LinkedList;

public class NowPlaying {
	
	private static Track mTrack = null;
	private static LinkedList<Item> stack = new LinkedList<Item>();
	
	private static Handler mHandler = null;
	
	private static final String TAG = "JLyrNowPlaying";
	
	public static class Item {
		private Track mTrack;
		private Track.State mState;
		
		public Item(Track track, Track.State state) {
			mTrack = track;
			mState = state;
		}
		
		public Track getTrack() {
			return mTrack;
		}
		
		public Track.State getState() {
			return mState;
		}
	}
	
	public synchronized void addItem(Track track, Track.State state) {
		boolean is_playing = isNowPlaying(track, state);
		Item item = new Item(track, state);
		stack.addLast(item);
		if (is_playing) {
			setNowPlaying(track);
		} else {
			clearNowPlaying();
		}
	}
	
	private boolean isNowPlaying(Track track, Track.State state) {
		// Ignore null tracks
		if (track == null) {
			Log.e(TAG, "A null track got through!! (Ignoring it)");
			return false;
		}
		
		// same as current tracks
		if (track.equals(Track.SAME_AS_CURRENT)) {
			// this only happens for apps implementing Scrobble Droid's API
			Log.d(TAG, "Got a SAME_AS_CURRENT track");
			if (mTrack == null) {
				Log.e(TAG, "Got a SAME_AS_CURRENT track, but current was null!");
				return false;
			} else {
				track = mTrack;
			}
		}
		
		if (state == Track.State.START || state == Track.State.RESUME) { // start/resume
			return true;
		} else if (state == Track.State.PAUSE) { // pause
			if (mTrack != null && !track.equals(mTrack)) {
				Log.e(TAG, "paused track: " + track + " != current " + mTrack);
			}
			return false;
		} else if (state == Track.State.COMPLETE) { // "complete"
			if (mTrack != null && !track.equals(mTrack)) {
				Log.e(TAG, "completed track: " + track + " != current " + mTrack);
			}
			return false;
		} else if (state == Track.State.PLAYLIST_FINISHED) { // playlist end
			if (mTrack != null && !track.equals(mTrack)) {
				Log.e(TAG, "playlist finished track: " + track + " != current " + mTrack);
			}
			return false;
		} else if (state == Track.State.UNKNOWN_NONPLAYING) {
			return false;
		} else {
			Log.e(TAG, "Unknown track state: " + state.toString());
			return false;
		}
	}
	
	public static void setHandler(WeakRefHandler handler) {
		mHandler = handler;
	}
	
	private void setNowPlaying(Track track) {
		Log.i(TAG, "Setting track: " + track);
		mTrack = track;
		if (mHandler != null) {
			Message msg = Message.obtain(mHandler, 0);
			mHandler.sendMessage(msg);
		}
	}
	
	private void clearNowPlaying() {
		Log.i(TAG, "Clearing track");
		mTrack = null;
		if (mHandler != null) {
			Message msg = Message.obtain(mHandler, 0);
			mHandler.sendMessage(msg);
		}
	}
	
	public synchronized Track getTrack() {
		return mTrack;
	}
}
