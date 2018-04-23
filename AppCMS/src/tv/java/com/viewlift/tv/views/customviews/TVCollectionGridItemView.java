package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import static com.viewlift.tv.utility.Utils.getItemViewHeight;
import static com.viewlift.tv.utility.Utils.getItemViewWidth;
import static com.viewlift.tv.utility.Utils.getViewHeight;
import static com.viewlift.tv.utility.Utils.getViewWidth;

/**
 * Created by anas.azeem on 9/8/2017.
 * Owned by ViewLift, NYC
 */

public class TVCollectionGridItemView extends TVBaseView {
    private static final String TAG = "CollectionItemView";

    private final Layout parentLayout;
    private final boolean userParentLayout;
    private final Component component;
    private final String borderColor;
    protected int defaultWidth;
    protected int defaultHeight;
    private List<TVCollectionGridItemView.ItemContainer> childItems;
    private List<View> viewsToUpdateOnClickEvent;
    private boolean selectable;
    private CardView childrenContainer;
    private static int mPosition = 0;

    @Inject
    public TVCollectionGridItemView(Context context,
                                    Layout parentLayout,
                                    boolean useParentLayout,
                                    Component component,
                                    int defaultWidth,
                                    int defaultHeight,
                                    String borderColor) {
        super(context);
        this.parentLayout = parentLayout;
        this.userParentLayout = useParentLayout;
        this.component = component;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.viewsToUpdateOnClickEvent = new ArrayList<>();
        this.borderColor = borderColor;
        init();
    }

    @Override
    public void init() {
        int width = (int) getItemViewWidth(getContext(),
                component.getLayout(),
                defaultWidth);
        int height = (int) getItemViewHeight(getContext(),
                component.getLayout(),
                defaultHeight);

        FrameLayout.LayoutParams layoutParams;
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(width, height);
        /*marginLayoutParams.setMargins(horizontalMargin,
                verticalMargin,
                horizontalMargin,
                verticalMargin);*/
        layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        setLayoutParams(layoutParams);
        childItems = new ArrayList<>();
        if (component.getComponents() != null) {
            initializeComponentHasViewList(component.getComponents().size());
        }
    }

    @Override
    protected ViewGroup createChildrenContainer() {
        childrenContainer = new CardView(getContext());
        CardView.LayoutParams childContainerLayoutParams =
                new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        childrenContainer.setBackgroundResource(android.R.color.transparent);
        addView(childrenContainer);
        return childrenContainer;
    }

    public void addChild(TVCollectionGridItemView.ItemContainer itemContainer) {
        if (childrenContainer == null) {
            createChildrenContainer();
        }
        childItems.add(itemContainer);
        childrenContainer.addView(itemContainer.childView);
    }


    public View getChild(int index) {
        if (0 <= index && index < childItems.size()) {
            return childItems.get(index).childView;
        }
        return null;
    }

    @Override
    protected Component getChildComponent(int index) {
        if (component.getComponents() != null &&
                0 <= index &&
                index < component.getComponents().size()) {
            return component.getComponents().get(index);
        }
        return null;
    }

    @Override
    public Layout getLayout() {
        return component.getLayout();
    }

