package al.musi.lyricsfetcher;

/**
 * Created by re on 2015-09-23.
 */
import java.io.File;
import java.util.ArrayList;

public class FindFiles {

    private static ArrayList<String> xd;

    public FindFiles() {
        xd = new ArrayList<>();
    }

    /**
     * @param path Path to directory where we'll search for files
     * @return ArrayList of Strings
     */
    public ArrayList<String> getFiles(String path) {
        xd = new ArrayList<>();
        this.walk(path);
        return xd;
    }

    private void walk( String path ) {
        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            }else if (f.isFile()) {
                xd.add(f.getName());
            }
        }
    }
}
