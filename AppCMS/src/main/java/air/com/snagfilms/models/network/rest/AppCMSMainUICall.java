package air.com.snagfilms.models.network.rest;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.inject.Inject;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSMainUICall {
    private static final String TAG = "AppCMSMainUICall";

    private final AppCMSMainUI appCMSMainUI;
    private final Gson gson;
    private final File storageDirectory;
    private final String mainVersionKey;
    private final String mainOldVersionKey;

    @Inject
    public AppCMSMainUICall(AppCMSMainUI appCMSMainUI,
                            Gson gson,
                            File storageDirectory,
                            String mainVersionKey,
                            String mainOldVersionKey) {
        this.appCMSMainUI = appCMSMainUI;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
        this.mainVersionKey = mainVersionKey;
        this.mainOldVersionKey = mainOldVersionKey;
    }

    @WorkerThread
    public JsonElement call(Context context, String siteId) throws IOException {
        String appCMSMainUrl = context.getString(R.string.app_cms_main_url,
                context.getString(R.string.app_cms_api_baseurl),
                siteId);
        Uri dataUri = Uri.parse(appCMSMainUrl);
        JsonElement main = appCMSMainUI.get(dataUri.toString()).execute().body();
        JsonElement mainInStorage = null;
        try {
            mainInStorage = readMainFromFile(dataUri);
        } catch (IOException exception) {
            Log.w(TAG, "Previous version of Main.json file is not in storage");
        }

        boolean useExistingOldVersion = true;

        if (mainInStorage != null) {
            try {
                main.getAsJsonObject().add(mainOldVersionKey,
                        mainInStorage.getAsJsonObject().get(mainVersionKey));
                useExistingOldVersion = false;
            } catch (IllegalStateException e) {
                Log.w(TAG, "Previous file is invalid: " + e.toString());
            }
        }

        if (useExistingOldVersion) {
            main.getAsJsonObject().add(mainOldVersionKey,
                    main.getAsJsonObject().get(mainVersionKey));
        }

        return writeMainToFile(dataUri, main);
    }

    private JsonElement writeMainToFile(Uri dataUri, JsonElement main) throws IOException {
        String outputFilename = dataUri.getPathSegments().get(dataUri.getPathSegments().size() - 1);
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() + outputFilename));
        String output = main.toString();
        outputStream.write(output.getBytes());
        outputStream.close();
        return main;
    }

    private JsonElement readMainFromFile(Uri dataUri) throws IOException {
        String inputFilename = dataUri.getPathSegments().get(dataUri.getPathSegments().size() - 1);
        InputStream inputStream = new FileInputStream(storageDirectory.toString() + inputFilename);
        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        JsonElement main = gson.toJsonTree(sb.toString());
        scanner.close();
        inputStream.close();
        return main;
    }
}
