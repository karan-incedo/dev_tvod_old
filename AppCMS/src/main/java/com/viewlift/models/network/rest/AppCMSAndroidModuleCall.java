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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.inject.Inject;

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
                     String version,
                     Action1<AppCMSAndroidModules> readyAction) {
        //Log.d(TAG, "Retrieving list of modules at URL: " + bundleUrl);

        AppCMSAndroidModules appCMSAndroidModules = new AppCMSAndroidModules();

        readModuleListFromFile(bundleUrl,
                version,
                (moduleDataMap) -> {
                    appCMSAndroidModules.setModuleListMap(moduleDataMap.appCMSAndroidModule);
                    appCMSAndroidModules.setLoadedFromNetwork(moduleDataMap.loadedFromNetwork);
                    Observable.just(appCMSAndroidModules).subscribe(readyAction);
                });
    }

    private void writeModuleToFile(String outputFilename, Map<String, ModuleList> moduleListMap) {
        try {
            OutputStream outputStream = new FileOutputStream(
                    new File(storageDirectory.toString() +
                            File.separatorChar +
                            outputFilename));
            String output = gson.toJson(moduleListMap,
                    new TypeToken<Map<String, ModuleList>>(){}.getType());
            outputStream.write(output.getBytes());
            outputStream.close();
        } catch (Exception e) {
            //Log.e(TAG, "Could not write module to file: " +
//                    outputFilename +
//                    " - "
//                    + e.getMessage());
        }
    }

    private void deletePreviousFiles(String url) {
        String fileToDeleteFilenamePattern = getResourceFilenameWithJsonOnly(url);
        File savedFileDirectory = new File(storageDirectory.toString());
        if (savedFileDirectory.isDirectory()) {
            String[] listExistingFiles = savedFileDirectory.list();
            for (String existingFilename : listExistingFiles) {
                if (existingFilename.contains(fileToDeleteFilenamePattern)) {
                    File fileToDelete = new File(storageDirectory, existingFilename);
                    try {
                        if (fileToDelete.delete()) {
//                            //Log.i(TAG, "Successfully deleted pre-existing file: " + fileToDelete);
                        } else {
                            //Log.e(TAG, "Failed to delete pre-existing file: " + fileToDelete);
                        }
                    } catch (Exception e) {
                        //Log.e(TAG, "Could not delete file: " +
//                                fileToDelete +
//                                " - " +
//                                e.getMessage());
                    }
                }
            }
        }
    }

    private void readModuleListFromFile(String blocksBaseUrl,
                                        String version,
                                        Action1<ModuleDataMap> readyAction) {
        Observable.fromCallable(() -> {
            ModuleDataMap moduleDataMap = new ModuleDataMap();
            moduleDataMap.loadedFromNetwork = false;

            try {
                InputStream inputStream = new FileInputStream(
                        new File(storageDirectory.toString() +
                                File.separatorChar +
                                getResourceFilename(blocksBaseUrl, version)));
                Scanner scanner = new Scanner(inputStream);
                StringBuffer sb = new StringBuffer();
                while (scanner.hasNextLine()) {
                    sb.append(scanner.nextLine());
                }

                scanner.close();
                inputStream.close();
                moduleDataMap.appCMSAndroidModule = gson.fromJson(sb.toString(),
                        new TypeToken<Map<String, ModuleList>>(){}.getType());
            } catch (Exception e) {
                //Log.w(TAG, "Failed to load block modules from file: " + e.getMessage());
                try {
                    Response<JsonElement> moduleListResponse =
                            appCMSAndroidModuleRest.get(blocksBaseUrl).execute();
                    if (moduleListResponse != null &&
                            moduleListResponse.body() != null) {
                        moduleDataMap.appCMSAndroidModule = gson.fromJson(moduleListResponse.body(),
                                new TypeToken<Map<String, ModuleList>>() {
                                }.getType());
                        moduleDataMap.loadedFromNetwork = true;
                        deletePreviousFiles(getResourceFilenameWithJsonOnly(blocksBaseUrl));
                        writeModuleToFile(getResourceFilename(blocksBaseUrl, version), moduleDataMap.appCMSAndroidModule);
                    }
                } catch (Exception e1) {
                    //Log.e(TAG, "Failed to load block modules from file: " + e1.getMessage());
                }
            }

            return moduleDataMap;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((result) -> Observable.just(result).subscribe(readyAction));
    }

    private String getResourceFilenameWithJsonOnly(String url) {
        int startIndex = url.lastIndexOf(File.separatorChar);
        StringBuilder resourceFilename = new StringBuilder();
        if (0 <= startIndex && startIndex < url.length()) {
            resourceFilename.append(url.substring(startIndex + 1));
        } else {
            resourceFilename.append(url);
        }
        resourceFilename.append("_blocks_bundle.v");
        return resourceFilename.toString();
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
        resourceFilename.append("_blocks_bundle.v" + version);
        return resourceFilename.toString();
    }

    private static class ModuleDataMap {
        Map<String, ModuleList> appCMSAndroidModule;
        boolean loadedFromNetwork;
    }
}
