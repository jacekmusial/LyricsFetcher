package al.musi.lyricsfetcher;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by re on 2015-09-24.
 */
public class DisplayLirycsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
