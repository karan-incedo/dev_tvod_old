package com.viewlift.models.network.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.android.Blocks;
import com.viewlift.models.data.appcms.ui.page.ModuleList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by viewlift on 10/3/17.
 */

public class AppCMSAndroidModuleCall {
    private static final String TAG = "AndroidModuleCall";

    private final Gson gson;
    private final AppCMSAndroidModuleRest appCMSAndroidModuleRest;
    private final File storageDirectory;

    @Inject
    public AppCMSAndroidModuleCall(Gson gson,
                                   AppCMSAndroidModuleRest appCMSAndroidModuleRest,
                                   File storageDirectory) {
        this.gson = gson;
        this.appCMSAndroidModuleRest = appCMSAndroidModuleRest;
        this.storageDirectory = storageDirectory;
    }

    public void call(String bundleUrl,
                     String blocksBaseUrl,
                     List<Blocks> blocksList,
                     Action1<AppCMSAndroidModules> readyAction) {
        Log.d(TAG, "Retrieving list of modules at URL: " + bundleUrl);

        AppCMSAndroidModules appCMSAndroidModules = new AppCMSAndroidModules();

        readModuleListFromFile(blocksList,
                blocksBaseUrl,
                (moduleList) -> {
                    appCMSAndroidModules.setModuleListMap(moduleList);
                    Observable.just(appCMSAndroidModules).subscribe(readyAction);
                });
    }

    private void writeModuleToFile(String outputFilename, ModuleList moduleList) {
        try {
            OutputStream outputStream = new FileOutputStream(
                    new File(storageDirectory.toString() +
                            File.separatorChar +
                            outputFilename));
            String output = gson.toJson(moduleList,
                    ModuleList.class);
            outputStream.write(output.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Could not write module to file: " +
                    outputFilename +
                    " - "
                    + e.getMessage());
        }
    }

    private void deletePreviousFiles(String url) {
        String fileToDeleteFilenamePatter = getResourceFilenameWithJsonOnly(url);
        File savedFileDirectory = new File(storageDirectory.toString());
        if (savedFileDirectory.isDirectory()) {
            String[] listExistingFiles = savedFileDirectory.list();
            for (String existingFilename : listExistingFiles) {
                if (existingFilename.contains(fileToDeleteFilenamePatter)) {
                    File fileToDelete = new File(existingFilename);
                    if (fileToDelete.delete()) {
                        Log.i(TAG, "Successfully deleted pre-existing file: " + fileToDelete);
                    } else {
                        Log.e(TAG, "Failed to delete pre-existing file: " + fileToDelete);
                    }
                }
            }
        }
    }

    private void readModuleListFromFile(List<Blocks> blocksList,
                                        String blocksBaseUrl,
                                        Action1<Map<String, ModuleList>> readyAction) {
        Observable.fromCallable(() -> {
            Map<String, ModuleList> appCMSAndroidModule = new HashMap<>();
            if (blocksList != null) {
                for (Blocks blocks : blocksList) {
                    Log.d(TAG, "Retrieving block: " + blocks.getBlock());
                    try {
                        if (!appCMSAndroidModule.containsKey(blocks.getBlock())) {
                            Log.d(TAG, "Attempting to read block from file");
                            InputStream inputStream = new FileInputStream(
                                    new File(storageDirectory.toString() +
                                            File.separatorChar +
                                            getResourceFilename(blocks.getBlock(), blocks.getVersion())));
                            Scanner scanner = new Scanner(inputStream);
                            StringBuffer sb = new StringBuffer();
                            while (scanner.hasNextLine()) {
                                sb.append(scanner.nextLine());
                            }

                            scanner.close();
                            inputStream.close();
                            ModuleList moduleList = gson.fromJson(sb.toString(),
                                    ModuleList.class);
                            deletePreviousFiles(getResourceFilenameWithJsonOnly(blocks.getBlock()));
                            writeModuleToFile(getResourceFilename(blocks.getBlock(), blocks.getVersion()), moduleList);
                            appCMSAndroidModule.put(blocks.getBlock(), moduleList);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Cached file could not be retrieved");

                        StringBuilder bundleUrl = new StringBuilder(blocksBaseUrl);
                        bundleUrl.append("/");
                        bundleUrl.append(blocks.getBlock());
                        bundleUrl.append("/android.json");
                        bundleUrl.append("?version=");
                        bundleUrl.append(blocks.getVersion());

                        Log.d(TAG, "Attempting to retrieve updated module from URL: " +
                                bundleUrl.toString());

                        Response<JsonElement> moduleListResponse =
                                appCMSAndroidModuleRest.get(bundleUrl.toString()).execute();
                        try {
                            if (moduleListResponse.body() != null) {
                                ModuleList moduleList = gson.fromJson(moduleListResponse.body(),
                                        ModuleList.class);
                                deletePreviousFiles(getResourceFilenameWithJsonOnly(blocks.getBlock()));
                                writeModuleToFile(getResourceFilename(blocks.getBlock(), blocks.getVersion()), moduleList);
                                appCMSAndroidModule.put(blocks.getBlock(), moduleList);
                            }
                        } catch (Exception e1) {
                            Log.e(TAG, "Failed to retrieve module from URL: " +
                                    bundleUrl.toString() +
                                    " " +
                                    e1.getMessage());
                        }
                    }
                }
            }

            return appCMSAndroidModule;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((result) -> Observable.just(result).subscribe(readyAction));
    }

    private String getResourceFilenameWithJsonOnly(String url) {
        final String JSON_EXT = ".json";
        int startIndex = url.lastIndexOf(File.separatorChar);
        int endIndex = url.indexOf(JSON_EXT) + JSON_EXT.length();
        if (0 <= startIndex && startIndex < endIndex) {
            return url.substring(startIndex + 1, endIndex);
        }
        return url;
    }

    private String getResourceFilename(String url, String version) {
        int startIndex = url.lastIndexOf(File.separatorChar);
        int endIndex = url.length();
        StringBuilder resourceFilename = new StringBuilder();
        if (0 <= startIndex && startIndex < endIndex) {
            resourceFilename.append(url.substring(startIndex + 1, endIndex));
        } else {
            resourceFilename.append(url);
        }
        resourceFilename.append(".v" + version);
        return url;
    }
}
