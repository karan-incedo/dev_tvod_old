package viewlift.com.myappcmsapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by viewlift on 7/13/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    public static class VideoData {
        String hlsUrl;
        String adUrl;
        String title;
    }

    public interface OnItemClickedListener {
        void clicked(VideoData videoData);
    }

    private List<VideoData> videoDataList;
    private OnItemClickedListener onItemClickedListener;

    public VideoAdapter(List<VideoData> videoDataList,
                        OnItemClickedListener onItemClickedListener) {
        this.videoDataList = videoDataList;
        this.onItemClickedListener = onItemClickedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.film_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final VideoData videoData = videoDataList.get(position);
        holder.titleTextView.setText(videoData.title);
        holder.titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickedListener.clicked(videoData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.film_title);
        }
    }
}
