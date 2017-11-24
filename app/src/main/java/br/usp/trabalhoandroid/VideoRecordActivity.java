package br.usp.trabalhoandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class VideoRecordActivity extends AppCompatActivity
{
    static final int REQUEST_VIDEO_CAPTURE = 1;
    Button btnRecord;
    VideoView videoView;
    MediaSessionCompat mMediaSession;
    PlaybackStateCompat.Builder mStateBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);

        btnRecord = findViewById(R.id.btnRecordTrain);
        videoView = findViewById(R.id.videoView);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchVideoRecordIntent();
            }
        });

    }

    private void dispatchVideoRecordIntent()
    {
        Intent videoRecIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (videoRecIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(videoRecIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_VIDEO_CAPTURE)
        {
            Uri videoUri = data.getData();
            videoView.setVideoURI(videoUri);

            btnRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVideo();
                }
            });

        }

        Log.d("debug", resultCode + " " + requestCode);

    }

    private void playVideo()
    {

        // Create a MediaSessionCompat
        mMediaSession = new MediaSessionCompat(this, "debug");

        // Enable callbacks from MediaButtons and TransportControls
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        //mMediaSession.setState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller
        //mMediaSession.setCallback(new MySessionCallback());

        // Create a MediaControllerCompat
        MediaControllerCompat mediaController =
                new MediaControllerCompat(this, mMediaSession);

        MediaControllerCompat.setMediaController(this, mediaController);
    }
}











