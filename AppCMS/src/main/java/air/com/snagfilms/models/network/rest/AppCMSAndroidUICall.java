package air.com.snagfilms.models.network.rest;

import android.net.Uri;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.android.Android;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSAndroidUICall {
    private static final String SAVE_PATH = "Android/";

    private final AppCMSAndroidUI appCMSAndroidUI;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSAndroidUICall(AppCMSAndroidUI appCMSAndroidUI, Gson gson, File storageDirectory) {
        this.appCMSAndroidUI = appCMSAndroidUI;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public Android call(Uri dataUri, boolean loadFromFile) throws IOException {
        if (loadFromFile) {
            return readAndroidFromfile(dataUri);
        }
        return writeAndroidToFile(dataUri, appCMSAndroidUI.get(dataUri.toString()).execute().body());
    }

    private Android writeAndroidToFile(Uri dataUri, Android android) throws IOException {
        String outputFilename = dataUri.getPathSegments().get(dataUri.getPathSegments().size() - 1);
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() + outputFilename));
        String output = gson.toJson(android, Android.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return android;
    }

    private Android readAndroidFromfile(Uri dataUri) throws IOException {
        String inputFilename = dataUri.getPathSegments().get(dataUri.getPathSegments().size() - 1);
        InputStream inputStream = new FileInputStream(
                new File(storageDirectory.toString() + inputFilename));
        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        Android android = gson.fromJson(sb.toString(), Android.class);
        scanner.close();
        inputStream.close();
        return android;
    }
}
