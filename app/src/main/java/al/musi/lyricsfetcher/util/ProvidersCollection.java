package al.musi.lyricsfetcher.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jlyr.providers.AZLyricsProvider;
import com.jlyr.providers.ChartLyricsProvider;
import com.jlyr.providers.DarkLyricsProvider;
import com.jlyr.providers.DummyProvider;
import com.jlyr.providers.JamendoProvider;
import com.jlyr.providers.LyrDbProvider;
import com.jlyr.providers.LyricsProvider;
import com.jlyr.providers.MetroLyricsProvider;
import com.jlyr.providers.SongLyricsProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ProvidersCollection {
	private static final LinkedHashMap<String, Class<?>> map = new LinkedHashMap<String, Class<?>>() {
		private static final long serialVersionUID = 1L;

		{
			put("SongLyrics", SongLyricsProvider.class);
			put("AZLyrics", AZLyricsProvider.class);
			put("MetroLyrics", MetroLyricsProvider.class);
			put("LyrDB", LyrDbProvider.class);
			put("ChartLyrics", ChartLyricsProvider.class);
			put("DarkLyrics", DarkLyricsProvider.class);
			put("Jamendo", JamendoProvider.class);
			put("Dummy", DummyProvider.class);
		}
	};
	
	private List<String> mSources = null;
	private Context mContext;
	
	public static final String TAG = "JLyrProvidersCollection";
	
	public ProvidersCollection(Context context, String[] sources) {
		mContext = context;
		if (sources != null) {
			mSources = Arrays.asList(sources);
		} else {
			mSources = getSourcesFromPreference();
		}
	}
	
	public ProvidersCollection(String[] sources) {
		if (sources != null) {
			mSources = Arrays.asList(sources);
		} else {
			mSources = ProvidersCollection.getAll();
		}
	}
	
	public LyricsProvider providerFromClass(Class<?> cl, Track track) {
		Constructor<?> ctor = null;
		LyricsProvider provider = null;
		try {
			ctor = cl.getDeclaredConstructor(Track.class);
			ctor.setAccessible(true);
			provider = (LyricsProvider) ctor.newInstance(track);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return provider;
	}
	
	public boolean isEnabled(String source) {
		//TODO: implement the configuration and stuff 
		return true;
	}
	
	public List<String> getSourcesFromPreference() {
		if (mContext == null) {
			return ProvidersCollection.getAll();
		}
    	SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(mContext);
    	
    	String pref = SP.getString("providers", "");
    	Log.i(TAG, "Preference providers: " + pref);
    	if (!pref.equals("")) {
    		return Arrays.asList(pref.split(","));
    	} else {
    		return ProvidersCollection.getAll();
    	}
    }
	
	public LyricsProvider[] toArray(Track track) {
		return toArray(track, true);
	}
	
	public LyricsProvider[] toArray(Track track, boolean onlyEnabled) {
		ArrayList<LyricsProvider> providers = new ArrayList<LyricsProvider>();
		for (String source : mSources) {
			if (!map.containsKey(source)) {
				Log.w(TAG, "Skipping unknown provider: " + source);
				continue;
			}
			if (onlyEnabled && !isEnabled(source)) {
				Log.i(TAG, "Skip disabled provider: " + source);
				continue;
			}
			Class<?> cl = map.get(source);
			LyricsProvider provider = providerFromClass(cl, track);
			if (provider == null) {
				Log.e(TAG, "Error loading provider for " + source);
				continue;
			} else {
				providers.add(provider);
			}
		}
		return providers.toArray(new LyricsProvider[0]);
	}
	
	public List<String> getSources() {
		return mSources;
	}
	
	static public List<String> getAll() {
		return Arrays.asList(map.keySet().toArray(new String[0])); 
	}
}
