package al.musi.lyricsfetcher.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LyricsWebSearch {
	
	Track mTrack;
	String mSearchEngine;
	Context mContext;
	
	protected String mSearchDomain = "http://www.duckduckgo.com/?q=";
	protected String mSearchQuery = "lyrics";
	protected String mSearchURL = mSearchDomain + enc(mSearchQuery);
	
	public static final String TAG = "JLyrWebSearch";
	
	public LyricsWebSearch(Context context, Track track, String searchEngine) {
		mContext = context;
		mTrack = track;
		mSearchEngine = searchEngine;
		
		init();
	}
	
	protected void init() {
		String artist = mTrack.getArtist();
		String title = mTrack.getTitle();
		
		// We are using DuckDuck to search any search engines, with bang(!) notation
		mSearchDomain = "http://www.duckduckgo.com/?q=";
		
    	if (mSearchEngine.equals("DuckDuckGo")) {
    		mSearchQuery = artist + " - " + title + " lyrics";
    	} else if (mSearchEngine.equals("Google")) {
    		mSearchQuery = "!g " + artist + " - " + title + " lyrics";
    	} else if (mSearchEngine.equals("Bing")) {
    		mSearchQuery = "!b " + artist + " - " + title + " lyrics";
    	} else if (mSearchEngine.equals("Yahoo")) {
    		mSearchQuery = "!y " + artist + " - " + title + " lyrics";
    	} else {
    		mSearchQuery = artist + " - " + title + " lyrics";
    	}
    	
		mSearchURL = mSearchDomain + enc(mSearchQuery);
	}
	
	public void start() {
		Log.i(TAG, "The URL is: " + mSearchURL);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSearchURL));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	mContext.startActivity(intent);
	}
	
	protected static String enc(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "URLEncoder lacks support for UTF-8!?");
			return null;
		}
	}
}
