package com.viewlift.views.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.ViewGroup;

import com.viewlift.models.data.playersettings.HLSStreamingQuality;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;

public class HLSStreamingQualitySelectorAdapter extends AppCMSDownloadRadioAdapter<HLSStreamingQuality> {
    List<HLSStreamingQuality> availableStreamingQualities;
    int selectedIndex;
    AppCMSPresenter appCMSPresenter;

    public HLSStreamingQualitySelectorAdapter(Context context,
                                       AppCMSPresenter appCMSPresenter,
                                       List<HLSStreamingQuality> items) {
        super(context, items,appCMSPresenter);
        this.appCMSPresenter = appCMSPresenter;
        this.availableStreamingQualities = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = super.onCreateViewHolder(viewGroup, i);

        viewHolder.getmText().setTextColor(appCMSPresenter.getBrandPrimaryCtaColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (viewHolder.getmRadio().getButtonDrawable() != null) {
                viewHolder.getmRadio().getButtonDrawable().setColorFilter(appCMSPresenter.getBrandPrimaryCtaColor(),
                        PorterDuff.Mode.MULTIPLY);
            }
        } else {
            int switchOnColor = appCMSPresenter.getBrandPrimaryCtaColor();
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
        viewHolder.getmText().setText(availableStreamingQualities.get(i).getValue());
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

    public int getDownloadQualityPosition() {
        return downloadQualityPosition;
    }
}