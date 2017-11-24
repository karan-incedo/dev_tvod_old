package com.viewlift.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.views.customviews.PopModel;

import java.util.ArrayList;

/**
 * Created by wishy.gupta on 12-10-2017.
 */

public class PopupMenuAdapter extends BaseAdapter {
    private ArrayList<PopModel> items;
    int imageViewId, textViewId;

    public PopupMenuAdapter(ArrayList<PopModel> items) {
        this.items = items;
        this.imageViewId = View.generateViewId();
        this.textViewId = View.generateViewId();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
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

//            convertView = createListElement(parent.getContext());
//            viewHolder.imgViewLeft = (ImageView)
//                    convertView.findViewById(imageViewId);
//            viewHolder.txtViewRight = (TextView)
//                    convertView.findViewById(textViewId);
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.pop_element, parent, false);
           /* viewHolder.imgViewLeft = (ImageView)
                    convertView.findViewById(R.id.popImage);
            viewHolder.txtViewRight = (TextView)
                    convertView.findViewById(R.id.popText);*/
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imgViewLeft.setImageResource(items.get(position).getImg());
        viewHolder.txtViewRight.setText(items.get(position).getTitle());
        return convertView;
    }

    private View createListElement(Context context) {
        RelativeLayout parentLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams parentParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        parentParams.setMargins(10, 5, 10, 5);
        parentLayout.setLayoutParams(parentParams);
        ImageView leftImage = new ImageView(context);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        imageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        leftImage.setLayoutParams(imageParams);
        leftImage.setId(imageViewId);
        parentLayout.addView(leftImage);
        TextView rightText = new TextView(context);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.RIGHT_OF, leftImage.getId());
        textParams.addRule(RelativeLayout.CENTER_VERTICAL);
        textParams.setMargins(10,0,0,0);
        rightText.setLayoutParams(textParams);
        rightText.setId(textViewId);
        rightText.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        parentLayout.addView(rightText);
//        parentLayout.setPadding(10, 5, 10, 5);
        return parentLayout;
    }

    static class ViewHolder {
        ImageView imgViewLeft;
        TextView txtViewRight;
    }
}
