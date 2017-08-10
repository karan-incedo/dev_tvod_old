package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.search.AppCMSSearchResult;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.models.network.components.DaggerAppCMSSearchUrlComponent;
import com.viewlift.models.network.modules.AppCMSSearchUrlData;
import com.viewlift.models.network.modules.AppCMSSearchUrlModule;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.models.network.utility.MainUtils;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.tv.views.component.AppCmsTvSearchComponent;
import com.viewlift.tv.views.component.DaggerAppCmsTvSearchComponent;
import com.viewlift.tv.views.customviews.CustomHeaderItem;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.presenter.AppCmsListRowPresenter;
import com.viewlift.tv.views.presenter.CardPresenter;
import com.viewlift.tv.views.presenter.JumbotronPresenter;
import com.viewlift.views.activity.AppCMSSearchActivity;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by nitin.tyagi on 7/21/2017.
 */

public class AppCmsSearchFragment extends Fragment {

    private static final String TAG = AppCmsSearchFragment.class.getName();
    @Inject
    AppCMSSearchUrlData appCMSSearchUrlData;
    @Inject
    AppCMSSearchCall appCMSSearchCall;
    private String lastSearchedString = "";
    private final int SEARCH_THRESHOLD = 3000;
    private long mLastClickTime;
    private boolean isCallToBeMade;
    private SearchAsyncTask searchTask;
    private  ModuleList moduleList;
    private int trayIndex = -1;
    private ArrayObjectAdapter mRowsAdapter;
    private AppCMSPresenter appCMSPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.appcms_search_view , null);
        EditText editText = (EditText)view.findViewById(R.id.appcms_et_search);
        editText.requestFocus();

        moduleList = new GsonBuilder().create().fromJson(MainUtils.loadJsonFromAssets(getActivity(), "tray_ftv_component.json"), ModuleList.class);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("BOY beforeTextChanged === ="  + charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("BOY onTextChanged === ="  + charSequence.toString());
            }


            @Override
            public void afterTextChanged(Editable editable) {
                if (lastSearchedString.equals(editable.toString())) {
                    return;
                }
                int timeElapsedAfterLastSearch = (int) (SystemClock.elapsedRealtime() - mLastClickTime);
                if (editable.length() >= 3) {
                   /* if (Utils.isConnected(mActivity))*/ {
                        if (timeElapsedAfterLastSearch > SEARCH_THRESHOLD) {
                            isCallToBeMade = false;
                           // hideNoResultsFound();

                            if (null != searchTask
                                    && searchTask.getStatus() == AsyncTask.Status.RUNNING) {
                                searchTask.cancel(true);
                            }

                            searchTask = new SearchAsyncTask(searchDataObserver, appCMSSearchCall);

                            String searchString = "";
                            try {
                                searchString = URLDecoder.decode(editable.toString(), "UTF-8");
                            }catch(Exception e){

                            }
                            lastSearchedString = searchString;

                            final String url = getUrl(lastSearchedString);

                            searchTask.execute(url);
                            mLastClickTime = SystemClock.elapsedRealtime();
                        } else {
                            // we lost a hit, even the length was >= 3.
                            // wait for 3 sec and make an API call if user doesn't search anything
                            isCallToBeMade = true;
                            hit(editable.toString());
                        }
                    }/* else {
                        //listener.showInternetDisconnectedDialog(RfixSearchFragment.this);
                        //RETRY_WHAT = RETRY_ON_SEARCHING;
                    }*/
                } else {
//                adapter.setData(new ArrayList<>());
                    //searchVerticalGridFragment.clearData();
                    lastSearchedString = "";
                    //btnSort.setVisibility(View.INVISIBLE);
                }
            }
        });
        return view;
    }


    private String getUrl(String url){
        return getString(R.string.app_cms_search_api_url,
                appCMSSearchUrlData.getBaseUrl(),
                appCMSSearchUrlData.getSiteName(),
                url);

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindSearchComponent();
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


    Action1<List<AppCMSSearchResult>> searchDataObserver = new Action1<List<AppCMSSearchResult>>() {
        @Override
        public void call(List<AppCMSSearchResult> appCMSSearchResults) {
            if(null != mRowsAdapter){
                mRowsAdapter.clear();
                mRowsAdapter = null;
            }
            setAdapter(appCMSSearchResults);
        }
    };


    /**
     * Say for example we missed a API hit because the time threshold wasn't elapsed yet, so if user
     * doesn't change again then nothing happens, therefore waiting for 3 seconds if user has
     * changed something, if yes (by checking {@link #isCallToBeMade}) make an API call else
     * do nothing
     *
     * @param s the string used to query
     */
    private void hit(final String s) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (isCallToBeMade) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //hideNoResultsFound();
                                if (searchTask != null && searchTask.getStatus() == AsyncTask.Status.RUNNING) {
                                    searchTask.cancel(true);
                                }
                                searchTask = new SearchAsyncTask(searchDataObserver,appCMSSearchCall);
                                final String url = getUrl(s);
                                searchTask.execute(url);

                                mLastClickTime = SystemClock.elapsedRealtime();
                            }
                        });
                    }
                }
            }
        }, 3000);
    }


    private static class SearchAsyncTask extends AsyncTask<String, Void, List<AppCMSSearchResult>> {
        final Action1<List<AppCMSSearchResult>> dataReadySubscriber;
        final AppCMSSearchCall appCMSSearchCall;

        SearchAsyncTask(Action1<List<AppCMSSearchResult>> dataReadySubscriber,
                        AppCMSSearchCall appCMSSearchCall) {
            this.dataReadySubscriber = dataReadySubscriber;
            this.appCMSSearchCall = appCMSSearchCall;
        }

        @Override
        protected List<AppCMSSearchResult> doInBackground(String... params) {
            if (params.length > 0) {
                try {
                    return appCMSSearchCall.call(params[0]);
                } catch (IOException e) {
                    Log.e(TAG, "I/O DialogType retrieving search data from URL: " + params[0]);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<AppCMSSearchResult> result) {
            Observable.just(result).subscribe(dataReadySubscriber);
        }
    }



    private void setAdapter(List<AppCMSSearchResult> appCMSSearchResults){
        if(null != moduleList){
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
                AppCmsBrowseFragment browseFragment = AppCmsBrowseFragment.newInstance(getActivity());
                browseFragment.setAdapter(mRowsAdapter);
                getChildFragmentManager().beginTransaction().replace(R.id.appcms_search_results_container ,browseFragment ,"frag").commit();
            }
        }
    }


    public void createTrayModule(final Context context,
                                 final Component component,
                                 List<AppCMSSearchResult> appCMSSearchResults,
                                 final ModuleList moduleUI,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 final AppCMSPresenter appCMSPresenter,
                                 boolean isCarousel) {

        CustomHeaderItem customHeaderItem = null;
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
                        customHeaderItem = new CustomHeaderItem(context, trayIndex++, "RESULT FOR " + lastSearchedString);
                        customHeaderItem.setmIsCarousal(isCarousel);
                        customHeaderItem.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                        customHeaderItem.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                        customHeaderItem.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
                        customHeaderItem.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
                        break;
                }
                break;
            case PAGE_COLLECTIONGRID_KEY:
                        /*for(Component component1 : component.getComponents()){*/

                if (null == mRowsAdapter) {
                    AppCmsListRowPresenter appCmsListRowPresenter = new AppCmsListRowPresenter(context);
                    mRowsAdapter = new ArrayObjectAdapter(appCmsListRowPresenter);
                }

                if (customHeaderItem == null) {
                    customHeaderItem = new CustomHeaderItem(context, trayIndex++, "RESULT FOR " + lastSearchedString);
                    customHeaderItem.setmIsCarousal(false);
                    customHeaderItem.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                    customHeaderItem.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                    customHeaderItem.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
                    customHeaderItem.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
                }
                CardPresenter trayCardPresenter = new CardPresenter(context, appCMSPresenter,
                        Integer.valueOf(component.getLayout().getTv().getHeight()),
                        Integer.valueOf(component.getLayout().getTv().getWidth()),
                        jsonValueKeyMap
                );
                ArrayObjectAdapter traylistRowAdapter = new ArrayObjectAdapter(trayCardPresenter);

                for (AppCMSSearchResult searchResult : appCMSSearchResults) {
                    BrowseFragmentRowData rowData = new BrowseFragmentRowData();
                    /**
                     * There is no getContent() in the AppCMSSearchResult object
                     * This class needs to be modified accordingly
                     * Merge issue: @Nitin Tyagi please fix
                     rowData.contentData = searchResult.getContent();
                     **/
                    rowData.uiComponentList = component.getComponents();
                    traylistRowAdapter.add(rowData);
                    Log.d(TAG, "NITS header Items ===== " + rowData.contentData.getGist().getTitle());
                }

                mRowsAdapter.add(new ListRow(customHeaderItem, traylistRowAdapter));
                break;
        }
    }

}


