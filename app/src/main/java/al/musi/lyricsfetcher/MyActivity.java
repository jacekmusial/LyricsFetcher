package al.musi.lyricsfetcher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity {

    private Button myButton;
    private Button clearButton;
    private EditText editTextArtist;
    private EditText editTextTitle;
    private TextView textView;
    private String lyrics;
    private Intent intent;

    // Local broadcast
    private BroadcastReceiver broadcastReceiver;

    public static void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager manager = ((ConnectivityManager)
                context.getSystemService(CONNECTIVITY_SERVICE));
        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onResume() {
        //startService(intent);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter("lyricSearching"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                broadcastReceiver);
        stopService(intent);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                lyrics = (String) extras.get("message");
                Log.d("receiver", "its a message: " + lyrics);
                if (lyrics != null) {
                    Log.d("receiver", "got message: " + lyrics);
                    //Toast.makeText(MyActivity.this, lyrics, Toast.LENGTH_SHORT).show();
                    textView = (TextView) findViewById(R.id.textViewLyrics);
                    textView.setText(lyrics);
                }
            }
        };
        //look for text fields
        editTextArtist = (EditText) findViewById(R.id.editTextArtist);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);

        editTextArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO implement
            }
        });

        //Watch for button `search` clicks.
        myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextArtist.getText().toString().length() > 2
                        && editTextTitle.getText().length() > 0) {
                    intent = new Intent(MyActivity.this, AZLyricsProvider.class);
                    intent.putExtra("artist",
                            editTextArtist.getText().toString().toLowerCase());
                    intent.putExtra("title",
                            editTextTitle.getText().toString().toLowerCase());

                    if (isNetworkAvailable(getBaseContext())) {
                        startService(intent); // launch lyrics search service
                    } else {
                        Toast.makeText(MyActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                    //close soft keyboard
                    ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        //watch for button 'clear' clicks.
        clearButton = (Button) findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextArtist.setText("");
                editTextTitle.setText("");
                textView.setText("");
            }
        });
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