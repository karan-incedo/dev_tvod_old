package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.FeatureDetail;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by viewlift on 7/21/17.
 */

public class SubscriptionMetaDataView extends LinearLayout {
    private static final String TAG = "SubsMetaDataView";

    private static int viewCreationPlanDetailsIndex = 0;

    private final Component component;
    private final Layout layout;
    private final ViewCreator viewCreator;
    private final Module moduleAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final int planDetailsIndex;
    private final Settings moduleSettings;
    private int devicesSupportedComponentIndex;
    private int devicesSupportedFeatureIndex;

    public SubscriptionMetaDataView(Context context,
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
        this.devicesSupportedComponentIndex = -1;
        this.devicesSupportedFeatureIndex = -1;
        viewCreationPlanDetailsIndex++;
        init();
    }

    public void init() {
        setOrientation(VERTICAL);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        if (planDetailsIndex >= 0) {
            List<FeatureDetail> featureDetails =
                    moduleAPI.getContentData()
                            .get(planDetailsIndex)
                            .getPlanDetails()
                            .get(0)
                            .getFeatureDetails();

            Component devicesSupportedComponent = null;
            for (int i = 0; i < component.getComponents().size(); i++) {
                AppCMSUIKeyType keyType = jsonValueKeyMap.get(component.getComponents().get(i).getKey());
                if (keyType == AppCMSUIKeyType.PAGE_PLANMETADATADEVICECOUNT_KEY) {
                    devicesSupportedComponent = component.getComponents().get(i);
                    devicesSupportedComponentIndex = i;
                }
            }

            for (int i = 0; i < featureDetails.size(); i++) {
                if (!TextUtils.isEmpty(featureDetails.get(i).getValueType()) &&
                        featureDetails.get(i).getValueType().equals("integer")) {
                    devicesSupportedFeatureIndex = i;
                } else {
                    FeatureDetail featureDetail = featureDetails.get(i);
                    createPlanDetails(featureDetail);
                }
            }

            if (devicesSupportedComponent != null &&
                    devicesSupportedComponentIndex > 0 &&
                    devicesSupportedFeatureIndex > 0) {
                int numDevicesSupported =
                        moduleAPI.getContentData()
                                .get(planDetailsIndex)
                                .getPlanDetails()
                                .get(0)
                                .getSupportedDevices()
                                .size();
                createDevicesSupportedComponent(devicesSupportedComponent, numDevicesSupported);
            }
        }
    }

    private void createPlanDetails(FeatureDetail featureDetail) {
        GridLayout planLayout = new GridLayout(getContext());
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        planLayout.setLayoutParams(layoutParams);
        planLayout.setOrientation(GridLayout.HORIZONTAL);
        planLayout.setColumnCount(2);
        int componentIndex = 0;
        if (component.getComponents() != null)  {
            for (componentIndex = 0;
                 componentIndex < component.getComponents().size();
                 componentIndex++) {
                if (componentIndex != devicesSupportedComponentIndex) {
                    Component subComponent = component.getComponents().get(componentIndex);
                    planLayout.addView(addChildComponent(subComponent,
                            featureDetail));
                }
            }
        }

        addView(planLayout);
    }

    private void createDevicesSupportedComponent(Component devicesSupportedComponent,
                                                 int numSupportedDevices) {
        GridLayout planLayout = new GridLayout(getContext());
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        planLayout.setLayoutParams(layoutParams);
        planLayout.setOrientation(GridLayout.HORIZONTAL);
        planLayout.setColumnCount(2);

        GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
        gridLayoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        ViewCreator.ComponentViewResult componentViewResult = viewCreator.getComponentViewResult();
        viewCreator.createComponentView(getContext(),
                devicesSupportedComponent,
                devicesSupportedComponent.getLayout(),
                moduleAPI,
                null,
                moduleSettings,
                jsonValueKeyMap,
                appCMSPresenter,
                false,
                "");
        if (componentViewResult.componentView instanceof TextView) {
            ((TextView) componentViewResult.componentView).setText("Device(s)");
            componentViewResult.componentView.setLayoutParams(gridLayoutParams);
            planLayout.addView(componentViewResult.componentView);
        }

        gridLayoutParams = new GridLayout.LayoutParams();
        gridLayoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        viewCreator.createComponentView(getContext(),
                devicesSupportedComponent,
                devicesSupportedComponent.getLayout(),
                moduleAPI,
                null,
                moduleSettings,
                jsonValueKeyMap,
                appCMSPresenter,
                false,
                "");
        if (componentViewResult.componentView instanceof TextView) {
            ((TextView) componentViewResult.componentView).setText(String.valueOf(numSupportedDevices));
            componentViewResult.componentView.setLayoutParams(gridLayoutParams);
            gridLayoutParams.setGravity(Gravity.END);
            ((TextView) componentViewResult.componentView)
                    .setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor()));
            planLayout.addView(componentViewResult.componentView);
        }

        addView(planLayout);
    }

    private View addChildComponent(Component subComponent,
                                   FeatureDetail featureDetail) {
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

            GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
            gridLayoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            switch (componentKeyType) {
                case PAGE_PLANMETADATATILE_KEY:
                    if (componentView instanceof  TextView) {
                        ((TextView) componentView).setText(featureDetail.getTextToDisplay());
                        componentView.setLayoutParams(gridLayoutParams);
                    }
                    break;

                case PAGE_PLANMETADDATAIMAGE_KEY:
                    if (componentView instanceof ImageView) {
                        if (!TextUtils.isEmpty(featureDetail.getValue()) &&
                                featureDetail.getValue().equalsIgnoreCase("true")) {
                            ((ImageView) componentView).setImageResource(R.drawable.tickicon);
                        } else {
                            ((ImageView) componentView).setImageResource(R.drawable.crossicon);
                        }
                        gridLayoutParams.setGravity(Gravity.END);
                        componentView.setLayoutParams(gridLayoutParams);
                    }
                    break;

                default:
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
}
