package com.viewlift.views.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.ViewGroup;

import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;

public class StreamingQualitySelectorAdapter extends AppCMSDownloadRadioAdapter<String>  {
    List<String> availableStreamingQualities;
    int selectedIndex;
    AppCMSPresenter appCMSPresenter;

    public StreamingQualitySelectorAdapter(Context context,
                                           AppCMSPresenter appCMSPresenter,
                                           List<String> items) {
        super(context, items,appCMSPresenter);
        this.appCMSPresenter = appCMSPresenter;
        this.availableStreamingQualities = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = super.onCreateViewHolder(viewGroup, i);

        if (appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) {
            viewHolder.getmText().setTextColor(appCMSPresenter.getBrandPrimaryCtaColor());
        }else {
            viewHolder.getmText().setTextColor(Color.WHITE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (viewHolder.getmRadio().getButtonDrawable() != null) {
                viewHolder.getmRadio().getButtonDrawable().setColorFilter(
                        appCMSPresenter.getBrandPrimaryCtaColor(),PorterDuff.Mode.MULTIPLY);
/*
                viewHolder.getmRadio().getButtonDrawable().setColorFilter(Color.parseColor(
                        ViewCreator.getColor(viewGroup.getContext(),
                                        appCMSPresenter.getAppCtaBackgroundColor())),
                        PorterDuff.Mode.MULTIPLY);*/
            }
        } else {
            int switchOnColor = appCMSPresenter.getBrandPrimaryCtaColor();
            /*int switchOnColor = Color.parseColor(
                    ViewCreator.getColor(viewGroup.getContext(),
                            appCMSPresenter.getAppCtaBackgroundColor()));*/
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    }, new int[]{
                    switchOnColor,
                    switchOnColor
            });

            viewHolder.getmRadio().setButtonTintList(colorStateList);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AppCMSDownloadRadioAdapter.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        viewHolder.getmText().setText(availableStreamingQualities.get(i));
        if (selectedIndex == i) {
            viewHolder.getmRadio().setChecked(true);
            viewHolder.getmRadio().requestFocus();
        } else {
            viewHolder.getmRadio().setChecked(false);
        }
        viewHolder.getmRadio().invalidate();
    }

    @Override
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public int getDownloadQualityPosition() {
        return downloadQualityPosition;
    }
}
