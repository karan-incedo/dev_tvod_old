package com.viewlift.models.network.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.ModuleList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
                     boolean forceLoadFromNetwork,
                     Action1<AppCMSAndroidModules> readyAction) {
        //Log.d(TAG, "Retrieving list of modules at URL: " + bundleUrl);

        AppCMSAndroidModules appCMSAndroidModules = new AppCMSAndroidModules();

        readModuleListFromFile(bundleUrl,
                version,
                forceLoadFromNetwork,
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
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(moduleListMap);
            objectOutputStream.close();
            outputStream.close();
        } catch (Exception e) {
            //Log.e(TAG, "Could not write module to file: " +
//                    outputFilename +
//                    " - "
//                    + e.getMessage());
        }
    }

    public void deletePreviousFiles(String url) {
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

    private ModuleDataMap readModuleListFromNetwork(ModuleDataMap moduleDataMap,
                                                    String blocksBaseUrl,
                                                    String version) {
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
        return moduleDataMap;
       // return readModuleListFromNetworkTest(moduleDataMap,blocksBaseUrl,version);
    }

    private ModuleDataMap readModuleListFromNetworkTest(ModuleDataMap moduleDataMap,
                                                        String blocksBaseUrl,
                                                        String version) {
        try {
            String json = null;
            InputStream inputStream = AppCMSApplication.getContext().getAssets().open("androidModule.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

            moduleDataMap.appCMSAndroidModule = gson.fromJson(json,
                    new TypeToken<Map<String, ModuleList>>() {
                    }.getType());
            moduleDataMap.loadedFromNetwork = true;
            new Thread(() -> {
                deletePreviousFiles(getResourceFilenameWithJsonOnly(blocksBaseUrl));
                writeModuleToFile(getResourceFilename(blocksBaseUrl, version), moduleDataMap.appCMSAndroidModule);
            }).run();
        } catch (Exception e1) {
        }
        return moduleDataMap;
    }


    private void readModuleListFromFile(String blocksBaseUrl,
                                        String version,
                                        boolean forceLoadFromNetwork,
                                        Action1<ModuleDataMap> readyAction) {
        Observable.fromCallable(() -> {
            ModuleDataMap moduleDataMap = new ModuleDataMap();
            moduleDataMap.loadedFromNetwork = false;
            if (forceLoadFromNetwork) {
                moduleDataMap = readModuleListFromNetwork(moduleDataMap, blocksBaseUrl, version);
            } else {
                try {
                    InputStream inputStream = new FileInputStream(
                            new File(storageDirectory.toString() +
                                    File.separatorChar +
                                    getResourceFilename(blocksBaseUrl, version)));

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    long startTime = new Date().getTime();
                    Log.d(TAG, "Start time: " + startTime);
                    ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
                    moduleDataMap.appCMSAndroidModule = (HashMap<String, ModuleList>) objectInputStream.readObject();
                    long endTime = new Date().getTime();
                    Log.d(TAG, "End time: " + endTime);
                    Log.d(TAG, "Time elapsed: " + (endTime - startTime));
                    objectInputStream.close();
                    bufferedInputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    //Log.w(TAG, "Failed to load block modules from file: " + e.getMessage());
                    moduleDataMap = readModuleListFromNetwork(moduleDataMap, blocksBaseUrl, version);
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
