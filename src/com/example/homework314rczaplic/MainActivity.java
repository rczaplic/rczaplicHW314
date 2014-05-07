package com.example.homework314rczaplic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity
{
	//private static final String TAG = "MainActivity";

	public static final String PREFS_NAME = "MyPrefsFile";
	
	public static final String UW_ZIP_CODE = "98195";
	public static final String GA_ZIP_CODE = "30188";
	
	// List of Preference Names
	public static final String PREF_ZIP_CODE = "zipcode";
	public static final String PREF_LOCATION = "location";
	public static final String PREF_TEXT = "curr_text";
	public static final String PREF_TEMP = "curr_temp";
	public static final String PREF_CODE = "curr_code";
	
	public static final String PREF_DATE_1 = "date1";
	public static final String PREF_LOW_1 = "low1";
	public static final String PREF_HIGH_1 = "high1";
	public static final String PREF_TEXT_1 = "text1";
	public static final String PREF_CODE_1 = "code1";
	
	public static final String PREF_DATE_2 = "date2";
	public static final String PREF_LOW_2 = "low2";
	public static final String PREF_HIGH_2 = "high2";
	public static final String PREF_TEXT_2 = "text2";
	public static final String PREF_CODE_2 = "code2";
	
	public static final String PREF_DATE_3 = "date3";
	public static final String PREF_LOW_3 = "low3";
	public static final String PREF_HIGH_3 = "high3";
	public static final String PREF_TEXT_3 = "text3";
	public static final String PREF_CODE_3 = "code3";
	
	private EditText editZipCode;
	private TextView textViewCity;
	private TextView textCurrent;
	private ImageView imageCurrent;
	
	private TextView textDay1;
	private TextView textHigh1;
	private TextView textLow1;
	private TextView textText1;
	private ImageView imageDay1;
	
	private TextView textDay2;
	private TextView textHigh2;
	private TextView textLow2;
	private TextView textText2;
	private ImageView imageDay2;
	
	private TextView textDay3;
	private TextView textHigh3;
	private TextView textLow3;
	private TextView textText3;
	private ImageView imageDay3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		editZipCode = (EditText)findViewById(R.id.editZipCode);
		textViewCity = (TextView)findViewById(R.id.textCity);
		textCurrent = (TextView)findViewById(R.id.textCurrent);
		imageCurrent = (ImageView)findViewById(R.id.imageCurrent);
		
		textDay1 = (TextView)findViewById(R.id.textDay1);
		textHigh1 = (TextView)findViewById(R.id.textHigh1);
		textLow1 = (TextView)findViewById(R.id.textLow1);
		textText1 = (TextView)findViewById(R.id.textText1);
		imageDay1 = (ImageView)findViewById(R.id.imageDay1);
		
		textDay2 = (TextView)findViewById(R.id.textDay2);
		textHigh2 = (TextView)findViewById(R.id.textHigh2);
		textLow2 = (TextView)findViewById(R.id.textLow2);
		textText2 = (TextView)findViewById(R.id.textText2);
		imageDay2 = (ImageView)findViewById(R.id.imageDay2);
		
		textDay3 = (TextView)findViewById(R.id.textDay3);
		textHigh3 = (TextView)findViewById(R.id.textHigh3);
		textLow3 = (TextView)findViewById(R.id.textLow3);
		textText3 = (TextView)findViewById(R.id.textText3);
		imageDay3 = (ImageView)findViewById(R.id.imageDay3);
		
		editZipCode.setOnEditorActionListener(new OnEditorActionListener() 
		{
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
		    {
		        if (actionId == EditorInfo.IME_ACTION_DONE) 
		        {
		        	updateForecast();
		            return true;
		        }
		        else 
		        {
		            return false;
		        }
		    }
		});
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(PREF_ZIP_CODE, editZipCode.getText().toString());
		
		// Commit the edits!
		editor.commit();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		
		String zipCode = settings.getString(PREF_ZIP_CODE, "");
		
		if (zipCode.length() < 5)
		{
			editZipCode.setText(UW_ZIP_CODE); // Set UW Zip Code
		}
		else
		{
			editZipCode.setText(zipCode);
		}
		
		updateForecast();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection 
	    switch (item.getItemId()) {
	        case R.id.action_refresh:
	        	updateForecast();
	        	return true;
	        
	        case R.id.action_seattle:
	        	editZipCode.setText(UW_ZIP_CODE);
	        	updateForecast();
	        	return true;
	        	
	        case R.id.action_woodstock:
	        	editZipCode.setText(GA_ZIP_CODE);
	        	updateForecast();
	        	return true;
	        	
	        default:
	        	return super.onOptionsItemSelected(item);
	    }
	}
	
	private void updateForecast()
	{				
		// 1. Validate the Zip Code
		if (editZipCode.getText().length() < 5)
		{
			editZipCode.setError("Invalid Zip Code", null);		
			return;
		}
		else
		{
			editZipCode.setError(null);
		}
		
		// 2. Hide the soft keyboard if its there
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editZipCode.getWindowToken(), 0);
		
		// 3. Save off the zip code for future use.
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(PREF_ZIP_CODE, editZipCode.getText().toString());
		
		// Commit the edits!
		editor.commit();		
		
		// 4. Kick off the async task to obtain the proper data
		new FetchWeatherTask().execute();	
	}
	
	// When FetchWeatherTask finishes, it calls the code below to update the activity UI 
	private void updateWeather()
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		
		textViewCity.setText(settings.getString(PREF_LOCATION, ""));
		
		///// Set Current Day UI Widgets /////
		String text = settings.getString(PREF_TEXT, "");
		String temp = settings.getString(PREF_TEMP, "");
		String code = settings.getString(PREF_CODE, "");
		
		String current;
		if (!temp.equals(""))
			current = text + ", " + temp + "F, " + convertFtoC(temp) + "C"; 
		else
			current = text + ", " + temp + "F";
		
		textCurrent.setText(current);
		
		setImage(imageCurrent, code);
		
		///// Set Day 1 UI Widgets /////
		String date = settings.getString(PREF_DATE_1, "");
		String low = settings.getString(PREF_LOW_1, "");
		String high = settings.getString(PREF_HIGH_1, "");
		text = settings.getString(PREF_TEXT_1, "");
		code = settings.getString(PREF_CODE_1, "");
		
		textDay1.setText(date);
		
		current = high + "F, " + convertFtoC(high) + "C";
		textHigh1.setText(current);
		
		current = low + "F, " + convertFtoC(low) + "C";
		textLow1.setText(current);
		
		textText1.setText(text);
		
		setImage(imageDay1, code);
		
		///// Set Day 2 UI Widgets /////
		date = settings.getString(PREF_DATE_2, "");
		low = settings.getString(PREF_LOW_2, "");
		high = settings.getString(PREF_HIGH_2, "");
		text = settings.getString(PREF_TEXT_2, "");
		code = settings.getString(PREF_CODE_2, "");
		
		textDay2.setText(date);
		
		current = high + "F, " + convertFtoC(high) + "C";
		textHigh2.setText(current);
		
		current = low + "F, " + convertFtoC(low) + "C";
		textLow2.setText(current);
		
		textText2.setText(text);
		
		setImage(imageDay2, code);
		
		///// Set Day 3 UI Widgets /////
		date = settings.getString(PREF_DATE_3, "");
		low = settings.getString(PREF_LOW_3, "");
		high = settings.getString(PREF_HIGH_3, "");
		text = settings.getString(PREF_TEXT_3, "");
		code = settings.getString(PREF_CODE_3, "");
		
		textDay3.setText(date);
		
		current = high + "F, " + convertFtoC(high) + "C";
		textHigh3.setText(current);
		
		current = low + "F, " + convertFtoC(low) + "C";
		textLow3.setText(current);
		textText3.setText(text);
			
		setImage(imageDay3, code);
	}
	
	private String convertFtoC(String temp)
	{
		float f = Float.parseFloat(temp);
		float c = (float) ((f - 32.0)*(5.0 / 9.0));
		
		DecimalFormat df = new DecimalFormat("0.0");
		
		return df.format(c);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	// FetchWeatherTask - private class to do async data collection
	/////////////////////////////////////////////////////////////////////////////////////////////
	private class FetchWeatherTask extends AsyncTask<Void,Void,Void>
	{
		private static final String URL = "http://weather.yahooapis.com/forecastrss?p=";
		
		private static final String TAG_DESCRIPTION = "description";
		private static final String TAG_WEATHER = "yweather:condition";
		private static final String TAG_FORECAST = "yweather:forecast";
		
		@Override
		protected Void doInBackground(Void... parms)
		{
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);			
			String zipCode = settings.getString(PREF_ZIP_CODE, "");

			String url = URL + zipCode;
			
			try 
			{
				String xmlString = new String(getUrlBytes(url));
				//Log.i(TAG, xmlString);
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = factory.newPullParser();
				parser.setInput(new StringReader(xmlString));
				
				parseItems(parser);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}										
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			
			updateWeather();
		}		
		
		private byte[] getUrlBytes(String urlSpec) throws IOException
		{
			URL url = new URL(urlSpec);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
			try
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				InputStream in = connection.getInputStream();
				
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					return null;
				}
				
				int bytesRead = 0;
				byte[] buffer = new byte[1024];
				
				while ((bytesRead = in.read(buffer)) > 0)
				{
					out.write(buffer, 0, bytesRead);				
				}
				
				out.close();
				return out.toByteArray();
			}
			finally
			{
				connection.disconnect();
			}
		}
		
		private void parseItems(XmlPullParser parser)
				throws XmlPullParserException, IOException
		{
			boolean description = true; // Only read the first description tag.
			int forecast = 0;
			String text = "";
			String temp = "";
			String code = "";
			String date = "";
			String low = "";
			String high = "";			

			int eventType = parser.next();
			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			
			while (eventType != XmlPullParser.END_DOCUMENT)
			{			
				if (description && eventType == XmlPullParser.START_TAG &&
						TAG_DESCRIPTION.equals(parser.getName()))
				{
					editor.putString(PREF_LOCATION, parser.nextText());
					
					description = false;
				}
				
				if (eventType == XmlPullParser.START_TAG &&
						TAG_WEATHER.equals(parser.getName()))
				{
					text = parser.getAttributeValue(null, "text");
					temp = parser.getAttributeValue(null, "temp");
					code = parser.getAttributeValue(null, "code");
					
					editor.putString(PREF_TEXT, text);
					editor.putString(PREF_TEMP, temp);
					editor.putString(PREF_CODE, code);
				}
				
				if (eventType == XmlPullParser.START_TAG &&
						TAG_FORECAST.equals(parser.getName()))
				{
					date = parser.getAttributeValue(null, "date");
					low = parser.getAttributeValue(null, "low");
					high = parser.getAttributeValue(null, "high");
					text = parser.getAttributeValue(null, "text");
					code = parser.getAttributeValue(null, "code");
						
					if (forecast == 1)
					{
						editor.putString(PREF_DATE_1, date);
						editor.putString(PREF_LOW_1, low);
						editor.putString(PREF_HIGH_1, high);
						editor.putString(PREF_TEXT_1, text);
						editor.putString(PREF_CODE_1, code);
					}
					else if (forecast == 2)
					{
						editor.putString(PREF_DATE_2, date);
						editor.putString(PREF_LOW_2, low);
						editor.putString(PREF_HIGH_2, high);
						editor.putString(PREF_TEXT_2, text);
						editor.putString(PREF_CODE_2, code);
					}
					else if (forecast == 3)
					{
						editor.putString(PREF_DATE_3, date);
						editor.putString(PREF_LOW_3, low);
						editor.putString(PREF_HIGH_3, high);
						editor.putString(PREF_TEXT_3, text);
						editor.putString(PREF_CODE_3, code);
					}

					forecast++;
				}
				
				eventType = parser.next();
			}
			
			editor.commit();
		}		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	// setImage - Used to populate image for current and forecasted weather.  Yes, I could use
	// an async task to load from the yahoo image link, but this gives me the ability to change
	// out the images if I ever wanted to use my own.
	/////////////////////////////////////////////////////////////////////////////////////////////	
	private void setImage(ImageView image, String code)
	{
		if (code.equals("0") || code.equals("00"))
		{
			image.setImageResource(R.drawable.ic_35); // 0 - Tornado uses the same image as 35 - Mixed Rain and Hail
		}
		else if (code.equals("1") || code.equals("01"))
		{
			image.setImageResource(R.drawable.ic_01);
		}
		else if (code.equals("2") || code.equals("02"))
		{
			image.setImageResource(R.drawable.ic_02);
		}
		else if (code.equals("3") || code.equals("03"))
		{
			image.setImageResource(R.drawable.ic_03);
		}
		else if (code.equals("4") || code.equals("04"))
		{
			image.setImageResource(R.drawable.ic_04);
		}
		else if (code.equals("5") || code.equals("05"))
		{
			image.setImageResource(R.drawable.ic_05);
		}
		else if (code.equals("6") || code.equals("06"))
		{
			image.setImageResource(R.drawable.ic_06);
		}
		else if (code.equals("7") || code.equals("07"))
		{
			image.setImageResource(R.drawable.ic_07);
		}
		else if (code.equals("8") || code.equals("08"))
		{
			image.setImageResource(R.drawable.ic_08);
		}
		else if (code.equals("9") || code.equals("09"))
		{
			image.setImageResource(R.drawable.ic_09);
		}
		
		else if (code.equals("10"))
		{
			image.setImageResource(R.drawable.ic_10);
		}		
		else if (code.equals("11"))
		{
			image.setImageResource(R.drawable.ic_11);
		}
		else if (code.equals("12"))
		{
			image.setImageResource(R.drawable.ic_12);
		}
		else if (code.equals("13"))
		{
			image.setImageResource(R.drawable.ic_13);
		}
		else if (code.equals("14"))
		{
			image.setImageResource(R.drawable.ic_14);
		}
		else if (code.equals("15"))
		{
			image.setImageResource(R.drawable.ic_15);
		}
		else if (code.equals("16"))
		{
			image.setImageResource(R.drawable.ic_16);
		}
		else if (code.equals("17"))
		{
			image.setImageResource(R.drawable.ic_17);
		}
		else if (code.equals("18"))
		{
			image.setImageResource(R.drawable.ic_18);
		}
		else if (code.equals("19"))
		{
			image.setImageResource(R.drawable.ic_19);
		}
		
		else if (code.equals("20"))
		{
			image.setImageResource(R.drawable.ic_20);
		}		
		else if (code.equals("21"))
		{
			image.setImageResource(R.drawable.ic_21);
		}
		else if (code.equals("22"))
		{
			image.setImageResource(R.drawable.ic_22);
		}
		else if (code.equals("23"))
		{
			image.setImageResource(R.drawable.ic_23);
		}
		else if (code.equals("24"))
		{
			image.setImageResource(R.drawable.ic_24);
		}
		else if (code.equals("25"))
		{
			image.setImageResource(R.drawable.ic_25);
		}
		else if (code.equals("26"))
		{
			image.setImageResource(R.drawable.ic_26);
		}
		else if (code.equals("27"))
		{
			image.setImageResource(R.drawable.ic_27);
		}
		else if (code.equals("28"))
		{
			image.setImageResource(R.drawable.ic_28);
		}
		else if (code.equals("29"))
		{
			image.setImageResource(R.drawable.ic_29);
		}
		
		else if (code.equals("30"))
		{
			image.setImageResource(R.drawable.ic_30);
		}
		else if (code.equals("31"))
		{
			image.setImageResource(R.drawable.ic_31);
		}
		else if (code.equals("32"))
		{
			image.setImageResource(R.drawable.ic_32);
		}
		else if (code.equals("33"))
		{
			image.setImageResource(R.drawable.ic_33);
		}
		else if (code.equals("34"))
		{
			image.setImageResource(R.drawable.ic_34);
		}
		else if (code.equals("35"))
		{
			image.setImageResource(R.drawable.ic_35);
		}
		else if (code.equals("36"))
		{
			image.setImageResource(R.drawable.ic_36);
		}
		else if (code.equals("37"))
		{
			image.setImageResource(R.drawable.ic_37);
		}
		else if (code.equals("38"))
		{
			image.setImageResource(R.drawable.ic_38);
		}
		else if (code.equals("39"))
		{
			image.setImageResource(R.drawable.ic_39);
		}
		
		else if (code.equals("40"))
		{
			image.setImageResource(R.drawable.ic_40);
		}
		else if (code.equals("41"))
		{
			image.setImageResource(R.drawable.ic_41);
		}
		else if (code.equals("42"))
		{
			image.setImageResource(R.drawable.ic_42);
		}
		else if (code.equals("43"))
		{
			image.setImageResource(R.drawable.ic_43);
		}
		else if (code.equals("44"))
		{
			image.setImageResource(R.drawable.ic_44);
		}
		else if (code.equals("45"))
		{
			image.setImageResource(R.drawable.ic_45);
		}
		else if (code.equals("46"))
		{
			image.setImageResource(R.drawable.ic_46);
		}
		else if (code.equals("47"))
		{
			image.setImageResource(R.drawable.ic_47);
		}
	}
}
