package com.viewlift.casting.roku;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class RokuLaunchThread extends Thread {
    private String TAG = "RokuLaunchThread";
    private RokuWrapper client;
    private RokuLaunchThreadParams params;

    RokuLaunchThread(RokuWrapper client) {
        this.client = client;
    }

    public void setParams(RokuLaunchThreadParams params) {
        this.params = params;
    }

    private void launchApp() {
        try {
            String str;
            switch (this.params.getContentType()) {
                case RokuLaunchThreadParams.CONTENT_TYPE_SHOW:
                    //Log.d(TAG, "Request to play a show on Roku");
                    str = "http://" + this.params.getUrl().getAuthority() + "/launch/" + this.params.getRokuAppId();
                    str += "?contentID=" + this.params.getContentId()
                            + "&contentType=show&userID=" + this.params.getUserId();
                    break;
                case RokuLaunchThreadParams.CONTENT_TYPE_FILM:
                    //Log.d(TAG, "Request to play a Film on Roku");
                    str = "http://" + this.params.getUrl().getAuthority() + "/launch/" + this.params.getRokuAppId();
                    str += "?playID=" + this.params.getContentId()
                            + "&contentType=film&userID=" + this.params.getUserId();
                    break;
                default:
                    //Log.d(TAG, "Request to launch app on Roku");
                    str = "http://" + this.params.getUrl().getAuthority() + "/launch/" + this.params.getRokuAppId();
                    break;
            }


            final String finalStr = str;
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        makeHttpCall(finalStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();


        } catch (Exception e) {
            Message msg = Message.obtain();
            msg.what = RokuWrapper.APP_LAUNCH_FAILED_MESSAGE_ID;
            msg.obj = e.getMessage();
            this.client.handler.sendMessage(msg);
            //Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    private void makeFallbackCall(String str) throws IOException {
        URL obj = new URL(str);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/plain; charset=\"utf-8\"");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String appRunLocation = client.getHeader(con, RokuWrapper.RUN_LOCATION);
        Message msg = Message.obtain();
        switch (this.params.getContentType()) {
            case RokuLaunchThreadParams.CONTENT_TYPE_SHOW:
                msg.what = RokuWrapper.SHOW_LAUNCH_MESSAGE_ID;
                break;
            case RokuLaunchThreadParams.CONTENT_TYPE_FILM:
                msg.what = RokuWrapper.FILM_LAUNCH_MESSAGE_ID;
                break;
            default:
                msg.what = RokuWrapper.APP_LAUNCH_MESSAGE_ID;
                break;
        }
        msg.obj = appRunLocation;
        this.client.handler.sendMessage(msg);
        in.close();
    }

    private void makeHttpCall(String strUrl) throws IOException {
        int code = 0;
        String message = "";

        try {

            URL url = new URL(strUrl); // here is your URL path


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.flush();
            writer.close();
            os.close();

            code = conn.getResponseCode();
            conn.getResponseMessage();
            message = "";

            if (code == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                message = sb.toString();

            } else {
                message = new String("false : " + code);
            }
        } catch (Exception e) {
        }

        Message msg = Message.obtain();
        if (code == 200 || code == 204) {
            switch (this.params.getContentType()) {
                case RokuLaunchThreadParams.CONTENT_TYPE_SHOW:
                    msg.what = RokuWrapper.SHOW_LAUNCH_MESSAGE_ID;
                    break;
                case RokuLaunchThreadParams.CONTENT_TYPE_FILM:
                    msg.what = RokuWrapper.FILM_LAUNCH_MESSAGE_ID;
                    break;
                default:
                    msg.what = RokuWrapper.APP_LAUNCH_MESSAGE_ID;
                    break;
            }
            this.client.handler.sendMessage(msg);
        } else {
            msg.what = RokuWrapper.APP_LAUNCH_FAILED_MESSAGE_ID;
            this.client.handler.sendMessage(msg);
        }
    }


    private void stopApp() {
        try {
            String strStopRokuUrl = "http://" + this.params.getUrl().getAuthority() + "/keypress/Home";

            URL url = new URL(strStopRokuUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.flush();
            writer.close();
            os.close();

            int code = conn.getResponseCode();
            conn.getResponseMessage();
            String message = "";

            if (code == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }
                Message msg = Message.obtain();
                msg.what = RokuWrapper.APP_STOPPED_MESSAGE_ID;
                this.client.handler.sendMessage(msg);
                in.close();
                message = sb.toString();

            } else {
                message = new String("false : " + code);
            }


        } catch (FileNotFoundException fne) {
            Message msg = Message.obtain();
            msg.what = RokuWrapper.APP_STOPPED_MESSAGE_ID;
            this.client.handler.sendMessage(msg);
        } catch (Exception e) {
            //Log.e("RokuLaunchThread: ", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        switch (this.params.getAction()) {
            case RokuWrapper.ACTION_LAUNCH:
                this.launchApp();
                break;
            case RokuWrapper.ACTION_STOP:
                this.stopApp();
                break;
            default:
                break;
        }
    }
}
