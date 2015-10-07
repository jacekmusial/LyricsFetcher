package al.musi.lyricsfetcher.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;

public class TrackBrowser implements Runnable {

	public static final int DID_START = 0;
	public static final int DID_ERROR = 1;
	public static final int DID_SUCCEED = 2;
	public static final int ADD = 3;
	public static final int DID_INTERRUPT = 4;

	private Handler handler;
	boolean isRunning = false;
	
	public static final String TAG = "JLyrTrackBrowser";

	public class TrackView {
		private Track mTrack = null;
		
		public TrackView(Track track) {
			mTrack = track;
		}
		
		public Track getTrack() {
			return mTrack;
		}
		
		public String toString() {
			if (mTrack == null) {
				return "Track is null!";
			} else {
				return mTrack.toString();
			}
		}
	};
	
	public TrackBrowser() {
		this(new Handler());
	}

	public TrackBrowser(Handler _handler) {
		handler = _handler;
	}

	public void run() {
		
		isRunning = true;
		
		handler.sendMessage(Message.obtain(handler, TrackBrowser.DID_START));
		try {
			File dir = LyricReader.getLyricsDirectory();
	        if (!dir.exists()) {
	        	dir.mkdirs();
	        }
	        File[] file_list = dir.listFiles();
	        
	        Log.i(TAG, "Number of files found: " + file_list.length);
	        
	        int i = 0;
	        while (isRunning && i < file_list.length) {
	        	LyricReader reader = new LyricReader(file_list[i]);
	        	Track track = reader.getTrack();
	        	TrackView tv = new TrackView(track);
	        	handler.sendMessage(Message.obtain(handler, TrackBrowser.ADD, tv));
	        	
	        	i++;
	        }
	        
	        if (isRunning) {
	        	handler.sendMessage(Message.obtain(handler, TrackBrowser.DID_SUCCEED));
	        } else {
	        	handler.sendMessage(Message.obtain(handler, TrackBrowser.DID_INTERRUPT));
	        }
		} catch (Exception e) {
			handler.sendMessage(Message.obtain(handler, TrackBrowser.DID_ERROR, e));
		}
		
		isRunning = false;
	}
	
	public void stop() {
		isRunning = false;
	}
 }