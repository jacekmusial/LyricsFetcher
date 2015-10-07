package al.musi.lyricsfetcher.util;

public class Track {
	
	public enum State {
		START, RESUME, PAUSE, COMPLETE, PLAYLIST_FINISHED, UNKNOWN_NONPLAYING
	};

	/**
	 * We have to use this, as signals sent to Scrobble Droid can be void of any
	 * track information if it's "playing" boolean is set to false
	 */
	public static final Track SAME_AS_CURRENT;

	static {
		SAME_AS_CURRENT = new Track("SAME_AS_CURRENT", "SAME_AS_CURRENT", "SAME_AS_CURRENT", "SAME_AS_CURRENT");
	}
	
	String mArtist = null;
	String mTitle = null;
	String mAlbum = null;
	String mYear = null;
	
	public static final String TAG = "JLyrTrack";
	
	public Track(String artist, String title, String album, String year) {
		mArtist = artist;
		mTitle = title;
		mAlbum = album;
		mYear = year;
	}

	public String getArtist() {
		return mArtist;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getAlbum() {
		return mAlbum;
	}
	
	public String getYear() {
		return mYear;
	}
	
	@Override
	public String toString() {
		return mArtist + " - " + mTitle;
		//return "Track [mAlbum=" + mAlbum + "(" + mYear + ")" + ", mArtist=" + mArtist + ", mTitle=" + mTitle + "]";
	}

	/**
	 * Only checks artist, album and track strings, which
	 * means that tracks sent to ScrobblingService can be properly compared.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Track other = (Track) obj;
		if (mAlbum != other.getAlbum())
			return false;
		if (mArtist != other.getArtist())
			return false;
		if (mTitle != other.getTitle())
			return false;
		return true;
	}
}
