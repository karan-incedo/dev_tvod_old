package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.FeatureDetail;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;
import java.util.Map;

@SuppressLint("ViewConstructor")
@SuppressWarnings("FieldCanBeLocal, unused")
public class ViewPlansMetaDataView extends LinearLayout {

    private static int viewCreationPlanDetailsIndex = 0;

    private final Component component;
    private final Layout layout;

    private final ViewCreator viewCreator;
    private final Module moduleAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final int planDetailsIndex;
    private final Settings moduleSettings;
    private ContentDatum planData;

    public ViewPlansMetaDataView(Context context,
                                 Component component,
                                 Layout layout,
                                 ViewCreator viewCreator,
                                 Module moduleAPI,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter,
                                 Settings moduleSettings) {
        super(context);
        this.component = component;
        this.layout = layout;
        this.viewCreator = viewCreator;
        this.moduleAPI = moduleAPI;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.moduleSettings = moduleSettings;
        if (moduleAPI.getContentData() != null) {
            planDetailsIndex = viewCreationPlanDetailsIndex % moduleAPI.getContentData().size();
        } else {
            planDetailsIndex = -1;
        }

        viewCreationPlanDetailsIndex++;
        init();
    }

    public void init() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
    }

    public void setData(ContentDatum planData) {
        this.planData = planData;
        initViews();
    }

    private void initViews() {
        if (planData != null &&
                planData.getPlanDetails() != null &&
                planData.getPlanDetails().size() > 0 &&
                planData.getPlanDetails().get(0) != null &&
                planData.getPlanDetails().get(0).getFeatureDetails() != null) {
            List<FeatureDetail> featureDetails =
                    planData.getPlanDetails()
                            .get(0)
                            .getFeatureDetails();

            createPlanDetails(featureDetails);
        }
    }

    private void createPlanDetails(List<FeatureDetail> featureDetails) {
        removeAllViews();
        LinearLayout planLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        planLayout.setLayoutParams(layoutParams);
        planLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView featureDetailText = getCommaSeparatedFeatureDetail(featureDetails);

        planLayout.addView(featureDetailText);

        addView(planLayout);
    }

    @NonNull
    private TextView getCommaSeparatedFeatureDetail(List<FeatureDetail> featureDetails) {
        TextView featureDetailText = new TextView(getContext());
        featureDetailText.setMaxLines(2);
        featureDetailText.setEllipsize(TextUtils.TruncateAt.END);
        featureDetailText.setTextColor(Color.parseColor(appCMSPresenter.getAppTextColor()));

        for (int featureDetailsIndex = 0; featureDetailsIndex < featureDetails.size();
             featureDetailsIndex++) {

            if (!TextUtils.isEmpty(featureDetails.get(featureDetailsIndex).getValueType())) {
                FeatureDetail featureDetail = featureDetails.get(featureDetailsIndex);
                if (!TextUtils.isEmpty(featureDetail.getValue()) &&
                        "true".equalsIgnoreCase(featureDetail.getValue())) {
                    featureDetailText.append(featureDetail.getTextToDisplay());

                    if (featureDetailsIndex < featureDetails.size() - 1) {
                        featureDetailText.append(", ");
                    }
                }
            }
        }
        return featureDetailText;
    }
}
