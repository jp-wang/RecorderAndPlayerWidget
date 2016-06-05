package com.jp.recorderplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jp.recorderandplayer.playerview.PlayerView;
import com.jp.recorderandplayer.recorderview.RecorderView;

public class MainActivity extends AppCompatActivity {

    RecorderView recorderView;

    PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorderView = (RecorderView) this.findViewById(R.id.recorder);
        playerView = (PlayerView) this.findViewById(R.id.player);

        recorderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorderView.startAnimation(new RecorderView.RecorderViewListener() {
                    @Override
                    public void onRecorderFinished() {
                        Toast.makeText(MainActivity.this, "Record finished!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerView.startAnimation(10, new PlayerView.PlayerViewListener() {
                    @Override
                    public void onPlayerFinished() {
                        Toast.makeText(MainActivity.this, "Play finished!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
