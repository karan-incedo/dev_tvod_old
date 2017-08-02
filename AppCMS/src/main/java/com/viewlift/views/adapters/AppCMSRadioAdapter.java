package com.viewlift.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.viewlift.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sandeep.singh on 7/28/2017.
 */

public abstract class AppCMSRadioAdapter<T> extends RecyclerView.Adapter<AppCMSRadioAdapter.ViewHolder> {
    int downloadQualityPosition = 1; // Default position is 1, i.e 720p
    List<T> mItems;
    private ItemClickListener itemClickListener;
    private Context mContext;

    public AppCMSRadioAdapter(Context context, List<T> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public void onBindViewHolder(AppCMSRadioAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.mRadio.setChecked(i == downloadQualityPosition);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.view_item, viewGroup, false);
        return new ViewHolder(view);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener<T> {
        void onItemClick(T item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.radio)
        RadioButton mRadio;

        @BindView(R.id.text)
        TextView mText;

        public ViewHolder(final View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);

            View.OnClickListener clickListener = v -> {
                downloadQualityPosition = getAdapterPosition();
                notifyItemRangeChanged(0, mItems.size());
                itemClickListener.onItemClick(mItems.get(downloadQualityPosition));
            };
            itemView.setOnClickListener(clickListener);
            mRadio.setOnClickListener(clickListener);
        }
    }
}