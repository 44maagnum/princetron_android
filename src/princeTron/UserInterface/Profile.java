package princeTron.UserInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormatSymbols;

import json.org.json.JSONException;
import json.org.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class Profile extends Activity{
	/** Called when the activity is first created. */
	private String userName;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("Hello, Profile");
		setContentView(tv);


		Bundle extras = getIntent().getExtras(); 
		if(extras !=null){
			userName = extras.getString("userName");
		}

		int[] profile = null;


		try {
			profile = DownloadProfile();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		String month = new DateFormatSymbols().getMonths()[profile[1]];

		tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

		tv.setText("User Name: " + userName + "\n" +
				"Date Joined: " + month + " " + Ordinal(profile[0])+ ", " + profile[5] + "\n" +
				"User Rank: " + profile[4] + "\n" +
				"Wins: " + profile[2] + "\n" +
				"Losses: " + profile[3]);
		
		

	}



	public static String Ordinal(int number)
	{
		if (number == 0) return "0";
		switch (number % 100)
		{
		case 11: 
		case 12: 
		case 13: 
			return number + "th";
		}
		switch (number % 10)
		{
		case 1: return number + "st";
		case 2: return number + "nd";
		case 3: return number + "rd";
		}
		return number + "th";
	}


	private int[] DownloadProfile() throws URISyntaxException, ClientProtocolException, IOException, JSONException {

		if (userName == null)
			userName = "Peter";
		String uri = "http://www.princetron.com/u/" + userName;
		int[] values = new int[6];

		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(uri));
			HttpResponse response = client.execute(request);
			InputStream ips  = response.getEntity().getContent();
			BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));

			StringBuilder sb = new StringBuilder();
			String s;
			while(true )
			{
				s = buf.readLine();
				if(s==null || s.length()==0)
					break;
				sb.append(s);

			}
			buf.close();
			ips.close();

			String input = sb.toString().replace("&quot;", "\"");

//			Toast.makeText(getApplicationContext(), input
//					, Toast.LENGTH_LONG).show();


			JSONObject j = new JSONObject(input);

			values[0] = j.getInt("joined_day");
			values[1] = j.getInt("joined_month");
			values[2] = j.getInt("wins");
			values[3] = j.getInt("losses");
			values[4] = j.getInt("rank");
			values[5] = j.getInt("joined_year");


//			for(int i = 0; i < 6; i++){
//				Toast.makeText(getApplicationContext(), values[i] + ""
//						, Toast.LENGTH_LONG).show();
//			}
		} 
		finally {
			// any cleanup code...
		}
		return values;
	}
	
	
	public static int[] DownloadProfile(String userName) throws URISyntaxException, ClientProtocolException, IOException, JSONException {

		if (userName == null)
			userName = "Peter";
		String uri = "http://www.princetron.com/u/" + userName;
		int[] values = new int[6];

		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(uri));
			HttpResponse response = client.execute(request);
			InputStream ips  = response.getEntity().getContent();
			BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));

			StringBuilder sb = new StringBuilder();
			String s;
			while(true )
			{
				s = buf.readLine();
				if(s==null || s.length()==0)
					break;
				sb.append(s);

			}
			buf.close();
			ips.close();

			String input = sb.toString().replace("&quot;", "\"");

//			Toast.makeText(getApplicationContext(), input
//					, Toast.LENGTH_LONG).show();


			JSONObject j = new JSONObject(input);

			values[0] = j.getInt("joined_day");
			values[1] = j.getInt("joined_month");
			values[2] = j.getInt("wins");
			values[3] = j.getInt("losses");
			values[4] = j.getInt("rank");
			values[5] = j.getInt("joined_year");


//			for(int i = 0; i < 6; i++){
//				Toast.makeText(getApplicationContext(), values[i] + ""
//						, Toast.LENGTH_LONG).show();
//			}
		} 
		finally {
			// any cleanup code...
		}
		return values;
	}
	
	
}
