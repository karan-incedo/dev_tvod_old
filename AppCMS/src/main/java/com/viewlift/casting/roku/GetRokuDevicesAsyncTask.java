package com.viewlift.casting.roku;

import android.os.AsyncTask;
import android.util.Log;

import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;
import com.viewlift.models.network.rest.AppCMSVideoDetailCall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.functions.Action1;

import static com.viewlift.casting.roku.RokuWrapper.selectedRokuDevice;


public class GetRokuDevicesAsyncTask extends AsyncTask<GetRokuDevicesAsyncTask.Params, Integer, String> {
    private static final String TAG = "GetRokuDevicesAsyncTask";

    private final Action1<String> readyAction;

    public static class Params {
        String url;
        boolean loadFromFile;
        public static class Builder {
            private Params params;
            public Builder() {
                this.params = new Params();
            }
            public Builder url(String url) {
                params.url = url;
                return this;
            }
            public Builder loadFromFile(boolean loadFromFile) {
                params.loadFromFile = loadFromFile;
                return this;
            }
            public Params build() {
                return params;
            }
        }
    }

    public GetRokuDevicesAsyncTask(Action1<String> readyAction) {
        this.readyAction = readyAction;
    }

    @Override
    protected String doInBackground(Params... params) {
        if (params.length > 0) {
                return getAllApps(params[0].url);

        }
        return null;
    }

    @Override
    protected void onPostExecute(String responseRokuDevices) {
        Observable.just(responseRokuDevices).subscribe(readyAction);
    }

    private String getAllApps(String Url) {
        try {
            URL obj = new URL(Url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            InputStream inputStream = con.getInputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String res = response.toString();
            Log.d(TAG, "Roku devices"+res);

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());

            return "";
        }
    }
}