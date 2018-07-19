package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.views.customviews.ViewCreatorMultiLineLayoutListener;

import java.util.Map;

/**
 * Created by anas.azeem on 2/1/2018.
 * Owned by ViewLift, NYC
 */

public class TVCreditBlocksView extends RelativeLayout {
    private final String fontFamilyKey;
    private final String fontFamilyKeyType;
    private final String fontFamilyValue;
    private final String fontFamilyValueType;
    private final String directorListTitle;
    private final String directorList;
    private final String starringListTitle;
    private final String starringList;
    private final int textColor;
    private final int moreBackgroundColor;
    private final int moreForegroundColor;
    private final float fontsizeKey;
    private final float fontsizeValue;

    private TextView directorListTitleView;
    private TextView directorListView;
    private TextView starringListTitleView;
    private TextView starringListView;

    public TVCreditBlocksView(Context context,
                            Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                            String fontFamilyKey,
                            String fontFamilyKeyType,
                            String fontFamilyValue,
                            String fontFamilyValueType,
                            String directorListTitle,
                            String directorList,
                            String starringListTitle,
                            String starringList,
                            int textColor,
                            int moreBackgroundColor,
                            int moreForegroundColor,
                            float fontsizeKey,
                            float fontsizeValue) {
        super(context);
        this.fontFamilyKey = fontFamilyKey;
        this.fontFamilyKeyType = fontFamilyKeyType;
        this.fontFamilyValue = fontFamilyValue;
        this.fontFamilyValueType = fontFamilyValueType;
        this.directorListTitle = directorListTitle;
        this.directorList = directorList;
        this.starringList = starringList;
        this.starringListTitle = starringListTitle;
        this.textColor = textColor;
        this.moreBackgroundColor = moreBackgroundColor;
        this.moreForegroundColor = moreForegroundColor;
        this.fontsizeKey = fontsizeKey;
        this.fontsizeValue = fontsizeValue;
        init(context, jsonValueKeyMap);
    }

    private void init(Context context, Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        Typeface keyTypeFace = getTypeFace(context, jsonValueKeyMap, fontFamilyKey, fontFamilyKeyType);
        Typeface valueTypeFace = getTypeFace(context, jsonValueKeyMap, fontFamilyValue, fontFamilyValueType);

        int directorListTitleViewId = View.generateViewId();
        int directorListViewId = View.generateViewId();
        int starringListTitleViewId = View.generateViewId();
        int starringListViewId = View.generateViewId();

        directorListTitleView = new TextView(getContext());
        directorListTitleView.setTypeface(keyTypeFace);
        directorListTitleView.setTextColor(textColor);
        if (fontsizeKey != -1.0f) {
            directorListTitleView.setTextSize(fontsizeKey);
        }
        LayoutParams directorListTitleLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        directorListTitleLayoutParams.addRule(ALIGN_PARENT_START);
        directorListTitleView.setLayoutParams(directorListTitleLayoutParams);
        directorListTitleView.setSingleLine(true);
        directorListTitleView.setId(directorListTitleViewId);
        addView(directorListTitleView);

        directorListView = new TextView(getContext());
        directorListView.setTypeface(valueTypeFace);
        directorListView.setTextColor(textColor);
        directorListView.setPadding(33,
                0,
                0,
                0);
        if (fontsizeValue != -1.0f) {
            directorListView.setTextSize(fontsizeValue);
        }
        LayoutParams directorListLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        directorListLayoutParams.addRule(ALIGN_PARENT_END);
        directorListLayoutParams.addRule(END_OF, directorListTitleViewId);
        directorListLayoutParams.addRule(ALIGN_BASELINE, directorListTitleViewId);
        directorListView.setLayoutParams(directorListLayoutParams);
        directorListView.setId(directorListViewId);
        addView(directorListView);

        starringListTitleView = new TextView(getContext());
        starringListTitleView.setTypeface(keyTypeFace);
        starringListTitleView.setTextColor(textColor);
        if (fontsizeKey != -1.0f) {
            starringListTitleView.setTextSize(fontsizeKey);
        }
        LayoutParams starringListTitleLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        starringListTitleLayoutParams.addRule(ALIGN_PARENT_START);
        starringListTitleLayoutParams.addRule(BELOW, directorListTitleViewId);
        starringListTitleView.setLayoutParams(starringListTitleLayoutParams);
        starringListTitleView.setSingleLine(true);
        starringListTitleView.setId(starringListTitleViewId);
        addView(starringListTitleView);

        starringListView = new TextView(getContext());
        starringListView.setTypeface(valueTypeFace);
        starringListView.setTextColor(textColor);
        starringListView.setPadding(33,
                4,
                0,
                0);
        if (fontsizeValue != -1.0f) {
            starringListView.setTextSize(fontsizeValue);
        }

        LayoutParams starringListLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        starringListLayoutParams.addRule(ALIGN_PARENT_END);
        starringListLayoutParams.addRule(END_OF, starringListTitleViewId);
        starringListLayoutParams.addRule(ALIGN_BASELINE, starringListTitleViewId);
        starringListView.setLayoutParams(starringListLayoutParams);
        starringListView.setId(starringListViewId);
        addView(starringListView);

        updateText(directorListTitle, directorList, starringListTitle, starringList);
    }

