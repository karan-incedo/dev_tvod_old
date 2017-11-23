package com.viewlift.views.customviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.viewlift.R;
import com.viewlift.views.adapters.PopupMenuAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

public class PopupMenu extends PopupWindow implements View.OnClickListener {

    RelativeLayout ticketLayout;
    RelativeLayout facebookLayout;
    RelativeLayout instagramLayout;
    private Activity activity;
    ListenerForPopMenu listenerForPopMenu;

    public PopupMenu(Activity activity) {
        super(activity);

        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popView = inflater.inflate(R.layout.pop_list, null);
        this.setContentView(popView);
//		this.setWidth(dip2px(activity, 200));
//        this.setHeight(dip2px(activity, 200));
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        ticketLayout = (RelativeLayout) popView.findViewById(R.id.popDialogLayout1);
        facebookLayout = (RelativeLayout) popView.findViewById(R.id.popDialogLayout2);
        instagramLayout = (RelativeLayout) popView.findViewById(R.id.popDialogLayout3);
        ticketLayout.setOnClickListener(this);
        facebookLayout.setOnClickListener(this);
        instagramLayout.setOnClickListener(this);
    }


    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void showLocation(int resourId, Activity activity) {
        showAsDropDown(activity.findViewById(resourId), dip2px(activity, 0),
                dip2px(activity, -20));
    }

    public void setListener(ListenerForPopMenu listenerForPopMenu) {
        this.listenerForPopMenu = listenerForPopMenu;
    }

    @Override
    public void onClick(View view) {
        if (view == ticketLayout) {
            listenerForPopMenu.ticketsClick();
        }
        if (view == facebookLayout) {
            listenerForPopMenu.facebookClick();
        }
        if (view == instagramLayout) {
            listenerForPopMenu.instagramClick();
        }
    }

    public interface ListenerForPopMenu {
        void ticketsClick();

        void facebookClick();

        void instagramClick();
    }

}
