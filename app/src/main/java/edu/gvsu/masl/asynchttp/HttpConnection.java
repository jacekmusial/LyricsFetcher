package edu.gvsu.masl.asynchttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Asynchronous HTTP connections
 *
 * @author Greg Zavitz & Joseph Roth
 */
public class HttpConnection implements Runnable {

	public static final int DID_START = 0;
	public static final int DID_ERROR = 1;
	public static final int DID_SUCCEED = 2;

	private static final int GET = 0;
	private static final int POST = 1;
	private static final int PUT = 2;
	private static final int DELETE = 3;
	private static final int BITMAP = 4;

	private String url;
	private int method;
	private Handler handler;
	private String data;

	private HttpClient httpClient;

	public HttpConnection() {
		this(new Handler());
	}

	public HttpConnection(Handler _handler) {
		handler = _handler;
	}

	public void create(int method, String url, String data) {
		this.method = method;
		this.url = url;
		this.data = data;
		ConnectionManager.getInstance().push(this);
	}

	public void get(String url) {
		create(GET, url, null);
	}

	public void post(String url, String data) {
		create(POST, url, data);
	}

	public void put(String url, String data) {
		create(PUT, url, data);
	}

	public void delete(String url) {
		create(DELETE, url, null);
	}

	public void bitmap(String url) {
		create(BITMAP, url, null);
	}

	public void run() {
		handler.sendMessage(Message.obtain(handler, HttpConnection.DID_START));
		httpClient = new DefaultHttpClient();
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 25000);
		try {
			HttpResponse response = null;
			switch (method) {
				case GET:
					response = httpClient.execute(new HttpGet(url));
					break;
				case POST:
					HttpPost httpPost = new HttpPost(url);
					httpPost.setEntity(new StringEntity(data));
					response = httpClient.execute(httpPost);
					break;
				case PUT:
					HttpPut httpPut = new HttpPut(url);
					httpPut.setEntity(new StringEntity(data));
					response = httpClient.execute(httpPut);
					break;
				case DELETE:
					response = httpClient.execute(new HttpDelete(url));
					break;
				case BITMAP:
					response = httpClient.execute(new HttpGet(url));
					processBitmapEntity(response.getEntity());
					break;
			}
			if (method < BITMAP)
				processEntity(response.getEntity());
		} catch (Exception e) {
			handler.sendMessage(Message.obtain(handler,
					HttpConnection.DID_ERROR, e));
		}
		ConnectionManager.getInstance().didComplete(this);
	}

	private void processEntity(HttpEntity entity) throws IllegalStateException,
			IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(entity
				.getContent()));
		String line, result = "";
		String eol = System.getProperty("line.separator");
		while ((line = br.readLine()) != null)
			result += line+eol;
		Message message = Message.obtain(handler, DID_SUCCEED, result);
		handler.sendMessage(message);
	}

	private void processBitmapEntity(HttpEntity entity) throws IOException {
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		Bitmap bm = BitmapFactory.decodeStream(bufHttpEntity.getContent());
		handler.sendMessage(Message.obtain(handler, DID_SUCCEED, bm));
	}

}