    public int getNumberOfChildren() {
        return childItems.size();
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void bindChild(Context context,
                          View view,
                          final ContentDatum data,
                          Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                          final OnClickHandler onClickHandler,
                          final AppCMSUIKeyType viewTypeKey,
                          int position) {
        AppCMSPresenter appCMSPresenter =
                ((AppCMSApplication) context.getApplicationContext())
                        .getAppCMSPresenterComponent().appCMSPresenter();
        final Component childComponent = matchComponentToView(view);
        if (childComponent != null) {
            boolean bringToFront = true;
            AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());
            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());
            if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY) {
                    int childViewWidth = (int) getViewWidth(getContext(),
                            childComponent.getLayout(),
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    int childViewHeight = (int) getViewHeight(getContext(),
                            childComponent.getLayout(),
                            getViewHeight(getContext(),
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (userParentLayout) {
                        childViewWidth = (int) getViewWidth(getContext(),
                                parentLayout,
                                defaultWidth);
                        childViewHeight = (int) getViewHeight(getContext(),
                                parentLayout,
                                defaultHeight);
                    }
                    if (!TextUtils.isEmpty(data.getGist().getVideoImageUrl())) {
                        String imageUrl =
                                context.getString(R.string.app_cms_image_with_resize_query,
                                        data.getGist().getVideoImageUrl(),
                                        childViewWidth,
                                        childViewHeight);
                        //Log.d(TAG, "Loading image: " + imageUrl);
                        Glide.with(context)
                                .load(imageUrl)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .placeholder(R.drawable.video_image_placeholder)
                                        .error(ContextCompat.getDrawable(context, R.drawable.video_image_placeholder)))
                                .into((ImageView) view);

                    }
                    bringToFront = false;
                    view.setFocusable(true);
                    view.setOnClickListener(v -> {
                        appCMSPresenter.showLoadingDialog(true);
                        onClickHandler.click(
                                TVCollectionGridItemView.this,
                                childComponent,
                                data);
                        view.setClickable(false);
                        new android.os.Handler().postDelayed(() -> view.setClickable(true), 3000);
                    });

                    final boolean[] clickable = {true};
                    view.setOnKeyListener((v, keyCode, event) -> {
                        if(event.getAction() == KeyEvent.ACTION_DOWN
                                && keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                                && clickable[0]) {
                            appCMSPresenter.showLoadingDialog(true);
                            onClickHandler.play(
                                    childComponent,
                                    data);
                            clickable[0] = false;
                            new android.os.Handler().postDelayed(() -> clickable[0] = true, 3000);
                            return true;
                        } else if(event.getAction() == KeyEvent.ACTION_DOWN
                                && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            ((AppCmsHomeActivity) context).findViewById(R.id.appcms_removeall).setFocusable(false);
                        }else if(event.getAction() == KeyEvent.ACTION_DOWN
                                && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            ((AppCmsHomeActivity) context).findViewById(R.id.appcms_removeall).setFocusable(true);
                        }
                        return false;
                    });
                    view.setBackground(Utils.getTrayBorder(context, borderColor, component));
                    view.setPadding(1, 3, 1, 3);
                }else if(componentKey == AppCMSUIKeyType.PAGE_ICON_IMAGE_KEY){
                    int childViewWidth = (int) getViewWidth(getContext(),
                            childComponent.getLayout(),
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    int childViewHeight = (int) getViewHeight(getContext(),
                            childComponent.getLayout(),
                            getViewHeight(getContext(),
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (!TextUtils.isEmpty(data.getGist().getVideoImageUrl())) {
                        String imageUrl =
                                context.getString(R.string.app_cms_image_with_resize_query,
                                        data.getGist().getVideoImageUrl(),
                                        childViewWidth,
                                        childViewHeight);
                      /*  Log.d(TAG, "Loading image Title: " + data.getGist().getTitle());
                        Log.d(TAG, "Loading image: " + imageUrl);*/
                        Glide.with(context)
                                .load(imageUrl)
                                .apply(new RequestOptions().override(childViewWidth, childViewHeight))
//                                .centerCrop()
                                .into((ImageView) view);

                        bringToFront = false;
                        view.setFocusable(true);
                        view.setBackground(Utils.getMenuSelector(context, appCMSPresenter.getAppCtaBackgroundColor(),
                                appCMSPresenter.getAppCMSMain().getBrand().getCta().getSecondary().getBorder().getColor()));
                       // view.setBackgroundResource(R.drawable.st_menu_color_selector);
                        view.setOnClickListener(v ->
                                {
                           // Toast.makeText(context, "Clicked on " + data.getGist().getTitle(), Toast.LENGTH_SHORT).show();
                            appCMSPresenter.showLoadingDialog(true);

                             appCMSPresenter.navigateToTVPage(
                                    data.getGist().getId(),
                                     data.getGist().getTitle(),
                                    data.getGist().getPermalink(),
                                    false,
                                    Uri.EMPTY,
                                    true,
                                   false,
                                    false);
                             mPosition = position;
                            new android.os.Handler().postDelayed(() -> view.setClickable(true), 3000);
                        });

                    }
                    if (position == mPosition)
                        view.requestFocus();
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY) {
                    ((TextView) view).setText("");
                    view.setOnClickListener(v -> onClickHandler.click(
                            TVCollectionGridItemView.this,
                            childComponent,
                            data));
                    view.setFocusable(false);
                } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DELETE_ITEM_BUTTON) {
                    view.setNextFocusUpId(R.id.appcms_removeall);
                    view.setOnClickListener(v -> onClickHandler.delete(childComponent, data));
                    view.setFocusable(true);
                }

            } else if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_ICON_LABEL_KEY) {
                    if (childComponent.getNumberOfLines() != 0) {
                        ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                    }
                    ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    setTypeFace(appCMSPresenter,context, jsonValueKeyMap, childComponent, ((TextView) view));
                    view.setFocusable(false);
                    String textCase = childComponent.getTextCase();
                    if(textCase != null && !TextUtils.isEmpty(data.getGist().getTitle())){
                        String title = data.getGist().getTitle();
                        if(textCase.equalsIgnoreCase(context.getResources().getString(R.string.text_case_caps))){
                            title = title.toUpperCase();
                        }else if(textCase.equalsIgnoreCase(context.getResources().getString(R.string.text_case_small))){
                            title = title.toLowerCase();
                        }else if(textCase.equalsIgnoreCase(context.getResources().getString(R.string.text_case_sentence))){
                            String text  = Utils.convertStringIntoCamelCase(title);
                            if(text != null){
                                title = text;
                            }
                        }
                        ((TextView) view).setText(title);
                    }
                }
                if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_TITLE_LABEL) {
                    if (!TextUtils.isEmpty(data.getGist().getTitle())) {
                        ((TextView) view).setText(data.getGist().getTitle());
                        if (childComponent.getNumberOfLines() != 0) {
                            ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                        }
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    }
                    setTypeFace(appCMSPresenter,context, jsonValueKeyMap, childComponent, ((TextView) view));
                    view.setFocusable(false);

                } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DESCRIPTION_LABEL) {
                    if (!TextUtils.isEmpty(data.getGist().getDescription())) {
                        ((TextView) view).setText(Html.fromHtml(data.getGist().getDescription()));
                        if (childComponent.getNumberOfLines() != 0) {
                            ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                        }
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    }
                    setTypeFace(appCMSPresenter,context, jsonValueKeyMap, childComponent, ((TextView) view));
                    view.setFocusable(false);
                } else if (componentKey == AppCMSUIKeyType.PAGE_HISTORY_LAST_ADDED_LABEL_KEY) {
                    if (data.getUpdateDate() != 0
                            /*&& data.getUpdateDate() < System.currentTimeMillis()*/) {
                        Calendar thatDay = Calendar.getInstance();
                        Date date = new Date(data.getUpdateDate());
                        thatDay.setTime(date);
                        Calendar today = Calendar.getInstance();
                        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
                        long days = diff / (24 * 60 * 60 * 1000);
                        String fmt = getResources().getText(R.string.item_shop).toString();
                        ((TextView) view).setText(MessageFormat.format(fmt, days));
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_SUBTITLE_LABEL) {
                        StringBuilder stringBuilder = new StringBuilder();

                        if (data.getGist() != null) {
                            stringBuilder.append(Utils.convertSecondsToTime(data.getGist().getRuntime()));
                        }

                        if (data.getContentDetails() != null
                                && data.getContentDetails().getAuthor() != null) {
                            if (stringBuilder.length() > 0) stringBuilder.append(" | ");
                            stringBuilder.append(data.getContentDetails().getAuthor());
                        }

                        if (data.getGist() != null && data.getGist().getPublishDate() != null) {
                            try {

                                    String date = appCMSPresenter.getDateFormat(
                                            Long.parseLong(data.getGist().getPublishDate()),
                                            "MMMM dd, yyyy");

                                /*Date publishedDate = new Date(data.getGist().getPublishDate());
                                SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                                String date = spf.format(publishedDate);*/
                                if (stringBuilder.length() > 0) stringBuilder.append(" | ");
                                stringBuilder.append("Published on ");
                                stringBuilder.append(date);
                            } catch (Exception e) {
                            }
                        }
                        ((TextView) view).setText(stringBuilder);
                }
            } else if (componentKey == AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY) {
                int gridImagePadding = Integer.valueOf(
                        childComponent.getLayout().getTv().getPadding() != null
                        ? childComponent.getLayout().getTv().getPadding()
                        : "0");
                view.setPadding(gridImagePadding, 0, gridImagePadding, 0);
                ((ProgressBar) view).setProgressDrawable(Utils.getProgressDrawable(
                        context,
                        childComponent.getUnprogressColor(),
                        appCMSPresenter));
                ((ProgressBar) view).setMax(100);
                int progress = (int) Math.ceil(Utils.getPercentage(data.getGist().getRuntime(),
                        data.getGist().getWatchedTime()));
                //Log.d(TAG , "Progress Bar = "+progress);
                ((ProgressBar) view).setProgress(0);
                ((ProgressBar) view).setProgress(progress);
                view.setFocusable(false);
            }
            view.forceLayout();
        }
    }

    public Component matchComponentToView(View view) {
        Component result = null;
        for (TVCollectionGridItemView.ItemContainer itemContainer : childItems) {
            if (itemContainer.childView == view) {
                return itemContainer.component;
            }
        }
        return result;
    }

    public List<View> getViewsToUpdateOnClickEvent() {
        return viewsToUpdateOnClickEvent;
    }

    public interface OnClickHandler {
        void click(TVCollectionGridItemView collectionGridItemView,
                   Component childComponent,
                   ContentDatum data);

        void play(Component childComponent, ContentDatum data);

        void delete(Component childComponent, ContentDatum data);
    }

    public static class ItemContainer {
        View childView;
        Component component;

        public static class Builder {
            private TVCollectionGridItemView.ItemContainer itemContainer;

            public Builder() {
                itemContainer = new TVCollectionGridItemView.ItemContainer();
            }

            public TVCollectionGridItemView.ItemContainer.Builder childView(View childView) {
                itemContainer.childView = childView;
                return this;
            }

            public TVCollectionGridItemView.ItemContainer.Builder component(Component component) {
                itemContainer.component = component;
                return this;
            }

            public TVCollectionGridItemView.ItemContainer build() {
                return itemContainer;
            }
        }
    }

    public ViewGroup getChildrenContainer() {
        if (childrenContainer == null) {
            return createChildrenContainer();
        }
        return childrenContainer;
    }
}
