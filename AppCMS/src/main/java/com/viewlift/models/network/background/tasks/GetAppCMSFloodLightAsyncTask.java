package com.viewlift.models.network.background.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.viewlift.R;
import com.viewlift.models.network.rest.AppCMSFloodLightRest;

import java.util.Random;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSFloodLightAsyncTask extends AsyncTask<Void, Void, String> {

    private final Context context;
    private final AppCMSFloodLightRest appCMSFloodLightRest;
    private final Action1 action1;
    private String response = "";

    @Inject
    public GetAppCMSFloodLightAsyncTask(AppCMSFloodLightRest appCMSFloodLightRest, Context context, Action1 action1) {
        this.context = context;
        this.appCMSFloodLightRest = appCMSFloodLightRest;
        this.action1 = action1;
    }

    @Override
    protected String doInBackground(Void... params) {
        AdvertisingIdClient.Info idInfo = null;
        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            String url = context.getString(R.string.app_cms_floodlight_url,
                    idInfo.getId(),
                    randInt(1, 10) + "");
            /*String url = "src=6070801;cat=appco0;type=msnbm0;dc_rdid="+advertId.trim()+
                    ";dc_lat=;tag_for_child_directed_treatment=;ord="+randInt(1,10);;*/
            response = appCMSFloodLightRest.get(url).execute().body();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        Toast.makeText(context, "Resonse --" + response, Toast.LENGTH_LONG).show();
        Observable.just("succsss").subscribe(action1);
    }

    private int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

}
