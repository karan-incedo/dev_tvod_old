package com.viewlift.tv.views.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.firetvcustomkeyboard.CustomKeyboard;
import com.viewlift.models.data.appcms.search.AppCMSSearchResult;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.models.network.modules.AppCMSSearchUrlData;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.tv.views.customviews.CustomHeaderItem;
import com.viewlift.tv.views.presenter.AppCmsListRowPresenter;
import com.viewlift.tv.views.presenter.CardPresenter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by nitin.tyagi on 7/21/2017.
 */

public class AppCmsSearchFragment extends BaseFragment {

    private static final String TAG = AppCmsSearchFragment.class.getName();
    private static final long DELAY = 5000;
    @Inject
    AppCMSSearchUrlData appCMSSearchUrlData;
    @Inject
    AppCMSSearchCall appCMSSearchCall;
    private String lastSearchedString = "";
    private SearchAsyncTask searchTask;
    private  ModuleList moduleList;
    private int trayIndex = -1;
    private ArrayObjectAdapter mRowsAdapter;
    private AppCMSPresenter appCMSPresenter;
    TextView noSearchTextView;
    TextView searchPrevious;
    private TextView searchOne, searchTwo, searchThree;
    private Button btnClearHistory;
    private EditText editText;
    private LinearLayout llView;
    private boolean clrbtnFlag;

    private Typeface semiBoldTypeFace;
    private Typeface extraBoldTypeFace;
    private Typeface regularTypeface;
    private CustomKeyboard customKeyboard;

