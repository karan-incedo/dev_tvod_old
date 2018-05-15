package com.viewlift.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.Mpeg;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by sandeep.singh on 7/28/2017.
 */

public class AppCMSDownloadQualityAdapter extends AppCMSDownloadRadioAdapter<Mpeg> {
    protected List<Component> components;
    protected AppCMSPresenter appCMSPresenter;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;

    public AppCMSDownloadQualityAdapter(Context context, List<Mpeg> items, List<Component> components,
                                        AppCMSPresenter appCMSPresenter,
                                        Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        super(context, items);
        this.appCMSPresenter = appCMSPresenter;
        this.components = components;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.tintColor = appCMSPresenter.getBrandPrimaryCtaColor();

        if (appCMSPresenter.getUserDownloadQualityPref() != null) {
            for (Integer i = 0; i < items.size(); i++) {
                if (items.get(i).getRenditionValue().equals(appCMSPresenter
                        .getUserDownloadQualityPref())) {
                    this.downloadQualityPosition = i;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(AppCMSDownloadRadioAdapter.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        viewHolder.mText.setText(mItems.get(i).getRenditionValue());
        applyStyles(viewHolder);
    }

    private void applyStyles(AppCMSDownloadRadioAdapter.ViewHolder viewHolder) {
        for (Component component : components) {
            AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
            if (componentType == null) {
                componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }

            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
            if (componentKey == null) {
                componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }

            switch (componentType) {
                case PAGE_LABEL_KEY:
                case PAGE_TEXTVIEW_KEY:
                    int textColor = ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.colorAccent);

                    if (!TextUtils.isEmpty(component.getTextColor())) {
                        textColor = Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getTextColor()));
                    } else if (component.getStyles() != null) {
                        if (!TextUtils.isEmpty(component.getStyles().getColor())) {
                            textColor = Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getStyles().getColor()));
                        } else if (!TextUtils.isEmpty(component.getStyles().getTextColor())) {
                            textColor =
                                    Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getStyles().getTextColor()));
                        }
                    }
                    switch (componentKey) {
                        case PAGE_API_TITLE:
                            viewHolder.mText.setTextColor(textColor);
                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.mText.setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getBackgroundColor())));
                            }
                            if (!TextUtils.isEmpty(component.getFontFamily())) {
                                setTypeFace(viewHolder.itemView.getContext(),
                                        jsonValueKeyMap,
                                        component,
                                        viewHolder.mText);
                            }
                            if (component.getFontSize() != 0) {
                                viewHolder.mText.setTextSize(component.getFontSize());
                            }

                            break;

                        default:
                    }

                    break;
            }


        }

    }

    private String getColor(Context context, String color) {
        if (color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
        }
        return color;
    }

    private void setTypeFace(Context context,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                             Component component,
                             TextView textView) {
        if (jsonValueKeyMap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
            AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            Typeface face = null;
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_bold_ttf));
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_semibold_ttf));
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_extrabold_ttf));
                    break;
                default:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_regular_ttf));
            }
            textView.setTypeface(face);

            textView.setWidth(context.getResources().getDimensionPixelOffset(R.dimen.app_cms_download_quality_rendition_width));

        }
    }
}
