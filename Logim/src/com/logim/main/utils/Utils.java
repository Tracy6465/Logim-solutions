package com.logim.main.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

/**
 * 
 * @author David Prieto Rivera Class with utilities functions
 * 
 */
final public class Utils extends Activity {

	private Utils() {
	}

	/**
	 * 
	 * @return Actual date of the Phone in "yyyy-MM-dd" String format
	 */
	public static String getDatePhone()

	{

		Calendar cal = new GregorianCalendar();

		//Date date = cal.getTime();
	long time=cal.getTimeInMillis();
		//SimpleDateFormat df = new SimpleDateFormat();

		//String formatteDate = df.format(date);
		
		String phoneDate=Long.toString(time);
		return phoneDate;

	}

	/**
	 * 
	 * @param client
	 *            http
	 * @return client https Converts a httpclient to a https client
	 */
	public static HttpClient sslClient(HttpClient client) {
		try {
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, client.getParams());
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 
	 * @param inputStream
	 * @return String Converts Stream to a String
	 */

	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param length
	 *            of the String
	 * @return random String
	 */
	public static String getCadenaAlfanumAleatoria(int longitud) {
		String cadenaAleatoria = "";
		long milis = new java.util.GregorianCalendar().getTimeInMillis();
		Random r = new Random(milis);
		int i = 0;
		while (i < longitud) {
			char c = (char) r.nextInt(255);
			if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')) {
				cadenaAleatoria += c;
				i++;
			}
		}
		return cadenaAleatoria;
	}

	public static String doHttpRequest(int num_param,
			ArrayList<Pair> listParam, String url, String method, boolean json) {

		String result = "";
		HttpClient httpclient= new DefaultHttpClient();
		HttpResponse response = null;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					num_param);
			for (int i = 0; i < listParam.size(); i++) {
				nameValuePairs.add(new BasicNameValuePair(
						listParam.get(i).first, listParam.get(i).second));

			}
			if (method.equals("PUT")) {
				HttpPut httpput = new HttpPut(url);
				httpput.setHeader("Content-Type",
						"application/x-www-form-urlencoded;charset=utf-8");
				httpput.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
				response = httpclient.execute(httpput);

			} else if (method.equals("POST")) {

				HttpPost httppost = new HttpPost(url);
				if(!json)
				{
				httppost.setHeader("Content-Type",
						"application/x-www-form-urlencoded;charset=utf-8");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
				}
				else
				{
					httppost.setHeader("Content-Type",
							"application/json");
					JSONObject holder = null;
					try {
						holder = new JSONObject(listParam.get(0).second);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					StringEntity se = new StringEntity(holder.toString());
					httppost.setEntity(se);
					httppost.setHeader("Accept", "application/json");
					httppost.setEntity(se);
					
				}
				response = httpclient.execute(httppost);

			} else if (method.equals("GET")) {

				HttpGet httpget = new HttpGet(url);
				response = httpclient.execute(httpget);

			}

			
			if (response.getStatusLine().toString().equals("HTTP/1.1 200 OK")) {

				// Get hold of the response entity
				HttpEntity entity = response.getEntity();
				
				if (entity != null) {

					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					result = Utils.convertStreamToString(instream);
					instream.close();

				}
			}
			else
			{
				
			}

		} catch (ClientProtocolException e) { // TODO Auto-generated catch
			// block
			
			e.printStackTrace();
		} catch (IOException e) { // TODO Auto-generated catch block
		
			e.printStackTrace();
		}

		return result;

	}
	
	

}

// Centrar texto del titulo
/*
 * ViewGroup decorView= (ViewGroup) this.getWindow().getDecorView();
 * LinearLayout root= (LinearLayout) decorView.getChildAt(0); FrameLayout
 * titleContainer= (FrameLayout) root.getChildAt(0); TextView title= (TextView)
 * titleContainer.getChildAt(0); title.setGravity(Gravity.CENTER);
 * title.setTypeface(null,Typeface.BOLD);
 */

