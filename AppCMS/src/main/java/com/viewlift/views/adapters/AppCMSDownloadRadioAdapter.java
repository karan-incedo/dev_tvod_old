package com.viewlift.views.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.Utils;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.viewlift.presenters.AppCMSPresenter.PlatformType.TV;

/**
 * Created by sandeep.singh on 7/28/2017.
 */

public abstract class AppCMSDownloadRadioAdapter<T>
        extends RecyclerView.Adapter<AppCMSDownloadRadioAdapter.ViewHolder> {

    int tintColor;
    protected int downloadQualityPosition = 1; // Default position is 1, i.e 720p
    List<T> mItems;
    protected ItemClickListener itemClickListener;
    private Context mContext;
    private AppCMSPresenter appCMSPresenter;

    public AppCMSDownloadRadioAdapter(Context context, List<T> items , AppCMSPresenter appCMSPresenter) {
        this.mContext = context;
        this.mItems = items;
        this.appCMSPresenter = appCMSPresenter;
    }

    @Override
    public void onBindViewHolder(AppCMSDownloadRadioAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.mRadio.setChecked(i == downloadQualityPosition);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.download_quality_view_item, viewGroup, false);
        return new ViewHolder(view);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        itemClickListener.onItemClick(mItems.get(downloadQualityPosition));
    }

    public interface ItemClickListener<T> {
        void onItemClick(T item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.download_quality_radio_selection)
        AppCompatRadioButton mRadio;

        @BindView(R.id.download_quality_text)
        TextView mText;

        public ViewHolder(final View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);

            View.OnClickListener clickListener = v -> {
                downloadQualityPosition = getAdapterPosition();
                notifyItemRangeChanged(0, mItems.size());
                if(mItems.size() > downloadQualityPosition)
                itemClickListener.onItemClick(mItems.get(downloadQualityPosition));
            };

            itemView.setOnClickListener(clickListener);
            mRadio.setOnClickListener(clickListener);

            if (appCMSPresenter.getPlatformType() == TV && !Utils.isFireTVDevice(inflate.getContext())) {
                itemView.setOnFocusChangeListener(
                        new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if(b){
                                    mRadio.setBackground(view.getContext().getDrawable(R.drawable.circle_shape));
                                }else{
                                    mRadio.setBackground(null);
                                }
                            }
                        }
                );
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mRadio.getButtonDrawable() != null) {
                    mRadio.getButtonDrawable().setColorFilter(tintColor, PorterDuff.Mode.MULTIPLY);
                }
            } else {
                int switchOnColor = tintColor;
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_checked},
                                new int[]{}
                        }, new int[]{
                        switchOnColor,
                        switchOnColor
                });

                mRadio.setButtonTintList(colorStateList);
            }
        }

        public AppCompatRadioButton getmRadio() {
            return mRadio;
        }

        public void setmRadio(AppCompatRadioButton mRadio) {
            this.mRadio = mRadio;
        }

        public TextView getmText() {
            return mText;
        }

        public void setmText(TextView mText) {
            this.mText = mText;
        }
    }
}
