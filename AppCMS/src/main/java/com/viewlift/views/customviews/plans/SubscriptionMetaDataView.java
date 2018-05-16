package com.viewlift.views.customviews.plans;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.FeatureDetail;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.ViewCreator;

import java.util.List;
import java.util.Map;

/**
 * Created by viewlift on 7/21/17.
 */

@SuppressLint("ViewConstructor")
public class SubscriptionMetaDataView extends LinearLayout {
    private static final String TAG = "SubsMetaDataView";

    private final Component component;
    private final Layout layout;
    private final ViewCreator viewCreator;
    private final Module moduleAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final Settings moduleSettings;
    Context context;
    private int devicesSupportedComponentIndex;
    private int devicesSupportedFeatureIndex;
    private ContentDatum planData;
    private AppCMSAndroidModules appCMSAndroidModules;

    public SubscriptionMetaDataView(Context context,
                                    Component component,
                                    Layout layout,
                                    ViewCreator viewCreator,
                                    Module moduleAPI,
                                    Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                    AppCMSPresenter appCMSPresenter,
                                    Settings moduleSettings,
                                    AppCMSAndroidModules appCMSAndroidModules) {
        super(context);
        this.context = context;
        this.component = component;
        this.layout = layout;
        this.viewCreator = viewCreator;
        this.moduleAPI = moduleAPI;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.moduleSettings = moduleSettings;
        this.devicesSupportedComponentIndex = -1;
        this.devicesSupportedFeatureIndex = -1;
        this.appCMSAndroidModules = appCMSAndroidModules;
        init();
    }

    public void init() {
        setOrientation(VERTICAL);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
    }

    public void setData(ContentDatum planData) {
        this.planData = null;
        this.planData = planData;
        initViews();
    }

