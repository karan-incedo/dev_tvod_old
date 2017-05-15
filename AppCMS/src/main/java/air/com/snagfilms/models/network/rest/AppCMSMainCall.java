package air.com.snagfilms.models.network.rest;

import android.net.Uri;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.main.Main;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSMainCall {
    private static final String TAG = "AppCMSMainCall";

    private final AppCMSMainAPI appCMSMainAPI;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSMainCall(AppCMSMainAPI appCMSMainAPI, Gson gson, File storageDirectory) {
        this.appCMSMainAPI = appCMSMainAPI;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public Main call(Uri dataUri) throws IOException {
        Main main = appCMSMainAPI.get(dataUri.toString()).execute().body();
        Main mainInStorage = null;
        try {
            mainInStorage = readMainFromFile(dataUri);
        } catch (IOException exception) {
            Log.w(TAG, "Previous version of Main.json file is not in storage");
        }
        if (mainInStorage == null) {
            main.setOldVersion(main.getVersion());
        } else {
            main.setOldVersion(mainInStorage.getVersion());
        }

        return writeMainToFile(dataUri, main);
    }

    private Main writeMainToFile(Uri dataUri, Main main) throws IOException {
        String outputFilename = dataUri.getPathSegments().get(dataUri.getPathSegments().size() - 1);
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() + outputFilename));
        String output = gson.toJson(main, Main.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return main;
    }

    private Main readMainFromFile(Uri dataUri) throws IOException {
        String inputFilename = dataUri.getPathSegments().get(dataUri.getPathSegments().size() - 1);
        InputStream inputStream = new FileInputStream(storageDirectory.toString() + inputFilename);
        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        Main main = gson.fromJson(sb.toString(), Main.class);
        scanner.close();
        inputStream.close();
        return main;
    }
}
