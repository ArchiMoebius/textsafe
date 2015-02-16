package io.github.poerhiza.textsafe.utilities;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DataTransport
{
	private static final int TIMEOUT_MILLISEC = 1000;

	public static void sendDataToHTTPService(String URL, HashMap<String, String> data)
	{
	  try {
	        JSONObject json = new JSONObject();

	        for (Map.Entry<String, String> entry : data.entrySet()) 
	        { 
	        	json.put(entry.getKey(), entry.getValue());
        	}
	        HttpParams httpParams = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
	        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
	        HttpClient client = new DefaultHttpClient(httpParams);

	        HttpPost request = new HttpPost(URL);
	        request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
	        request.setHeader("json", json.toString());
	        HttpResponse response = client.execute(request);
	        HttpEntity entity = response.getEntity();

	        if (entity != null) {
	            InputStream instream = entity.getContent();

	            String result = convertStreamToString(instream);
	            Log.i("Read from server", result);
	        }
	    } catch (Throwable t) {
	        Log.i("Request failed: ", t.toString());
	    }
	}

	/*
	* To convert the InputStream to String we use the BufferedReader.readLine()
	* method. We iterate until the BufferedReader return null which means
	* there's no more data to read. Each line will appended to a StringBuilder
	* and returned as String.
	*/
	private static String convertStreamToString(InputStream is) {
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
	
	public static String returnNumber(Context ctx) {
	     String number = null;
	     String service = Context.TELEPHONY_SERVICE;
	     TelephonyManager tel_manager = (TelephonyManager) ctx.getSystemService(service);
	     int device_type = tel_manager.getPhoneType();

	     switch (device_type) {
	           case (TelephonyManager.PHONE_TYPE_CDMA):
	              number = tel_manager.getLine1Number();
	           break;
	           default:
	             number = "";
	            break;
	     }
	     return number;
	}
}
