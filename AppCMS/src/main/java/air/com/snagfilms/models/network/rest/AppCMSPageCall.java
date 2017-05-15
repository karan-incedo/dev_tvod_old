package air.com.snagfilms.models.network.rest;

import android.net.Uri;
import android.support.annotation.WorkerThread;

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

import air.com.snagfilms.models.data.appcms.android.Android;
import air.com.snagfilms.models.data.appcms.page.Page;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSPageCall {
    private final AppCMSPageAPI appCMSPageAPI;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSPageCall(AppCMSPageAPI appCMSPageAPI, Gson gson, File storageDirectory) {
        this.appCMSPageAPI = appCMSPageAPI;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public Page call(Uri dataUri, boolean loadFromFile) throws IOException {
        if (loadFromFile) {
            return readPageFromFile(dataUri);
        }
        return writePageToFile(dataUri, appCMSPageAPI.get(dataUri.toString()).execute().body());
    }

    private Page writePageToFile(Uri dataUri, Page page) throws IOException {
        String outputFilename = dataUri.getPathSegments().get(dataUri.getPathSegments().size() - 1);
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() + outputFilename));
        String output = gson.toJson(page, Page.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return page;
    }

    private Page readPageFromFile(Uri dataUri) throws IOException {
        String inputFilename = dataUri.getPathSegments().get(dataUri.getPathSegments().size() - 1);
        InputStream inputStream = new FileInputStream(
                new File(storageDirectory.toString() + inputFilename));
        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        Page page = gson.fromJson(sb.toString(), Page.class);
        scanner.close();
        inputStream.close();
        return page;
    }
}
