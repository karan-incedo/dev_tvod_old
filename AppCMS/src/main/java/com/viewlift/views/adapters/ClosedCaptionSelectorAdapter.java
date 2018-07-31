package com.viewlift.views.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.ViewGroup;

import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.ViewCreator;

import java.io.Serializable;
import java.util.List;

public class ClosedCaptionSelectorAdapter extends AppCMSDownloadRadioAdapter<ClosedCaptions>  {
    List<ClosedCaptions> closedCaptionsList;
    int selectedIndex;
    AppCMSPresenter appCMSPresenter;

    public ClosedCaptionSelectorAdapter(Context context,
                                        AppCMSPresenter appCMSPresenter,
                                        List<ClosedCaptions> items) {
        super(context, items, appCMSPresenter);
        this.appCMSPresenter = appCMSPresenter;
        this.closedCaptionsList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = super.onCreateViewHolder(viewGroup, i);

        viewHolder.getmText().setTextColor(appCMSPresenter.getBrandPrimaryCtaColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (viewHolder.getmRadio().getButtonDrawable() != null) {
                viewHolder.getmRadio().getButtonDrawable().setColorFilter(Color.parseColor(
                        ViewCreator.getColor(viewGroup.getContext(),
                                appCMSPresenter.getAppCtaBackgroundColor())),
                        PorterDuff.Mode.MULTIPLY);
            }
        } else {
            int switchOnColor = Color.parseColor(
                    ViewCreator.getColor(viewGroup.getContext(),
                            appCMSPresenter.getAppCtaBackgroundColor()));
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

        ClosedCaptions closedCaptions = closedCaptionsList.get(i);
        viewHolder.getmText().setText(closedCaptions.getLanguage());
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

    @Override
    public int getItemCount() {
        return closedCaptionsList.size();
    }
}