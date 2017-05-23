package air.com.snagfilms.models.network.rest;

import android.net.Uri;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.page.Page;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSPageUICall {
    private static final String TAG = "AppCMSPageUICall";

    private final AppCMSPageUI appCMSPageUI;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSPageUICall(AppCMSPageUI appCMSPageUI, Gson gson, File storageDirectory) {
        this.appCMSPageUI = appCMSPageUI;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public Page call(Uri dataUri, boolean loadFromFile) throws IOException {
        if (loadFromFile) {
            return readPageFromFile(dataUri);
        }
        Page page = null;
        try {
            page = appCMSPageUI.get(dataUri.toString()).execute().body();
            page = writePageToFile(dataUri, page);
        } catch (JsonSyntaxException e) {
            Log.w(TAG, "Error trying to parse input JSON: " + dataUri.toString());
        }
        return page;
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