    private ProgressBar progressBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindSearchComponent();
    }

    public static AppCmsSearchFragment newInstance(String searchString) {

        Bundle args = new Bundle();
        args.putString("searchString", searchString);
        AppCmsSearchFragment fragment = new AppCmsSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.appcms_search_view , null);

        setTypeFaceValue();
        noSearchTextView = (TextView)view.findViewById(R.id.appcms_no_search_result);
        noSearchTextView.setVisibility(View.GONE);
        noSearchTextView.setTextColor(Color.parseColor(appCMSPresenter.getAppTextColor()));

        llView = (LinearLayout)view.findViewById(R.id.ll_view);
        editText = (EditText)view.findViewById(R.id.appcms_et_search);
        editText.setFocusable(false);
        Component editTextComponent = new Component();
        editTextComponent.setType(getString(R.string.app_cms_page_textfield_key));
        editTextComponent.setCornerRadius(5);
        editText.setBackground(Utils.getTrayBorder(getActivity(),
                Utils.getFocusColor(getActivity(), appCMSPresenter), editTextComponent));

        searchPrevious = (TextView)view.findViewById(R.id.search_previous);
        searchOne = (TextView)view.findViewById(R.id.search_history_one);
        searchTwo = (TextView)view.findViewById(R.id.search_history_two);
        searchThree = (TextView)view.findViewById(R.id.search_history_three);
        btnClearHistory = (Button)view.findViewById(R.id.btn_clear_history);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().
                setColorFilter(Color.parseColor(Utils.getFocusColor(getActivity(),appCMSPresenter)) ,
                        PorterDuff.Mode.MULTIPLY
                );

        customKeyboard = (CustomKeyboard)view.findViewById(R.id.appcms_keyboard);
        customKeyboard.setFocusColor(Utils.getFocusColor(getActivity() , appCMSPresenter));

        if(null != appCMSPresenter &&
                null != appCMSPresenter.getAppCMSMain() &&
                null != appCMSPresenter.getAppCMSMain().getBrand() &&
                null != appCMSPresenter.getAppCMSMain().getBrand().getCta() &&
                null != appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary()){
            String focusStateTextColor = appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor();
            String unFocusStateTextColor = appCMSPresenter.getAppCMSMain().getBrand().getCta().getSecondary().getTextColor();
            customKeyboard.getButtonTextColorDrawable(focusStateTextColor , unFocusStateTextColor);
        }
        customKeyboard.requestFocus();

        customKeyboard.setonButtonFocusChangeListener(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null != appCMSPresenter
                        && null != appCMSPresenter.getCurrentActivity()
                        && appCMSPresenter.getCurrentActivity() instanceof AppCmsHomeActivity){
                    ((AppCmsHomeActivity) appCMSPresenter.getCurrentActivity()).shouldShowLeftNavigation(aBoolean);
                }
            }
        });

        if(null != regularTypeface)
            editText.setTypeface(regularTypeface);
        if(null != extraBoldTypeFace)
            searchPrevious.setTypeface(extraBoldTypeFace);
        if(null != semiBoldTypeFace) {
            noSearchTextView.setTypeface(semiBoldTypeFace);
            searchOne.setTypeface(semiBoldTypeFace);
            searchTwo.setTypeface(semiBoldTypeFace);
            searchThree.setTypeface(semiBoldTypeFace);
            btnClearHistory.setTypeface(semiBoldTypeFace);
        }

        String packageName = getActivity().getPackageName();
        List<String> strings = Arrays.asList(getResources().getStringArray(R.array.app_cms_splash_progress_needed));

        boolean requirelandscapeTray = strings.contains(packageName);

        if(appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS) {
            moduleList = new GsonBuilder().create().
                    fromJson(Utils.loadJsonFromAssets(getActivity(), "tray_ftv_component_sports.json"), ModuleList.class);
        }else {
            moduleList = new GsonBuilder().create().
                    fromJson(Utils.loadJsonFromAssets(getActivity(),
                            requirelandscapeTray ? "tray_ftv_component_tray02.json" : "tray_ftv_component.json")
                            , ModuleList.class);
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (lastSearchedString.trim().equals(editable.toString().trim())) {
                    return;
                }
                if (editable.toString().trim().length() >= 3){
                    if(appCMSPresenter.isNetworkConnected()){
                        handler.removeCallbacks(searcRunnable);

                        handler.postDelayed(searcRunnable,DELAY);

                        progressBar.setVisibility(View.VISIBLE);
                    }else{
                        appCMSPresenter.searchRetryDialog(editable.toString());
                    }
                }else{
                    lastSearchedString = "";
                    if(null != mRowsAdapter){
                        mRowsAdapter.clear();
                    }
                    noSearchTextView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnClearHistory.setOnClickListener(onClickListener);
        searchOne.setOnClickListener(onClickListener);
        searchTwo.setOnClickListener(onClickListener);
        searchThree.setOnClickListener(onClickListener);

        searchOne.setTextColor(Utils.getTextColorDrawable(getActivity() ,
               appCMSPresenter));
        searchTwo.setTextColor(Utils.getTextColorDrawable(getActivity() ,
                appCMSPresenter));
        searchThree.setTextColor(Utils.getTextColorDrawable(getActivity() ,
                appCMSPresenter));


        Component component = new Component();
        component.setBorderColor(Utils.getColor(getActivity(),Integer.toHexString(ContextCompat.getColor(getActivity() ,
                R.color.btn_color_with_opacity))));
        component.setBorderWidth(4);

        btnClearHistory.setBackground(Utils.setButtonBackgroundSelector(getActivity() ,
                Color.parseColor(Utils.getFocusColor(getActivity(),appCMSPresenter)),
                component,
                appCMSPresenter));

        btnClearHistory.setTextColor(Utils.getButtonTextColorDrawable(
                Utils.getColor(getActivity(),Integer.toHexString(ContextCompat.getColor(getActivity() ,
                        R.color.btn_color_with_opacity)))
                ,
                Utils.getColor(getActivity() , Integer.toHexString(ContextCompat.getColor(getActivity() ,
                        android.R.color.white))),appCMSPresenter
        ));

        btnClearHistory.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()){
                    case KeyEvent.KEYCODE_DPAD_UP:
                    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                        customKeyboard.setFocusOnGroup();
                        return true;
                    }
                }
                return false;
            }
        });

        if (getArguments() != null
                && getArguments().getString("searchString") != null) {
            editText.setText(getArguments().getString("searchString"));
        }

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.search_history_one :
                case R.id.search_history_two:
                case R.id.search_history_three:
                    TextView textView = (TextView)view;
                    editText.setText(textView.getText());
                    break;
                case R.id.btn_clear_history:
                    llView.setVisibility(View.INVISIBLE);
                    appCMSPresenter.clearSearchResultsSharePreference();
                    currentString = "";
                    previousString = "";

                  /*  if(clrbtnFlag) {
                        List<String> result = null;
                        result = appCMSPresenter.getSearchResultsFromSharePreference();
                        if(result == null)
                            result = new ArrayList<String>();

                        result.add(lastSearchedString);
                        appCMSPresenter.setSearchResultsOnSharePreference(result);
                    }*/


                    break;
            }

        }
    };

    private void setTypeFaceValue(){

        if(null == semiBoldTypeFace) {
            Component openSansSemiBoldComp = new Component();
            openSansSemiBoldComp.setFontFamily(appCMSPresenter.getFontFamily());
            openSansSemiBoldComp.setFontWeight(getString(R.string.app_cms_page_font_semibold_key));
            semiBoldTypeFace = Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap(), openSansSemiBoldComp);
        }
        if(null == extraBoldTypeFace) {
            Component openSansExtraBoldComp = new Component();
            openSansExtraBoldComp.setFontFamily(appCMSPresenter.getFontFamily());
            openSansExtraBoldComp.setFontWeight(getString(R.string.app_cms_page_font_extrabold_key));
            extraBoldTypeFace = Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap(), openSansExtraBoldComp);
        }
        if(null == regularTypeface) {
            Component openSansRegularComp = new Component();
            openSansRegularComp.setFontFamily(appCMSPresenter.getFontFamily());
            regularTypeface = Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap(), openSansRegularComp);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        List<String> resultForTv = appCMSPresenter.getSearchResultsFromSharePreference();

        if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.ENTERTAINMENT)) {
            if (resultForTv != null && resultForTv.size() > 0) {
                llView.setVisibility(View.VISIBLE);
                if(resultForTv.size() > 3) {
                    resultForTv.remove(resultForTv.iterator().next());
                }
                setSearchValueOnView(resultForTv, resultForTv.size());
            } else {
                llView.setVisibility(View.INVISIBLE);
            }
        } else {
            llView.setVisibility(View.GONE);
        }
        setFocusSequence();
        if(mRowsAdapter == null || (mRowsAdapter != null && mRowsAdapter.size() == 0))
        customKeyboard.requestFocus();

        Utils.pageLoading(false,getActivity());
    }


    /**
     * Handle the key press event on the keyboard.
     * All the events are handled viz. space, number/alphabets and back presses
     *
     * @param v instance of the view on which the keyPressed is called
     */
    public void keyPressed(View v) {
        if (v instanceof Button) {
            editText.append(" ");
        } else if (v instanceof TextView) {
            CharSequence text = ((TextView) v).getText();
            editText.append(text);
        } else if (v instanceof ImageButton) {
            String s = editText.getText().toString();
            if (s.length() > 0)
                editText.setText(s.substring(0, s.length() - 1));
        }
    }

    private void setFocusSequence(){
        searchOne.setNextFocusRightId(searchTwo.getVisibility() == View.VISIBLE ? R.id.search_history_two : R.id.btn_clear_history);
        searchTwo.setNextFocusRightId(searchThree.getVisibility() == View.VISIBLE? R.id.search_history_three : R.id.btn_clear_history);
        btnClearHistory.setNextFocusLeftId(searchThree.getVisibility() == View.VISIBLE ? R.id.search_history_three
                : ((searchTwo.getVisibility() == View.VISIBLE  ?
                R.id.search_history_two : R.id.search_history_one)));

    }

    private String getUrl(String url){
        return getString(R.string.app_cms_search_api_url_video_only,
                appCMSSearchUrlData.getBaseUrl(),
                appCMSSearchUrlData.getSiteName(),
                url,
                getString(R.string.type_video_only));

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void bindSearchComponent(){
        appCMSPresenter =
                ((AppCMSApplication) getActivity().getApplication()).getAppCMSPresenterComponent().appCMSPresenter();

        if (appCMSSearchUrlData == null || appCMSSearchCall == null) {
            ((AppCmsHomeActivity)getActivity()).getAppCMSSearchComponent().inject(this);
            if (appCMSSearchUrlData == null || appCMSSearchCall == null) {
                return;
            }
        }
    }


   String previousString = "" , currentString = "";
    Action1<List<AppCMSSearchResult>> searchDataObserver = new Action1<List<AppCMSSearchResult>>() {
        @Override
        public void call(List<AppCMSSearchResult> appCMSSearchResults) {
            if(null != mRowsAdapter){
                mRowsAdapter.clear();
                mRowsAdapter = null;
            }
            if(null != appCMSSearchResults && appCMSSearchResults.size() > 0){
                clrbtnFlag = true;
                noSearchTextView.setVisibility(View.GONE);

                if(currentString.length() > 0){
                    previousString = currentString;
                }
                currentString = lastSearchedString;

                if(previousString.length() > 0){
                    addSearchValueInSharePref(previousString);
                }
                List<String> resultForTv = appCMSPresenter.getSearchResultsFromSharePreference();
                if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.ENTERTAINMENT)) {
                    if (resultForTv != null && resultForTv.size() > 0) {
                        if (resultForTv.size() > 0) {
                            llView.setVisibility(View.VISIBLE);
                            setSearchValueOnView(resultForTv, resultForTv.size());

                        } else {
                            llView.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    llView.setVisibility(View.GONE);
                }
                setAdapter(appCMSSearchResults);
            }else{
                if(!appCMSPresenter.isNetworkConnected()){
                    appCMSPresenter.searchRetryDialog(lastSearchedString.toString());
                }else{
                    clrbtnFlag = false;
                    noSearchTextView.setText(getString(R.string.app_cms_no_search_result , lastSearchedString).toUpperCase());
                    noSearchTextView.setVisibility(View.VISIBLE);
                }

                List<String> resultForTv = appCMSPresenter.getSearchResultsFromSharePreference();
                if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.ENTERTAINMENT)) {
                    if (resultForTv != null && resultForTv.size() > 0) {
                        llView.setVisibility(View.VISIBLE);
                        if (resultForTv.size() > 3) {
                            resultForTv.remove(resultForTv.iterator().next());
                        }
                        setSearchValueOnView(resultForTv, resultForTv.size());
                    }
                } else {
                    llView.setVisibility(View.GONE);
                }
            }

            setFocusSequence();
            progressBar.setVisibility(View.INVISIBLE);
        }

    };

    @Override
    public void onPause() {
        if(currentString.length() > 0){
            addSearchValueInSharePref(currentString);
        }
        currentString = "";
        previousString = "";
        super.onPause();
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void setSearchValueOnView(List<String> resultForTv, int size){
        for (int i = 0; i < size; i++) {
            if(i == 0){
                searchOne.setText(resultForTv.get(i).trim().toUpperCase());
                searchTwo.setVisibility(View.INVISIBLE);
                searchThree.setVisibility(View.INVISIBLE);
            } else if(i == 1){
                searchTwo.setVisibility(View.VISIBLE);
                searchThree.setVisibility(View.INVISIBLE);
                searchTwo.setText(resultForTv.get(i).trim().toUpperCase());
            } else if(i == 2){
                searchThree.setVisibility(View.VISIBLE);
                searchThree.setText(resultForTv.get(i).trim().toUpperCase());
            }
        }
    }

    private void addSearchValueInSharePref(String valueToBeSaved){
        List<String> result = appCMSPresenter.getSearchResultsFromSharePreference();
        if(result == null) {
            List<String> list = new ArrayList<String>();
            list.add(valueToBeSaved);
            appCMSPresenter.setSearchResultsOnSharePreference(list);
        } else {
            if (!result.isEmpty() && result.size() == 4) {
                result.remove(result.iterator().next());
            }
            for(int i = 0; i < result.size(); i++) {
                if(valueToBeSaved.trim().equalsIgnoreCase(result.get(i).trim())){

                    result.remove(result.get(i));
                    break;
                }
            }
            result.add(valueToBeSaved);
            appCMSPresenter.setSearchResultsOnSharePreference(result);
        }

    }

    Handler handler = new Handler();
    Runnable searcRunnable = new Runnable() {
        @Override
        public void run() {
            if(editText.getText().toString().length() > 2) {
                try {
                    if (searchTask != null && searchTask.getStatus() == AsyncTask.Status.RUNNING) {
                        searchTask.cancel(true);
                    }
                    searchTask = new SearchAsyncTask(searchDataObserver,
                            appCMSSearchCall,
                            appCMSPresenter.getApiKey());
                    String encodedString  = URLEncoder.encode(editText.getText().toString().trim() , "UTF-8");
                    String secondEncoding = URLEncoder.encode(encodedString , "UTF-8");

                    final String url = getUrl(secondEncoding);
                    System.out.println("Search result == " + editText.getText().toString().trim() + "url = " + url);
                    lastSearchedString = editText.getText().toString().trim();
                    searchTask.execute(url);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    private class SearchAsyncTask extends AsyncTask<String, Void, List<AppCMSSearchResult>> {
        final Action1<List<AppCMSSearchResult>> dataReadySubscriber;
        final AppCMSSearchCall appCMSSearchCall;
        final String apiKey;

        SearchAsyncTask(Action1<List<AppCMSSearchResult>> dataReadySubscriber,
                        AppCMSSearchCall appCMSSearchCall,
                        String apiKey) {
            this.dataReadySubscriber = dataReadySubscriber;
            this.appCMSSearchCall = appCMSSearchCall;
            this.apiKey = apiKey;
        }

        @Override
        protected List<AppCMSSearchResult> doInBackground(String... params) {
            if (params.length > 0) {
                try {
                    return appCMSSearchCall.call(apiKey, params[0]);
                } catch (IOException e) {
                    //Log.e(TAG, "I/O DialogType retrieving search data from URL: " + params[0]);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<AppCMSSearchResult> result) {
            if(isAdded()) {
                Observable.just(result).subscribe(dataReadySubscriber);
            }
        }
    }

    private Context mContext;
    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    private void setAdapter(List<AppCMSSearchResult> appCMSSearchResults){
        if(null != moduleList){
            trayIndex = -1;
            for(Component component : moduleList.getComponents()){
                createTrayModule(getActivity() ,
                                component ,
                        appCMSSearchResults,
                        moduleList,
                        appCMSPresenter.getJsonValueKeyMap(),
                        appCMSPresenter,
                        false);
            }
        }

        if(null != mRowsAdapter && mRowsAdapter.size() > 0){
            {
                AppCmsBrowseFragment browseFragment = AppCmsBrowseFragment.newInstance(mContext);
                browseFragment.setScreenName("Search");
                browseFragment.setmRowsAdapter(mRowsAdapter);
                getChildFragmentManager().beginTransaction().replace(R.id.appcms_search_results_container ,browseFragment ,"frag").commitAllowingStateLoss();
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    private CustomHeaderItem customHeaderItem = null;
    public void createTrayModule(final Context context,
                                 final Component component,
                                 List<AppCMSSearchResult> appCMSSearchResults,
                                 final ModuleList moduleUI,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 final AppCMSPresenter appCMSPresenter,
                                 boolean isCarousel) {
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
                switch (componentKey) {
                    case PAGE_TRAY_TITLE_KEY:
                        createCustomHeaderItem(context, component, appCMSSearchResults, moduleUI, appCMSPresenter, isCarousel);
                        break;
                }
                break;
            case PAGE_COLLECTIONGRID_KEY:
                if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.ENTERTAINMENT)) {
                    createRowsForEntertainment(context, component, appCMSSearchResults, moduleUI, jsonValueKeyMap, appCMSPresenter);
                } else {
                    createRowsForST(context, component, appCMSSearchResults, moduleUI, jsonValueKeyMap, appCMSPresenter);
                }
                break;
        }
    }

    private void createCustomHeaderItem(Context context, Component component, List<AppCMSSearchResult> appCMSSearchResults, ModuleList moduleUI, AppCMSPresenter appCMSPresenter, boolean isCarousel) {
        customHeaderItem = null;
        customHeaderItem = new CustomHeaderItem(
                context,
                trayIndex++,
                appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.ENTERTAINMENT)
                        ? getResources().getQuantityString(R.plurals.app_cms_search_result_header,
                        appCMSSearchResults.size(),
                        lastSearchedString.toUpperCase())
                        : ""
        );
        customHeaderItem.setmIsCarousal(isCarousel);
        customHeaderItem.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
        customHeaderItem.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
        customHeaderItem.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
        customHeaderItem.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
        customHeaderItem.setFontFamily(component.getFontFamily());
        customHeaderItem.setFontWeight(component.getFontWeight());
        customHeaderItem.setFontSize(component.getLayout().getTv().getFontSize());
    }

    private void createRowsForEntertainment(Context context,
                                            Component component,
                                            List<AppCMSSearchResult> appCMSSearchResults,
                                            ModuleList moduleUI,
                                            Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                            AppCMSPresenter appCMSPresenter) {
        if (null == mRowsAdapter) {
            AppCmsListRowPresenter appCmsListRowPresenter = new AppCmsListRowPresenter(context, appCMSPresenter);
            mRowsAdapter = new ArrayObjectAdapter(appCmsListRowPresenter);
        }
        CardPresenter trayCardPresenter = new CardPresenter(context, appCMSPresenter,
                Integer.valueOf(component.getLayout().getTv().getHeight()),
                Integer.valueOf(component.getLayout().getTv().getWidth()),
                moduleUI.getSettings() != null && moduleUI.getSettings().isInfoHover(),
                component,
                jsonValueKeyMap);
        ArrayObjectAdapter trayListRowAdapter = new ArrayObjectAdapter(trayCardPresenter);

        for (int i = 0, appCMSSearchResultsSize = appCMSSearchResults.size(); i < appCMSSearchResultsSize; i++) {
            AppCMSSearchResult searchResult = appCMSSearchResults.get(i);
            BrowseFragmentRowData rowData = new BrowseFragmentRowData();
            rowData.isSearchPage = true;
            rowData.contentData = searchResult.getContent();
            rowData.uiComponentList = component.getComponents();
            rowData.action = component.getTrayClickAction();
            rowData.blockName = moduleUI.getBlockName();
            rowData.itemPosition = i;
            rowData.infoHover = moduleUI.getSettings() != null && moduleUI.getSettings().isInfoHover();
            trayListRowAdapter.add(rowData);
            //Log.d(TAG, "NITS header Items ===== " + rowData.contentData.getGist().getTitle());
        }
        mRowsAdapter.add(new ListRow(customHeaderItem, trayListRowAdapter));
    }

    private void createRowsForST(Context context,
                                 Component component,
                                 List<AppCMSSearchResult> appCMSSearchResults,
                                 ModuleList moduleUI,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter) {
        if (null == mRowsAdapter) {
            AppCmsListRowPresenter appCmsListRowPresenter = new AppCmsListRowPresenter(context, appCMSPresenter);
            mRowsAdapter = new ArrayObjectAdapter(appCmsListRowPresenter);
        }

        ArrayObjectAdapter trayListRowAdapter = null;
        int position = -1;
         for (int i = 0; i < appCMSSearchResults.size(); i++) {
            if(position == -1){
                 CardPresenter trayCardPresenter = new CardPresenter(context, appCMSPresenter,
                         Integer.valueOf(component.getLayout().getTv().getHeight()),
                         Integer.valueOf(component.getLayout().getTv().getWidth()),
                         moduleUI.getSettings() != null && moduleUI.getSettings().isInfoHover(),
                         component,
                         jsonValueKeyMap
                 );

                 trayListRowAdapter = new ArrayObjectAdapter(trayCardPresenter);
             }
            AppCMSSearchResult searchResult = appCMSSearchResults.get(i);
            BrowseFragmentRowData rowData = new BrowseFragmentRowData();
            rowData.isSearchPage = true;
            rowData.contentData = searchResult.getContent();
            rowData.uiComponentList = component.getComponents();
            rowData.action = component.getTrayClickAction();
            rowData.blockName = moduleUI.getBlockName();
            rowData.infoHover = moduleUI.getSettings() != null && moduleUI.getSettings().isInfoHover();
            rowData.rowNumber = trayIndex;
            position++;
            rowData.itemPosition = position;
            trayListRowAdapter.add(rowData);

            if ((trayListRowAdapter.size()  % 4 == 0) /*already four items in the adapter*/
                    || i == appCMSSearchResults.size() - 1 /*Reached the last item*/) {
                mRowsAdapter.add(new ListRow(customHeaderItem, trayListRowAdapter));
                createCustomHeaderItem(context, component, appCMSSearchResults, moduleUI, appCMSPresenter, false);
                position = -1;
             }
        }
    }


    public void searchResult(String searchQuery){
        try {
            handler.removeCallbacks(searcRunnable);
            if (searchTask != null && searchTask.getStatus() == AsyncTask.Status.RUNNING) {
                searchTask.cancel(true);
            }
            searchTask = new SearchAsyncTask(searchDataObserver,
                    appCMSSearchCall,
                    appCMSPresenter.getApiKey());

            String encodedString = URLEncoder.encode(searchQuery.trim(), "UTF-8");
            String secondEncoding = URLEncoder.encode(encodedString, "UTF-8");

            final String url = getUrl(secondEncoding);
            System.out.println("Search result == " + editText.getText().toString().trim());
            lastSearchedString = editText.getText().toString().trim();
            searchTask.execute(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean isSubNavigationVisible() {
        return false;
    }

    @Override
    public boolean isSubNavExist() {
        return false;
    }

    @Override
    public void showSubNavigation(boolean shouldShow) {

    }

    @Override
    public void setSubnavExistence(boolean isExist) {

    }
}


