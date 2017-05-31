package com.viewlift.views.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.Primary;
import com.viewlift.models.data.appcms.ui.android.User;
import com.viewlift.presenters.AppCMSPresenter;

import org.w3c.dom.Text;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/30/17.
 */

public class AppCMSNavItemsAdapter extends RecyclerView.Adapter<AppCMSNavItemsAdapter.ViewHolder> {
    private static final String TAG = "AppCMSNavItemsAdapter";

    private final Navigation navigation;
    private final AppCMSPresenter appCMSPresenter;
    private boolean userLoggedIn;

    public AppCMSNavItemsAdapter(Navigation navigation,
                                 boolean userLoggedIn,
                                 AppCMSPresenter appCMSPresenter) {
        this.navigation = navigation;
        this.userLoggedIn = userLoggedIn;
        this.appCMSPresenter = appCMSPresenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nav_item,
                        viewGroup,
                        false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Resources resources = viewHolder.itemView.getContext().getResources();
        if (i < navigation.getPrimary().size()) {
            final Primary primary = navigation.getPrimary().get(i);
            StringBuffer iconName = new StringBuffer();
            iconName.append(primary.getDisplayedPath().toLowerCase().replaceAll(" ", "_"));
            iconName.append(primary.getUrl().replace("/", "_"));
            int drawableId = resources.getIdentifier(iconName.toString(),
                    "drawable",
                    viewHolder.itemView.getContext().getPackageName());
            viewHolder.navItemIcon.setBackgroundResource(drawableId);
            viewHolder.navItemLabel.setText(primary.getTitle());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!appCMSPresenter.navigateToPage(primary.getPageId())) {
                        Log.e(TAG, "Could not navigate to page with Title: " +
                                primary.getTitle() +
                                " Id: " +
                                primary.getPageId());
                    }
                }
            });
        } else if (userLoggedIn) {
            final User user = navigation.getUser().get(i - navigation.getPrimary().size());
            StringBuffer iconName = new StringBuffer();
            iconName.append(user.getDisplayedName().toLowerCase().replaceAll(" ", "_"));
            iconName.append(user.getUrl().replaceAll("/", "_"));

            int drawableId = resources.getIdentifier(iconName.toString(),
                    "drawable",
                    viewHolder.itemView.getContext().getPackageName());
            viewHolder.navItemIcon.setBackgroundResource(drawableId);
            viewHolder.navItemLabel.setText(user.getTitle());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!appCMSPresenter.navigateToPage(user.getPageId())) {
                        Log.e(TAG, "Could not navigate to page with Title: " +
                                user.getTitle() +
                                " Id: " +
                                user.getPageId());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (navigation.getPrimary() != null) {
            count += navigation.getPrimary().size();
        }
        if (userLoggedIn && navigation.getUser() != null) {
            count += navigation.getUser().size();
        }
        return count;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView navItemIcon;
        TextView navItemLabel;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.navItemIcon = (ImageView) itemView.findViewById(R.id.nav_item_icon);
            this.navItemLabel = (TextView) itemView.findViewById(R.id.nav_item_label);
        }
    }
}
