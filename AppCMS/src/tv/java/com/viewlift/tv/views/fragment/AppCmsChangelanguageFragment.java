package com.viewlift.tv.views.fragment;


import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.Language;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.utils.LocaleUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class AppCmsChangelanguageFragment extends AbsDialogFragment {

    private AppCMSPresenter appCMSPresenter;

    public AppCmsChangelanguageFragment() {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public static AppCmsChangelanguageFragment newInstance() {
        AppCmsChangelanguageFragment fragment = new AppCmsChangelanguageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCMSPresenter =
                ((AppCMSApplication) getActivity().getApplication()).getAppCMSPresenterComponent().appCMSPresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_language, null);
        RecyclerView languageListView = view.findViewById(R.id.language_recylerview);
        languageListView.setAdapter(new LanuageAdapter());
        languageListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppBackgroundColor()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener((dialogInterface, i, keyEvent) -> {
            switch(keyEvent.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                        appCMSPresenter.stopSyncCodeAPI();
                        dismiss();
                        return true;
                    }
                    break;
            }
            return false;
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putInt( getString(R.string.tv_dialog_width_key) , MATCH_PARENT);
        bundle.putInt( getString(R.string.tv_dialog_height_key) , MATCH_PARENT);
        super.onActivityCreated(bundle);
    }

    class LanuageAdapter extends RecyclerView.Adapter<ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.change_language_view_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            System.out.println("");
            Language language = getItem(position);
            holder.mText.setText(language.getLanguageName());
        }


        @Override
        public int getItemCount() {
            return appCMSPresenter.getLanguageArrayList().size();
        }

        public Language getItem(int position){
            return appCMSPresenter.getLanguageArrayList().get(position);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.btn_language_select)
        Button languageSelectBtn;

        @BindView(R.id.language_text)
        TextView mText;

        public ViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
            Component component = new Component();
            component.setBorderColor(Utils.getColor(getActivity(),Integer.toHexString(ContextCompat.getColor(getActivity() ,
                    R.color.btn_color_with_opacity))));
            component.setBorderWidth(4);

            languageSelectBtn.setBackground(Utils.setButtonBackgroundSelector(getActivity() ,
                    Color.parseColor(Utils.getFocusColor(getActivity(),appCMSPresenter)),
                    component,
                    appCMSPresenter));

            languageSelectBtn.setTextColor(Utils.getButtonTextColorDrawable(
                    Utils.getColor(getActivity(),Integer.toHexString(ContextCompat.getColor(getActivity() ,
                            R.color.btn_color_with_opacity)))
                    ,
                    Utils.getColor(getActivity() , Integer.toHexString(ContextCompat.getColor(getActivity() ,
                            android.R.color.white))),appCMSPresenter
            ));

            languageSelectBtn.setOnClickListener( v -> {
                int position = getAdapterPosition();
                Language language = appCMSPresenter.getLanguageArrayList().get(position);
                LocaleUtils.setLocale(appCMSPresenter.getCurrentContext(),language.getLanguageCode());
                appCMSPresenter.navigateToHomePage();
            });
        }
    }

}