package com.viewlift.ccavenue.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.ccavenue.utility.AvenuesParams;
import com.viewlift.ccavenue.utility.Constants;
import com.viewlift.ccavenue.utility.RSAUtility;
import com.viewlift.ccavenue.utility.ServiceHandler;
import com.viewlift.ccavenue.utility.ServiceUtility;
import com.viewlift.presenters.AppCMSPresenter;

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
	private AppCMSPresenter appCMSPresenter;
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		orderID = "" ;
		accessCode = "" ;
		merchantID = "" ;
		cancelRedirectURL = "" ;
		setContentView(R.layout.activity_webview);
		mainIntent = getIntent();
		appCMSPresenter = ((AppCMSApplication) getApplication())
				.getAppCMSPresenterComponent()
				.appCMSPresenter();
		
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
			dialog = new ProgressDialog(WebViewActivity.this);
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
	
			// Making a request to url and getting response
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			String vResponse = getRSAKey() ;
			if(!ServiceUtility.chkNull(vResponse).equals("")
					&& ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR")==-1){
				StringBuffer vEncVal = new StringBuffer("");
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
				encVal = RSAUtility.encrypt(vEncVal.substring(0,vEncVal.length()-1), vResponse);
				params.add(new BasicNameValuePair(AvenuesParams.ACCESS_CODE, accessCode));
				params.add(new BasicNameValuePair(AvenuesParams.ORDER_ID, orderID));
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (dialog.isShowing())
				dialog.dismiss();
			
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

	    	        if (url.equalsIgnoreCase(cancelRedirectURL)) {
						//webview.stopLoading();
						new updateSubscriptionPlanAsyncTask ().execute();
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
			if (getIntent().getBooleanExtra("renewable",false)) {
				params.append(ServiceUtility.addToPostParams("payment_option","OPTCRDC")) ;
			}/* else {
				params.append(ServiceUtility.addToPostParams("payment_option","OPTDBCRD")) ;
				params.append(ServiceUtility.addToPostParams("payment_option","OPTNBK")) ;
				params.append(ServiceUtility.addToPostParams("payment_option","OPTCRDC")) ;
			}*/

			params.append(ServiceUtility.addToPostParams("merchant_param1",getIntent().getStringExtra(getString(R.string.app_cms_site_name))));
			params.append(ServiceUtility.addToPostParams("merchant_param2",getIntent().getStringExtra(getString(R.string.app_cms_user_id))));
			params.append(ServiceUtility.addToPostParams("merchant_param3",getIntent().getStringExtra(getString(R.string.app_cms_plan_id))));
			params.append(ServiceUtility.addToPostParams("merchant_param4","android"));

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
			post_dict.put(getString(R.string.app_cms_site_name), getIntent().getStringExtra(getString(R.string.app_cms_site_name)));
			post_dict.put(getString(R.string.app_cms_user_id), getIntent().getStringExtra(getString(R.string.app_cms_user_id)));
			post_dict.put(getString(R.string.app_cms_device), getString(R.string.app_cms_subscription_key));
			JsonDATA = String.valueOf(post_dict);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		try {
			//URL url = new URL(getString(R.string.app_cms_baseurl)+"/ccavenue/ccavenue/rsakey");
			URL url = new URL ("http://release-api.viewlift.com/ccavenue/ccavenue/rsakey") ;
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			// is output buffer writter
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestProperty ("Authorization", getIntent().getStringExtra("auth_token"));
			urlConnection.setRequestProperty("x-api-token",getIntent().getStringExtra("x-api-token"));
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


	private void finlizePaymentWithUpdatingBackend () {
//		Intent intent = new Intent();
//		intent.putExtra(getString(R.string.app_cms_ccavenue_payment_success),true) ;
//		setResult(RESULT_OK, intent);
//		finish();
		appCMSPresenter.finalizeSignupAfterCCAvenueSubscription(null) ;
	}

	private class updateSubscriptionPlanAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			dialog = new ProgressDialog(WebViewActivity.this);
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			String JsonResponse = null;
			String JsonDATA = "";
			String rsaToken = "" ;
			JSONObject post_dict = new JSONObject();

			try {
				        post_dict.put("vlTransactionId", orderID) ;
						post_dict.put("email",getIntent().getStringExtra("email"));
						post_dict.put("currencyCode","INR");
						post_dict.put("siteId",getIntent().getStringExtra("siteId"));
						post_dict.put("planId",getIntent().getStringExtra(getString(R.string.app_cms_plan_id)));
				        post_dict.put("platform","android");
						post_dict.put("zip","");
						post_dict.put("description","CCAvenue Subscripton");
						post_dict.put("subscription","ccavenue"); //very important to say this is a ccavenue request
				        post_dict.put("authorizedUserName",getIntent().getStringExtra("authorizedUserName"));
				JsonDATA = String.valueOf(post_dict);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;
			try {
				URL url = new URL("http://release-api.viewlift.com/subscription/subscribe");
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				// is output buffer writter
				urlConnection.setRequestMethod("POST");
				urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestProperty ("Authorization", getIntent().getStringExtra("auth_token"));
				urlConnection.setRequestProperty("x-api-token",getIntent().getStringExtra("x-api-token"));
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
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (dialog.isShowing())
				dialog.dismiss();
			displaySuccessPaymentDialog () ;
		}
	}

   private void displaySuccessPaymentDialog () {
	   AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
	   builder1.setMessage("Write your message here.");
	   builder1.setCancelable(true);

	   builder1.setPositiveButton(
			   "Start Watching",
			   new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   finlizePaymentWithUpdatingBackend () ;
					   dialog.cancel();
				   }
			   });

	   AlertDialog alert11 = builder1.create();
	   alert11.show();
   }

} 