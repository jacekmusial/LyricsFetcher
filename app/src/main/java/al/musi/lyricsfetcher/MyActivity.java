package al.musi.lyricsfetcher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity {

    private Button myButton;
    private EditText editTextArtist;
    private EditText editTextTitle;
    private TextView textView;
    private String lyrics;

    // Local broadcast
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lyrics = intent.getStringExtra("message");
            Log.d("receiver", "got message: " + lyrics);
            Toast.makeText(getBaseContext(), lyrics, Toast.LENGTH_LONG).show();
            //textView.setText(lyrics);
        }
    };

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter("lyricSearching"));
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //look for text fields
        editTextArtist = (EditText) findViewById(R.id.editTextArtist);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("lyricsSearching"));

        //Watch for button clicks.
        myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextArtist.getText().toString().length() > 3 &&
                        editTextTitle.getText().toString().length() > 3) {
                    //launch lyrics search service
                    Intent intent = new Intent(MyActivity.this, AZLyricsProvider.class);
                    intent.putExtra("artist", editTextArtist.toString());
                    intent.putExtra("title", editTextTitle.toString());
                    startService(intent);
                    //Toast.makeText(getBaseContext(), "asdsa", Toast.LENGTH_SHORT).show();
                    //textView = (TextView) findViewById(R.id.textViewLyrics);
                    //textView.setText("adsasd");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                broadcastReceiver);
        super.onPause();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viewer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //TODO implement
            case R.id.reload_menu_item:
                break;
            case R.id.save_menu_item:
                break;
            case R.id.delete_menu_item:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }*/
}