package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
public class ViewPlansMetaDataView extends LinearLayout {

    private static final String TAG = "ViewPlansMetaDataTAG_";

    private static int viewCreationPlanDetailsIndex = 0;

    private final Component component;

    @SuppressWarnings("FieldCanBeLocal, unused")
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
        LinearLayout planLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        planLayout.setLayoutParams(layoutParams);
        planLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (int featureDetailsIndex = 0; featureDetailsIndex < featureDetails.size();
             featureDetailsIndex++) {

            if (!TextUtils.isEmpty(featureDetails.get(featureDetailsIndex).getValueType())) {
                FeatureDetail featureDetail = featureDetails.get(featureDetailsIndex);
                int componentIndex;

                if (component.getComponents() != null) {
                    for (componentIndex = 0; componentIndex < component.getComponents().size();
                         componentIndex++) {
                        Component subComponent = component.getComponents().get(componentIndex);
                        planLayout.addView(addChildComponent(subComponent, featureDetail));
                    }
                }

                addCommaAfterEachFeature(featureDetails, planLayout, featureDetailsIndex);
            }
        }

        addView(planLayout);
    }

    private View addChildComponent(Component subComponent, FeatureDetail featureDetail) {
        ViewCreator.ComponentViewResult componentViewResult = viewCreator.getComponentViewResult();

        viewCreator.createComponentView(getContext(),
                subComponent,
                subComponent.getLayout(),
                moduleAPI,
                null,
                moduleSettings,
                jsonValueKeyMap,
                appCMSPresenter,
                false,
                "");

        View componentView = componentViewResult.componentView;
        if (componentView != null) {
            AppCMSUIKeyType componentKeyType = jsonValueKeyMap.get(subComponent.getKey());

            if (componentKeyType == null) {
                componentKeyType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }

            switch (componentKeyType) {
                case PAGE_PLANMETADATATITLE_KEY:
                    if (componentView instanceof TextView) {
                        componentView.setLayoutParams(new LinearLayout.LayoutParams(
                                LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        ((TextView) componentView).setText(featureDetail.getTextToDisplay());
                    }
                    break;

                default:
                    break;
            }

            Log.d(TAG, "Created child component: " +
                    planDetailsIndex +
                    " - " +
                    featureDetail.getTextToDisplay() +
                    " - " +
                    subComponent.getKey() +
                    " - " +
                    componentView.getClass().getName());
        }

        return componentView;
    }

    private void addCommaAfterEachFeature(List<FeatureDetail> featureDetails, LinearLayout planLayout,
                                          int featureDetailsIndex) {
        if (featureDetailsIndex < featureDetails.size() - 1) {
            TextView commaView = new TextView(getContext());
            commaView.setText(", ");
            commaView.setEllipsize(TextUtils.TruncateAt.END);
            commaView.setHorizontallyScrolling(false);
            commaView.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                    .getBrand().getCta().getPrimary().getTextColor()));
            planLayout.addView(commaView);
        }
    }
}
