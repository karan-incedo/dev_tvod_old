package viewlift.com.myappcmsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.viewlift.appcmssdk.ViewliftSDK;

public class MainActivity extends AppCompatActivity {
    private ViewliftSDK viewliftSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewliftSDK = ViewliftSDK.initialize(this);

        Button playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewliftSDK.launchVideoPlayer("",
                        getString(R.string.hls_url),
                        getString(R.string.ad_url));
            }
        });
    }
}
