package al.musi.lyricsfetcher;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection
{
    private String url;
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1";
    final static int MAX_RETRIES = 5;

    public HttpConnection(String url) {
        this.url = url;
    }

    public String a() {
        URL obj = null;
        HttpURLConnection con = null;
        int numTries = 0;
        while (numTries < MAX_RETRIES) {
            if (numTries != 0) {
                Log.d(":", "Retry n°" + numTries);
            }
            try {
                obj = new URL(this.url);
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(2000);
                //con.setChunkedStreamingMode(0);
                con.setRequestProperty("Accept", "*/*");
                con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                con.setRequestProperty("User-Agent", USER_AGENT);

                int responseCode = con.getResponseCode();
                Log.d(":", "Sending 'GET' request to URL : " + url);
                Log.d(":", "Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            numTries++;
        }
        return "?";
    }
}