package al.musi.lyricsfetcher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MyActivity extends AppCompatActivity {

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // Display a messagebox.
            Toast.makeText(getApplicationContext(), "You've got an event", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        //Button myButton = (Button) findViewById(R.id.button);

        File sdCardRoot = Environment.getExternalStorageDirectory();
        File dir = new File(sdCardRoot, "download");

        Log.d("path: ", dir.toString());

        /* get filenames from dir path */
        FindFiles findFiles = new FindFiles();
        ArrayList<String> nameOfFiles = findFiles.getFiles(dir.toString());

        /* and display it */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, nameOfFiles);

        ListView listView = (ListView) findViewById(R.id.fajnedane);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(mMessageClickedHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(Settings.ACTION_SEARCH_SETTINGS));
                break;
            case R.id.action_settings:
                startActivity(new Intent(Settings.));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
