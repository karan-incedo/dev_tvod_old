package com.viewlift.myappcmsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.viewlift.appcmssdk.ViewliftSDK;
import com.viewlift.models.data.appcms.films.FilmRecordResult;
import com.viewlift.models.data.appcms.films.Record;
import com.viewlift.models.data.rest.AppCMSFilmRecordsCall;
import com.viewlift.models.data.rest.AppCMSFilmRecordsRest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private ViewliftSDK viewliftSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewliftSDK = ViewliftSDK.initialize(this);

        final RecyclerView videoListView = (RecyclerView) findViewById(R.id.video_list_view);

        getVideoData(getString(R.string.app_cms_base_url),
                getString(R.string.app_cms_site_id),
                new Action1<List<VideoAdapter.VideoData>>() {
                    @Override
                    public void call(List<VideoAdapter.VideoData> videoData) {
                        VideoAdapter videoAdapter = new VideoAdapter(videoData,
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
                });

    }

    private void getVideoData(String baseUrl,
                              String siteId,
                              final Action1<List<VideoAdapter.VideoData>> readyAction) {

        final List<VideoAdapter.VideoData> videoDataList = new ArrayList<>();

        final VideoAdapter.VideoData videoData1 = new VideoAdapter.VideoData();
        videoData1.filmId = getString(R.string.filmid1);
        videoData1.adUrl = getString(R.string.ad_url1);
        videoDataList.add(videoData1);

        final VideoAdapter.VideoData videoData2 = new VideoAdapter.VideoData();
        videoData2.filmId = getString(R.string.filmid2);
        videoData2.adUrl = getString(R.string.ad_url2);
        videoDataList.add(videoData2);

        VideoAdapter.VideoData videoData3 = new VideoAdapter.VideoData();
        videoData3.filmId = getString(R.string.filmid3);
        videoData3.adUrl = getString(R.string.ad_url3);
        videoDataList.add(videoData3);

        Gson gson = new Gson();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build();
        AppCMSFilmRecordsRest appCMSFilmRecordsRest = retrofit.create(AppCMSFilmRecordsRest.class);
        AppCMSFilmRecordsCall appCMSFilmRecordsCall = new AppCMSFilmRecordsCall(appCMSFilmRecordsRest);

        final StringBuffer filmIds = new StringBuffer();
        filmIds.append(videoData1.filmId);
        filmIds.append(",");
        filmIds.append(videoData2.filmId);
        filmIds.append(",");
        filmIds.append(videoData3.filmId);

        String url = getString(R.string.app_cms_films_api_url,
                baseUrl,
                filmIds.toString(),
                siteId);
        appCMSFilmRecordsCall.getFilmsRecords(url, new Action1<FilmRecordResult>() {
            @Override
            public void call(FilmRecordResult filmRecordResult) {
                if (filmRecordResult != null) {
                    for (int i = 0; i < filmRecordResult.getRecords().size(); i++) {
                        Record filmRecord = filmRecordResult.getRecords().get(i);
                        videoDataList.get(i).hlsUrl =
                                filmRecord.getStreamingInfo().getVideoAssets().getHls();
                        videoDataList.get(i).title =
                                filmRecord.getGist().getTitle();
                    }
                    Observable.just(videoDataList).subscribe(readyAction);
                }
            }
        });
    }
}
