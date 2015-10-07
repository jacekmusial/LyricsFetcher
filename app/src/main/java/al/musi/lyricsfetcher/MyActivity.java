package al.musi.lyricsfetcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity {

    /**
     * main button, used to start up
     * lyrics fetching
     */
    private Button myButton;

    /**
     * first search field
     * contain name of artist/band
     */
    private EditText editTextArtist;

    /**
     * second search field
     * contain title song
     * TODO modify code so this variable can handle song/album/empty name.
     * if empty then list some of popular song that is in {@see editTextArtist}
     */
    private EditText editTextTitle;

    /**
     * text area below search fields and button
     * used to display lyrics
     */
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //look for text fields
        editTextArtist = (EditText) findViewById(R.id.editTextArtist);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);

        //Watch for button clicks.
        myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextArtist.getText().toString().length() > 3 &&
                        editTextTitle.getText().toString().length() > 3) {
                    Toast.makeText(getBaseContext(), "asdsa", Toast.LENGTH_SHORT).show();

                    textView = (TextView) findViewById(R.id.textViewLyrics);
                    textView.setText("adsasd");
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*//code to find files and display it by R.id.fajnedane
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File dir = new File(sdCardRoot, "download");
        Log.d("path: ", dir.toString());
        // get filenames from dir path
        FindFiles findFiles = new FindFiles();
        ArrayList<String> nameOfFiles = findFiles.getFiles(dir.toString());
        // and display it
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, nameOfFiles);
        ListView listView = (ListView) findViewById(R.id.fajnedane);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(mMessageClickedHandler);*/
    }

    @Override
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
    }
}