    public void updateText(String directorListTitle,
                           String directorList,
                           String starringListTitle,
                           String starringList) {
        if (!TextUtils.isEmpty(directorListTitle) && !TextUtils.isEmpty(directorList) &&
                directorListTitleView != null &&
                directorListView != null) {
            directorListTitleView.setText(directorListTitle);
            directorListView.setText(directorList);

            ViewTreeObserver directorListVto = directorListView.getViewTreeObserver();
            directorListVto.addOnGlobalLayoutListener(new ViewCreatorMultiLineLayoutListener(directorListView,
                    null,
                    directorList,
                    null,
                    true,
                    moreBackgroundColor,
                    moreForegroundColor,
                    false));
        }

        if (!TextUtils.isEmpty(starringListTitle) && !TextUtils.isEmpty(starringList) &&
                starringListTitleView != null &&
                starringListView != null) {
            starringListTitleView.setText(starringListTitle);
            starringListView.setText(starringList);

            ViewTreeObserver starringListVto = starringListView.getViewTreeObserver();
            starringListVto.addOnGlobalLayoutListener(new ViewCreatorMultiLineLayoutListener(starringListView,
                    null,
                    starringList,
                    null,
                    true,
                    moreBackgroundColor,
                    moreForegroundColor,
                    false));
        }
    }

    private Typeface getTypeFace(Context context,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 String fontFamily,
                                 String fontWeightKey) {

        if (jsonValueKeyMap.get(fontFamily) != null) {
            String fontName;
            switch (jsonValueKeyMap.get(fontFamily)) {
                case PAGE_TEXT_OPENSANS_FONTFAMILY_KEY:
                    fontName = context.getString(R.string.app_cms_page_font_family_key);
                    break;
                case PAGE_TEXT_LATO_FONTFAMILY_KEY:
                    fontName = context.getString(R.string.app_cms_page_font_lato_family_key);
                    break;
                default:
                    fontName = context.getString(R.string.app_cms_page_font_family_key);
                    break;

            }

            AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(fontWeightKey);
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            Typeface face;
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_bold_ttf, fontName));
                    break;

                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_semibold_ttf, fontName));
                    break;

                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_extrabold_ttf, fontName));
                    break;
                case PAGE_TEXT_BLACK_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_black_ttf, fontName));
                    break;
                case PAGE_TEXT_BLACK_ITALIC_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_black_italic_ttf, fontName));
                    break;
                case PAGE_TEXT_HAIRLINE_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_hairline_ttf, fontName));
                    break;
                case PAGE_TEXT_HAIRLINE_ITALIC_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_hairline_italic_ttf, fontName));
                    break;
                case PAGE_TEXT_HEAVY_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_heavy_ttf, fontName));
                    break;
                case PAGE_TEXT_HEAVY_ITALIC_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_heavy_italic_ttf, fontName));
                    break;
                case PAGE_TEXT_LIGHT_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_light_ttf, fontName));
                    break;
                case PAGE_TEXT_LIGHT_ITALIC_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_light_italic_ttf, fontName));
                    break;
                case PAGE_TEXT_MEDIUM_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_medium_ttf, fontName));
                    break;
                case PAGE_TEXT_MEDIUM_ITALIC_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_medium_italic_ttf, fontName));
                    break;
                case PAGE_TEXT_THIN_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_thin_ttf, fontName));
                    break;
                case PAGE_TEXT_THIN_ITALIC_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_thin_italic_ttf, fontName));
                    break;
                case PAGE_TEXT_SEMIBOLD_ITALIC_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_semibold_italic_ttf, fontName));
                    break;
                default:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.font_regular_ttf, fontName));
                    break;
            }
            return face;
        }
        return null;
    }
}

