package com.viewlift.models.network.rest;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.ModuleList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    private final AssetManager assetManager;
    private final Gson gson;
    private final AppCMSAndroidModuleRest appCMSAndroidModuleRest;
    private final File storageDirectory;

    private static final String[][] jsonFromAssets = {
            {"trayXX", "trayXX.json"}
    };
    private String xApiKey;

    @Inject
    public AppCMSAndroidModuleCall(AssetManager assetManager,
                                   Gson gson,
                                   AppCMSAndroidModuleRest appCMSAndroidModuleRest,
                                   File storageDirectory) {
        this.assetManager = assetManager;
        this.gson = gson;
        this.appCMSAndroidModuleRest = appCMSAndroidModuleRest;
        this.storageDirectory = storageDirectory;
    }

    public void call(String bundleUrl,
                     String xApiKey,
                     String version,
                     boolean forceLoadFromNetwork,
                     boolean bustCache,
                     Action1<AppCMSAndroidModules> readyAction) {
        Log.d(TAG, "Retrieving list of modules at URL: " + bundleUrl);

        this.xApiKey = xApiKey;

        AppCMSAndroidModules appCMSAndroidModules = new AppCMSAndroidModules();

        readModuleListFromFile(bundleUrl,
                version,
                forceLoadFromNetwork,
                bustCache,
                (moduleDataMap) -> {
                    Log.d(TAG, "Retrieving list of modules at URL: module " + moduleDataMap.appCMSAndroidModule);
                    addMissingModulesFromAssets(moduleDataMap.appCMSAndroidModule);
                    appCMSAndroidModules.setModuleListMap(moduleDataMap.appCMSAndroidModule);
                    appCMSAndroidModules.setLoadedFromNetwork(moduleDataMap.loadedFromNetwork);
                    Log.d(TAG, "Retrieving list of modules at URL: module " + moduleDataMap.appCMSAndroidModule);

                    Observable.just(appCMSAndroidModules)
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(readyAction);
                });
    }

    private void addMissingModulesFromAssets(Map<String, ModuleList> moduleListMap) {
        if (assetManager != null && moduleListMap != null) {
            for (String[] jsonFromAssetsVal : jsonFromAssets) {
                if (jsonFromAssetsVal != null && jsonFromAssetsVal.length == 2) {
                    if (!moduleListMap.containsKey(jsonFromAssetsVal[0])) {
                        try {
                            InputStream inputStream = assetManager.open(jsonFromAssetsVal[1]);
                            BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
                            ModuleList moduleList = gson.fromJson(inputReader,
                                    ModuleList.class);
                            moduleListMap.put(jsonFromAssetsVal[0], moduleList);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to read target " +
                                    jsonFromAssetsVal[1] +
                                    ": " +
                                    e.getMessage());
                        }
                    }
                }
            }
        }
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
                                                    boolean bustCache,
                                                    String blocksBaseUrl,
                                                    String version) {
        Response<JsonElement> moduleListResponse = null;
        try {

            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("x-api-key", xApiKey);

            if (bustCache) {
                StringBuilder urlWithCacheBuster = new StringBuilder(blocksBaseUrl);
                urlWithCacheBuster.append("?x=");
                urlWithCacheBuster.append(new Date().getTime());
                moduleListResponse =
                        appCMSAndroidModuleRest.get(urlWithCacheBuster.toString(), authTokenMap).execute();
            } else {
                moduleListResponse = appCMSAndroidModuleRest.get(blocksBaseUrl, authTokenMap).execute();
            }
            System.out.println("Retrieving module list from Network "+blocksBaseUrl);
            if (moduleListResponse != null &&
                    moduleListResponse.body() != null) {

                moduleDataMap.appCMSAndroidModule = gson.fromJson(moduleListResponse.body(),
                        new TypeToken<Map<String, ModuleList>>() {
                        }.getType());
                System.out.println("Retrieving module list from Network "+moduleDataMap.appCMSAndroidModule.size());
                moduleDataMap.loadedFromNetwork = true;
                new Thread(() -> {
                    deletePreviousFiles(getResourceFilenameWithJsonOnly(blocksBaseUrl));
                    writeModuleToFile(getResourceFilename(blocksBaseUrl, version), moduleDataMap.appCMSAndroidModule);
                }).run();
            }
        } catch (Exception e1) {
            //Log.e(TAG, "Failed to load block modules from file: " + e1.getMessage());
            try {
                JSONObject j1 = new JSONObject(moduleListResponse.body().toString());
                j1.remove("version");
                moduleDataMap.appCMSAndroidModule = gson.fromJson(j1.toString(),
                        new TypeToken<Map<String, ModuleList>>() {
                        }.getType());
                System.out.println("Retrieving module list from Network in the catch : " + moduleDataMap.appCMSAndroidModule.size());
            }catch(Exception e){

            }

        }
        return moduleDataMap;
    }

    private void readModuleListFromFile(String blocksBaseUrl,
                                        String version,
                                        boolean forceLoadFromNetwork,
                                        boolean bustCache,
                                        Action1<ModuleDataMap> readyAction) {
        Observable.fromCallable(() -> {
            ModuleDataMap moduleDataMap = new ModuleDataMap();
            moduleDataMap.loadedFromNetwork = false;
            if (forceLoadFromNetwork) {
                moduleDataMap = readModuleListFromNetwork(moduleDataMap,
                        bustCache,
                        blocksBaseUrl,
                        version);
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
                    System.out.println("Retrieving module list from file "+ moduleDataMap.appCMSAndroidModule);
                    long endTime = new Date().getTime();
                    Log.d(TAG, "End time: " + endTime);
                    Log.d(TAG, "Time elapsed: " + (endTime - startTime));
                    objectInputStream.close();
                    bufferedInputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    //Log.w(TAG, "Failed to load block modules from file: " + e.getMessage());
                    moduleDataMap = readModuleListFromNetwork(moduleDataMap,
                            bustCache,
                            blocksBaseUrl,
                            version);
                }
            }

            return moduleDataMap;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(throwable -> Observable.empty())
        .subscribe((result) -> Observable.just(result)
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe(readyAction));
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

    public static Map<String, ModuleList> jsonToMap(JSONObject json) throws JSONException {
        Map<String, ModuleList> retMap = new HashMap<String, ModuleList>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }


    public static Map<String, ModuleList> toMap(JSONObject object) throws JSONException {
        Map<String, ModuleList> map = new HashMap<String, ModuleList>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, (ModuleList)value);
        }
        return map;
    }
    public static List<ModuleList> toList(JSONArray array) throws JSONException {
        List<ModuleList> list = new ArrayList<ModuleList>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add((ModuleList)value);
        }
        return list;
    }

}
