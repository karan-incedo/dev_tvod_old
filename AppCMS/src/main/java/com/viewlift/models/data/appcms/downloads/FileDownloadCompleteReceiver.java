package com.viewlift.models.data.appcms.downloads;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by piyush.aggarwal on 9/5/2017.
 */

public class FileDownloadCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "FileDlReceiver";

    DownloadManager downloadManager;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        //Reference Id of the Download Completed
        long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        try {
            getDownloadedFile(referenceId);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void getDownloadedFile(long Image_DownloadId) {

        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(Image_DownloadId);
        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if (cursor != null && cursor.moveToFirst()) {
            downloadStatus(cursor, Image_DownloadId);
        }

    }

    private void downloadStatus(Cursor cursor, long DownloadId) {

        File mFile = null;
        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
//        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
//        String filename = cursor.getString(filenameIndex);

        String downloadFilePath = null;
        String downloadFileLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

        if (downloadFileLocalUri != null) {
            mFile = new File(Uri.parse(downloadFileLocalUri).getPath());
            downloadFilePath = mFile.getAbsolutePath();
            if (0 < downloadFilePath.length() && downloadFilePath.charAt(0) == '/') {
                downloadFilePath = downloadFilePath.substring(1);
            }
        }

        String filename = downloadFilePath;

        String statusText = "";
        String reasonText = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;

                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;

                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;

                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;

                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;

                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;

                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;

                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;

                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;

                    default:
                        break;
                }
                break;

            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;

                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;

                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;

                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;

                    default:
                        break;
                }
                break;

            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;

            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + downloadFileLocalUri;
                String extension = MimeTypeMap.getFileExtensionFromUrl(downloadFileLocalUri);
                if (extension.equals("mp4")) {
                    encryptTheFile(filename);
                }
                break;

            default:
                break;
        }
    }

    public void encryptTheFile(String filePath) {
        RandomAccessFile file = null;
        try {
            byte[] exoBitsData = new byte[10];
            file = new RandomAccessFile(filePath, "rw");

            //We are reading the first few bits and applying on bits
            file.read(exoBitsData, 0, 10);

            //Now we need to seek to 0th bit
            file.seek(0);

            for (int i = 0; i < exoBitsData.length; i++) {
                if (~exoBitsData[i] >= -128 && ~exoBitsData[i] <= 127)
                    exoBitsData[i] = (byte) ~exoBitsData[i];
            }

            file.write(exoBitsData);
        } catch (Exception e1) {
//            Log.e(TAG, "Failed to encrypt file: " + e1.getMessage());
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException e2) {
//                Log.e(TAG, "Failed to encrypt file: " + e2.getMessage());
            }
        }
    }
}
