package com.viewlift.views.fragments;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.presenters.AppCMSPresenter;

/*
 * Created by ram.kailash on 10/10/2017.
 */

public class AppCMSTrayMenuDialogFragment extends DialogFragment implements View.OnClickListener {

    private AppCMSPresenter appCMSPresenter;
    private ContentDatum contentDatum;
    private boolean isAdded, isDownloaded,isPending;
    private TrayMenuClickListener trayMenuClickListener;

    public static AppCMSTrayMenuDialogFragment newInstance(boolean isAdded, ContentDatum contentDatum) {
        AppCMSTrayMenuDialogFragment appCMSTrayMenuDialogFragment = new AppCMSTrayMenuDialogFragment();
        appCMSTrayMenuDialogFragment.isAdded = isAdded;
        appCMSTrayMenuDialogFragment.contentDatum = contentDatum;
        return appCMSTrayMenuDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
    }

    public void setMoreClickListener(TrayMenuClickListener moreClickListener) {
        this.trayMenuClickListener = moreClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.more_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        Button addToWatchList = (Button) view.findViewById(R.id.moreDialogAddToWatchListBtn);
        Button downloadBtn = (Button) view.findViewById(R.id.moreDialogDownloadBtn);
        Button closeBtn = (Button) view.findViewById(R.id.moreDialogCloseBtn);

        addToWatchList.setText(isAdded ? "REMOVE TO WATCHLIST" : "ADD TO WATCHLIST");
        addToWatchList.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppBackgroundColor()));
        addToWatchList.setTextColor(Color.parseColor(appCMSPresenter.getAppTextColor()));

        addToWatchList.setText(isAdded ? appCMSPresenter.getCurrentActivity().getResources().getString(R.string.remove_from_watchlist) : appCMSPresenter.getCurrentActivity().getResources().getString(R.string.add_to_watchlist));
        addToWatchList.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                .getCta().getPrimary().getBackgroundColor()));
        addToWatchList.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                .getCta().getPrimary().getTextColor()));
        //isDownloaded = appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId());
        //downloadBtn.setVisibility(isDownloaded?View.GONE:View.VISIBLE);

        appCMSPresenter.getUserVideoDownloadStatus(contentDatum.getGist().getId(),
                videoDownloadStatus -> {
                    if (videoDownloadStatus != null) {
                        if (videoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_COMPLETED ||
                                videoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_SUCCESSFUL){
                            isDownloaded = true;
                        }
                        if (videoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_RUNNING ||
                                videoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_PAUSED ||
                                videoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_PENDING) {

                            isPending = true;
                        }

                    }
                },
                appCMSPresenter.getLoggedInUser());

        if (!isDownloaded && !isPending) {
            downloadBtn.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                    .getCta().getPrimary().getBackgroundColor()));
            downloadBtn.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                    .getCta().getPrimary().getTextColor()));
            downloadBtn.setOnClickListener(this);
        }else {
            downloadBtn.setBackgroundColor(Color.GRAY);
            downloadBtn.setText(isDownloaded?"Downloaded":"Downloading...");
            downloadBtn.setActivated(false);
            downloadBtn.setOnClickListener(null);
        }
        //downloadBtn.setVisibility(isDownloaded ? View.GONE : View.VISIBLE);
        //downloadBtn.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppBackgroundColor()));
        //downloadBtn.setTextColor(Color.parseColor(appCMSPresenter.getAppTextColor()));
        closeBtn.setTextColor(Color.parseColor(appCMSPresenter.getAppTextColor()));

        if (!appCMSPresenter.isDownloadable()){
            downloadBtn.setVisibility(View.GONE);
        }
        addToWatchList.setOnClickListener(this);



        GradientDrawable gd = new GradientDrawable();
        gd.setColor(appCMSPresenter.getGeneralBackgroundColor()); // Changes this drawbale to use a single color instead of a gradient
        gd.setStroke(5, appCMSPresenter.getGeneralTextColor());

        closeBtn.setTextColor(appCMSPresenter.getGeneralTextColor());
        closeBtn.setBackground(gd);
        closeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.moreDialogAddToWatchListBtn) {
            dismiss();
            if (trayMenuClickListener != null)
                trayMenuClickListener.addToWatchListClick(!isAdded, contentDatum);
        } else if (v.getId() == R.id.moreDialogDownloadBtn) {
            dismiss();
            if (trayMenuClickListener != null)
                trayMenuClickListener.downloadClick(contentDatum);
        } else {
            dismiss();
        }
    }

    public interface TrayMenuClickListener {

        void addToWatchListClick(boolean isAddedOrNot, ContentDatum contentDatum);

        void downloadClick(ContentDatum contentDatum);
    }
}
