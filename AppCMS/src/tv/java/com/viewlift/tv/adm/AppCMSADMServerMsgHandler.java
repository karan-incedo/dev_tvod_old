package com.viewlift.tv.adm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.viewlift.R;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by anas.azeem on 3/22/2018.
 * Owned by ViewLift, NYC
 */

/**
 * MyServerMsgHandler abstracts the actions needed by your app to support ADM with your server.
 * It allows the asynchronous sending of http requests to prevent blocking the main thread.
 *
 * @version Revision: 1, Date: 11/11/2012
 */
public class AppCMSADMServerMsgHandler {

    /**
     * Tag for logs.
     */
    private final static String TAG = "ADMMessenger";

    /**
     * The server action "/register" sends the app instance's unique registration ID to your server.
     */
    private final static String REGISTER_ROUTE = "/register";

    /**
     * The server action "/unregister" notifies your server that this app instance is no longer registered with ADM.
     */
    private final static String UNREGISTER_ROUTE = "/unregister";

    /**
     * Sends an asynchronous http request to your server, to avoid blocking the main thread.
     *
     * @param context     Your application's current context.
     * @param httpRequest The http request to send.
     */
    @SuppressLint("StaticFieldLeak")
    private void sendHttpRequest(final Context context, final String httpRequest, final String registrationId) {
        Log.i(TAG, "Sending http request " + httpRequest);
        new AsyncTask<Void, Void, String>() {
            /** {@inheritDoc} */
            protected String doInBackground(final Void... params) {
                String response = "";
                HttpURLConnection connection = null;
                try {

                    URL url = new URL(httpRequest); // here is your URL path

                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("deviceAdmId", registrationId);
                    Log.e("params",postDataParams.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
//                    writer.write(getPostDataString(postDataParams));
                    writer.write("{\"deviceAdmId\" :\""+registrationId+"\"}");

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                        BufferedReader in=new BufferedReader(new
                                InputStreamReader(
                                conn.getInputStream()));

                        StringBuffer sb = new StringBuffer("");
                        String line="";

                        while((line = in.readLine()) != null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        return sb.toString();

                    }
                    else {
                        return new String("false : "+responseCode);
                    }
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }

            @Override
            protected void onPostExecute(String response) {
//                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                super.onPostExecute(response);
            }
        }.execute();
    }
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append(":");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    /**
     * Sends the app instance's unique registration ID to your server.
     *
     * @param context        Your application's current context.
     * @param registrationId Your application instance's registration ID.
     */
    public void registerAppInstance(final Context context, final String registrationId) {
        Log.i(TAG, "Sending registration id to 3rd party server " + registrationId);

        /* Build the URL to address your server. Values for server_address and server_port must be set correctly in your string.xml file. */
        String URL = context.getString(R.string.server_address);
        Log.i(TAG, URL);

        /* Add the registration ID into the request URL. */
        String fullUrl = URL/* + REGISTER_ROUTE*/ /*+ "?device=" + registrationId*/;

        /* Send the registration request asynchronously to prevent blocking the main thread. */
//        sendHttpRequest(context, fullUrl, registrationId);
    }

    /**
     * Notifies your server that this app instance is no longer registered with ADM.
     *
     * @param context        Your application's current context.
     * @param registrationId Your application instance's registration ID.
     */
    public void unregisterAppInstance(final Context context, final String registrationId) {
        Log.i(TAG, "Sending unregistration id to 3rd party server " + registrationId);

        /* Build the URL to address your server. Values for server_address and server_port must be set correctly in your string.xml file. */
        String URL = context.getString(R.string.server_address)/* + ":" + context.getString(R.string.server_port)*/;
        Log.i(TAG, URL);

        /* Add to the request URL the registration ID for the unregistered app instance. */
        String fullUrl = URL + UNREGISTER_ROUTE + "?device=" + registrationId;

        /* Send the unregister request asynchronously to prevent blocking the main thread. */
//        sendHttpRequest(context, fullUrl, registrationId);
    }


public String getPostDataString(JSONObject params) throws Exception {

    StringBuilder result = new StringBuilder();
    boolean first = true;

    Iterator<String> itr = params.keys();

    while (itr.hasNext()) {

        String key = itr.next();
        Object value = params.get(key);

        if (first)
            first = false;
        else
            result.append("&");

        result.append(URLEncoder.encode(key, "UTF-8"));
        result.append(":");
        result.append(URLEncoder.encode(value.toString(), "UTF-8"));

    }
    return result.toString();
}

}

