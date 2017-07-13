package viewlift.com.myappcmsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.viewlift.appcmssdk.ViewliftSDK;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewliftSDK viewliftSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewliftSDK = ViewliftSDK.initialize(this);

        RecyclerView videoListView = (RecyclerView) findViewById(R.id.video_list_view);
        VideoAdapter videoAdapter = new VideoAdapter(getVideoData(),
                new VideoAdapter.OnItemClickedListener() {
                    @Override
                    public void clicked(VideoAdapter.VideoData videoData) {
                        viewliftSDK.launchVideoPlayer(videoData.title,
                                videoData.hlsUrl,
                                videoData.adUrl);
                    }
                });
        videoListView.setAdapter(videoAdapter);
    }

    private List<VideoAdapter.VideoData> getVideoData() {
        List<VideoAdapter.VideoData> videoDataList = new ArrayList<>();

        VideoAdapter.VideoData videoData1 = new VideoAdapter.VideoData();
        videoData1.hlsUrl = getString(R.string.hls_url1);
        videoData1.title = getString(R.string.film_title1);
        videoData1.adUrl = getString(R.string.ad_url1);
        videoDataList.add(videoData1);

        VideoAdapter.VideoData videoData2 = new VideoAdapter.VideoData();
        videoData2.hlsUrl = getString(R.string.hls_url2);
        videoData2.title = getString(R.string.film_title2);
        videoData2.adUrl = getString(R.string.ad_url2);
        videoDataList.add(videoData2);

        VideoAdapter.VideoData videoData3 = new VideoAdapter.VideoData();
        videoData3.hlsUrl = getString(R.string.hls_url3);
        videoData3.title = getString(R.string.film_title3);
        videoData3.adUrl = getString(R.string.ad_url3);
        videoDataList.add(videoData3);

        return videoDataList;
    }
}
