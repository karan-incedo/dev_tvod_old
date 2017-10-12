package com.viewlift.views.customviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.viewlift.R;
import com.viewlift.views.adapters.PopupMenuAdapter;

import java.util.ArrayList;

public class PopupMenu extends PopupWindow {

    private Activity activity;
    public PopupMenu(Activity activity) {
        super(activity);
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popView = inflater.inflate(R.layout.pop_list, null);
        this.setContentView(popView);
//		this.setWidth(dip2px(activity, 150));
//        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);

        ListView listView = (ListView) popView.findViewById(R.id.popList);
        ArrayList<PopModel> popList = new ArrayList();
        popList.add(new PopModel(R.drawable.ic_tickets, "Tickets"));
        popList.add(new PopModel(R.drawable.ic_facebook, "Facebook"));
        popList.add(new PopModel(R.drawable.ic_instagram, "Instagram"));
        listView.setAdapter(new PopupMenuAdapter(popList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("pos",i+"");
            }
        });
    }


    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    public void showLocation(int resourId,Activity activity) {
        showAsDropDown(activity.findViewById(resourId), dip2px(activity, 0),
                dip2px(activity, -8));
    }

}