    public void initViews() {
        if (planData != null &&
                planData.getPlanDetails() != null &&
                planData.getPlanDetails().size() > 0 &&
                planData.getPlanDetails().get(0) != null &&
                planData.getPlanDetails().get(0).getFeatureDetails() != null) {
            List<FeatureDetail> featureDetails =
                    planData.getPlanDetails()
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

            /**
             *
             * Fix of MSEAN-1433
             * Whenever it cheld views wer more then 0 it created duplicate details view at
             * plan screen. #removeAllViews() help for clearing duplicate views.
             */
            if (getChildAt(0)!=null &&
                    getChildAt(0)instanceof  GridLayout) {
               removeAllViews();
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
                int numDevicesSupported = -1;
                if (!TextUtils.isEmpty(planData.getPlanDetails()
                        .get(0)
                        .getFeatureDetails()
                        .get(devicesSupportedFeatureIndex)
                        .getValue())) {
                    numDevicesSupported = Integer.valueOf(planData.getPlanDetails()
                            .get(0)
                            .getFeatureDetails()
                            .get(devicesSupportedFeatureIndex)
                            .getValue());
                }
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
        if (component.getComponents() != null) {
            for (componentIndex = 0;
                 componentIndex < component.getComponents().size();
                 componentIndex++) {
                if (componentIndex != devicesSupportedComponentIndex) {
                    Component subComponent = component.getComponents().get(componentIndex);
                    planLayout.addView(addChildComponent(subComponent,
                            featureDetail,
                            appCMSAndroidModules));
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
                appCMSAndroidModules,
                null,
                moduleSettings,
                jsonValueKeyMap,
                appCMSPresenter,
                false,
                "",
                component.getId());
        if (componentViewResult.componentView instanceof TextView) {
            ((TextView) componentViewResult.componentView).setText("Device(s)");
            componentViewResult.componentView.setLayoutParams(gridLayoutParams);
            planLayout.addView(componentViewResult.componentView);
        }

        if (numSupportedDevices != -1) {
            gridLayoutParams = new GridLayout.LayoutParams();
            gridLayoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            viewCreator.createComponentView(getContext(),
                    devicesSupportedComponent,
                    devicesSupportedComponent.getLayout(),
                    moduleAPI,
                    appCMSAndroidModules,
                    null,
                    moduleSettings,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    false,
                    "",
                    component.getId());
            if (componentViewResult.componentView instanceof TextView) {
                ((TextView) componentViewResult.componentView).setText(String.valueOf(numSupportedDevices));
                componentViewResult.componentView.setLayoutParams(gridLayoutParams);
                gridLayoutParams.setGravity(Gravity.END);
                gridLayoutParams.setMarginEnd((int) getContext().getResources().getDimension(R.dimen.app_cms_planmetapage_end_margin));
                ((TextView) componentViewResult.componentView)
                        .setTextColor(appCMSPresenter.getBrandPrimaryCtaColor());

                planLayout.addView(componentViewResult.componentView);
            }
        }

        addView(planLayout);
    }

    private View addChildComponent(Component subComponent,
                                   FeatureDetail featureDetail,
                                   AppCMSAndroidModules appCMSAndroidModules) {
        ViewCreator.ComponentViewResult componentViewResult = viewCreator.getComponentViewResult();
        viewCreator.createComponentView(getContext(),
                subComponent,
                subComponent.getLayout(),
                moduleAPI,
                appCMSAndroidModules,
                null,
                moduleSettings,
                jsonValueKeyMap,
                appCMSPresenter,
                false,
                "",
                component.getId());
        View componentView = componentViewResult.componentView;
        if (componentView != null) {
            AppCMSUIKeyType componentKeyType = jsonValueKeyMap.get(subComponent.getKey());

            if (componentKeyType == null) {
                componentKeyType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }

            GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
//            gridLayoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            switch (componentKeyType) {
                case PAGE_PLANMETADATATITLE_KEY:
                    if (componentView instanceof TextView) {
                        componentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        ((TextView) componentView).setText(featureDetail.getTextToDisplay());

                        if (!TextUtils.isEmpty(featureDetail.getValue()) &&
                                featureDetail.getValue().equalsIgnoreCase("true")) {
                            Drawable rightImage = ContextCompat.getDrawable(context, R.drawable.tickicon);
                            rightImage.setBounds(0, 0, rightImage.getIntrinsicWidth(), rightImage.getIntrinsicHeight());
                            ((TextView) componentView).setCompoundDrawables(null, null, rightImage, null);
                        } else {
                            Drawable rightImage = ContextCompat.getDrawable(context, R.drawable.crossicon);
                            rightImage.setBounds(0, 0, rightImage.getIntrinsicWidth(), rightImage.getIntrinsicHeight());
                            ((TextView) componentView).setCompoundDrawables(null, null, rightImage, null);
                        }

//                        ((TextView) componentView).setEllipsize(TextUtils.TruncateAt.END);
//                        componentView.setLayoutParams(gridLayoutParams);
                    }
                    break;


//                case PAGE_PLANMETADDATAIMAGE_KEY:
//                    if (componentView instanceof ImageView) {
//                        if (!TextUtils.isEmpty(featureDetail.getValue()) &&
//                                featureDetail.getValue().equalsIgnoreCase("true")) {
//                            ((ImageView) componentView).setImageResource(R.drawable.tickicon);
//                        } else {
//                            ((ImageView) componentView).setImageResource(R.drawable.crossicon);
//                        }
////                        gridLayoutParams.rowSpec=GridLayout.spec(2);
//                        gridLayoutParams.columnSpec=GridLayout.spec(0);
//                        gridLayoutParams.setMargins(0, 0, 16, 0);
//                        gridLayoutParams.setGravity(Gravity.END);
//                        componentView.setLayoutParams(gridLayoutParams);
//                    }
//                    break;

                default:
                    break;
            }

            //Log.d(TAG, "Created child component: " +
//                    featureDetail.getTextToDisplay() +
//                    " - " +
//                    subComponent.getKey() +
//                    " - " +
//                    componentView.getClass().getName());
        }
        return componentView;
    }
}
