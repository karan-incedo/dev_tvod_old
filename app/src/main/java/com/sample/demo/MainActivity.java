package com.sample.demo;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vl.viewlift.playersdk.VLPlayerSDKKit;
import com.vl.viewlift.playersdk.views.VLPlayer;


public class MainActivity extends AppCompatActivity {

    VLPlayer vlPlayer;
    Button buttonPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vlPlayer = findViewById(R.id.VLPlayerDemo);
        buttonPlay=findViewById(R.id.button2);

        VLPlayerSDKKit.SDKConfig config = new VLPlayerSDKKit.SDKConfig.Builder()
                .context(this).xApiKey("FJhLtOzPji2uvinKSTFNd8FLnnP0bNuw3qtXXALO").build();

        VLPlayerSDKKit vlPlayerSDKKit =new  VLPlayerSDKKit().init(config);


       /* vlPlayer.setAdTag("<>");
        vlPlayer.setUri(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
        vlPlayer.applyTimeBarColor(Color.YELLOW);
        vlPlayer.play();
        vlPlayer.seekTo(3000);
*/

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vlPlayer.setVideoId("00000151-d6a8-da54-a1fb-d7b9d7520000");
                vlPlayer.applyTimeBarColor(Color.GREEN);
                vlPlayer.play();
                vlPlayer.seekTo(5000);
            }
        });

    }
}
