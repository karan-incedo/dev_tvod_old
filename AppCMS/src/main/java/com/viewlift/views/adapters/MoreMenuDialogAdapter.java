package com.viewlift.views.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.Links;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.ArrayList;

/**
 * Created by wishy.gupta on 24-11-2017.
 */

public class MoreMenuDialogAdapter extends BaseAdapter {
    private AppCMSPresenter appCMSPresenter;
    ArrayList<Links> links;

    public MoreMenuDialogAdapter(AppCMSPresenter appCMSPresenter, ArrayList<Links> links) {
        this.appCMSPresenter = appCMSPresenter;
        this.links = links;
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public Object getItem(int position) {
        return links.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.pop_element, parent, false);
            viewHolder.buttonDialog = (Button) convertView.findViewById(R.id.dialog_button);
            viewHolder.buttonDialog.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                    .getCta().getPrimary().getTextColor()));
            viewHolder.buttonDialog.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                    .getCta().getPrimary().getBackgroundColor()));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (links != null && links.get(position).getTitle() != null) {
            viewHolder.buttonDialog.setText(links.get(position).getTitle());
        }
        viewHolder.buttonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCMSPresenter.openChromeTab(links.get(position).getDisplayedPath());
            }
        });
        return convertView;

    }

    static class ViewHolder {
        Button buttonDialog;
    }
}
