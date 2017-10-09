package com.viewlift.models.network.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
                     String blocksBaseUrl,
                     List<Blocks> blocksList,
                     Action1<AppCMSAndroidModules> readyAction) {
        Log.d(TAG, "Retrieving list of modules at URL: " + bundleUrl);

        AppCMSAndroidModules appCMSAndroidModules = new AppCMSAndroidModules();

        readModuleListFromFile(blocksList,
                blocksBaseUrl,
                (moduleDataMap) -> {
                    appCMSAndroidModules.setModuleListMap(moduleDataMap.appCMSAndroidModule);
                    appCMSAndroidModules.setLoadedFromNetwork(moduleDataMap.loadedFromNetwork);
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
        String fileToDeleteFilenamePattern = getResourceFilenameWithJsonOnly(url);
        File savedFileDirectory = new File(storageDirectory.toString());
        if (savedFileDirectory.isDirectory()) {
            String[] listExistingFiles = savedFileDirectory.list();
            for (String existingFilename : listExistingFiles) {
                if (existingFilename.contains(fileToDeleteFilenamePattern)) {
                    File fileToDelete = new File(storageDirectory, existingFilename);
                    try {
                        if (fileToDelete.delete()) {
                            Log.i(TAG, "Successfully deleted pre-existing file: " + fileToDelete);
                        } else {
                            Log.e(TAG, "Failed to delete pre-existing file: " + fileToDelete);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Could not delete file: " +
                                fileToDelete +
                                " - " +
                                e.getMessage());
                    }
                }
            }
        }
    }

    private void readModuleListFromFile(List<Blocks> blocksList,
                                        String blocksBaseUrl,
                                        Action1<ModuleDataMap> readyAction) {
        Observable.fromCallable(() -> {
            ModuleDataMap moduleDataMap = new ModuleDataMap();
            moduleDataMap.appCMSAndroidModule = new HashMap<>();
            moduleDataMap.loadedFromNetwork = false;
            if (blocksList != null) {
                for (Blocks blocks : blocksList) {
                    Log.d(TAG, "Retrieving block: " + blocks.getName());
                    try {
                        if (!moduleDataMap.appCMSAndroidModule.containsKey(blocks.getName())) {
                            Log.d(TAG, "Attempting to read block from file");
                            InputStream inputStream = new FileInputStream(
                                    new File(storageDirectory.toString() +
                                            File.separatorChar +
                                            getResourceFilename(blocks.getName(), blocks.getVersion())));
                            Scanner scanner = new Scanner(inputStream);
                            StringBuffer sb = new StringBuffer();
                            while (scanner.hasNextLine()) {
                                sb.append(scanner.nextLine());
                            }

                            scanner.close();
                            inputStream.close();
                            ModuleList moduleList = gson.fromJson(sb.toString(),
                                    ModuleList.class);
                            deletePreviousFiles(getResourceFilenameWithJsonOnly(blocks.getName()));
                            writeModuleToFile(getResourceFilename(blocks.getName(), blocks.getVersion()), moduleList);
                            moduleDataMap.appCMSAndroidModule.put(blocks.getName(), moduleList);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Cached file could not be retrieved");

                        StringBuilder bundleUrl = new StringBuilder(blocksBaseUrl);
                        bundleUrl.append("/");
                        bundleUrl.append(blocks.getName());
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
                                deletePreviousFiles(getResourceFilenameWithJsonOnly(blocks.getName()));
                                writeModuleToFile(getResourceFilename(blocks.getName(), blocks.getVersion()), moduleList);
                                moduleDataMap.appCMSAndroidModule.put(blocks.getName(), moduleList);
                                moduleDataMap.loadedFromNetwork = true;
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

            return moduleDataMap;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((result) -> Observable.just(result).subscribe(readyAction));
    }

    private String getResourceFilenameWithJsonOnly(String url) {
        int startIndex = url.lastIndexOf(File.separatorChar);
        if (0 <= startIndex && startIndex < url.length()) {
            return url.substring(startIndex + 1);
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

    private static class ModuleDataMap {
        Map<String, ModuleList> appCMSAndroidModule;
        boolean loadedFromNetwork;
    }
}