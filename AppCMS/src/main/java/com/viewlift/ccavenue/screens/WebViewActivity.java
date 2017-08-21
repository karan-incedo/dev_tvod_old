package com.viewlift.ccavenue.screens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.viewlift.R;
import com.viewlift.ccavenue.utility.AvenuesParams;
import com.viewlift.ccavenue.utility.Constants;
import com.viewlift.ccavenue.utility.RSAUtility;
import com.viewlift.ccavenue.utility.ServiceHandler;
import com.viewlift.ccavenue.utility.ServiceUtility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WebViewActivity extends Activity {
	private ProgressDialog dialog;
	Intent mainIntent;
	String html, encVal;
	String orderID = "" ;
	String accessCode = "" ;
	String cancelRedirectURL = "" ;
	String merchantID = "" ;
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		orderID = "knhgjlkhgggsdhkjhjghgnkjhg" ;
		accessCode = "AVGQ01EH20AR43QGRA" ;
		merchantID = "138366" ;
		cancelRedirectURL = "http://ccavenue.us-east-1.elasticbeanstalk.com/ccavResponseHandler.jsp" ;
		setContentView(R.layout.activity_webview);
		mainIntent = getIntent();
		
		// Calling async task to get display content
		new RenderView().execute();
	}
	
	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class RenderView extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
//			dialog = new ProgressDialog(WebViewActivity.this);
//			dialog.setMessage("Please wait...");
//			dialog.setCancelable(false);
//			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
	
			// Making a request to url and getting response
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(AvenuesParams.ACCESS_CODE, accessCode));
			params.add(new BasicNameValuePair(AvenuesParams.ORDER_ID, orderID));
			//String rsaKeyURL = mainIntent.getStringExtra(AvenuesParams.RSA_KEY_URL) ;
			//rsaKeyURL = rsaKeyURL + "?access_code=" + accessCode + "&merchant_id=138366&order_id=" + orderID ;
			//String vResponse = sh.makeServiceCall(rsaKeyURL, ServiceHandler.POST, params);
			String vResponse = getRSAKey() ;
			if(!ServiceUtility.chkNull(vResponse).equals("")
					&& ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR")==-1){
				StringBuffer vEncVal = new StringBuffer("");
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
				encVal = RSAUtility.encrypt(vEncVal.substring(0,vEncVal.length()-1), vResponse);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
//			if (dialog.isShowing())
//				dialog.dismiss();
			
			@SuppressWarnings("unused")
			class MyJavaScriptInterface
			{
				@JavascriptInterface
			    public void processHTML(String html)
			    {
			        // process the html as needed by the app
			    	String status = null;
			    	if(html.indexOf("Failure")!=-1){
			    		status = "Transaction Declined!";
			    	}else if(html.indexOf("Success")!=-1){
			    		status = "Transaction Successful!";
			    	}else if(html.indexOf("Aborted")!=-1){
			    		status = "Transaction Cancelled!";
			    	}else{
			    		status = "Status Not Known!";
			    	}
			    	//Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
			    	Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
					intent.putExtra("transStatus", status);
					startActivity(intent);
			    }
			}
			
			final WebView webview = (WebView) findViewById(R.id.webview);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
			webview.setWebViewClient(new WebViewClient(){

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					Log.v("url",url) ;
				}

				@Override
	    	    public void onPageFinished(WebView view, String url) {
	    	        super.onPageFinished(webview, url);
	    	        if(url.indexOf("/ccavResponseHandler.jsp")!=-1){
	    	        	webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
						//webview.loadUrl("https://test.ccavenue.com/transaction/transaction.do?command=initiateTransaction");
	    	        }
	    	    }  

	    	    @Override
	    	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	    	        Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
	    	    }
			});
			
			/* An instance of this class will be registered as a JavaScript interface */
			StringBuffer params = new StringBuffer();
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE,accessCode));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID,merchantID));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID,orderID));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL,cancelRedirectURL));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL,cancelRedirectURL));
			try {
				params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL,URLEncoder.encode(encVal,"UTF-8")));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			
			String vPostParams = params.substring(0,params.length()-1);
			try {
				webview.postUrl(Constants.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
			} catch (Exception e) {
				showToast("Exception occured while opening webview.");
			}
		}
	}
	
	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}

	public String getRSAKey () {
		String JsonResponse = null;
		String JsonDATA = "";
        String rsaToken = "" ;
		JSONObject post_dict = new JSONObject();

		try {
			post_dict.put("site", "snagfilms");
			post_dict.put("userId", "ANDROID-VIVEK");
			post_dict.put("device", "android");
			JsonDATA = String.valueOf(post_dict);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		try {
			URL url = new URL("https://develop-api.viewlift.com/ccavenue/ccavenue/rsakey");
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			// is output buffer writter
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Accept", "application/json");
			//set headers and method
			Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
			writer.write(JsonDATA);
			// json data
			writer.close();
			InputStream inputStream = urlConnection.getInputStream();
			//input stream
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				// Nothing to do.
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String inputLine;
			while ((inputLine = reader.readLine()) != null)
				buffer.append(inputLine + "\n");
			if (buffer.length() == 0) {
				// Stream was empty. No point in parsing.
				return null;
			}
			JsonResponse = buffer.toString();
			//response data
			Log.i("TAG", JsonResponse);
			try {
				JSONObject jsonObj = new JSONObject(JsonResponse);
				rsaToken = jsonObj.getString("rsaToken");
				orderID = jsonObj.getString("orderId") ;
				accessCode = jsonObj.getString("accessCode") ;
				cancelRedirectURL = jsonObj.getString("redirectUrl") ;
				merchantID = jsonObj.getString("merchantId") ;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					Log.e("TAG", "Error closing stream", e);
				}
			}
		}
		return rsaToken ;
	}
} 