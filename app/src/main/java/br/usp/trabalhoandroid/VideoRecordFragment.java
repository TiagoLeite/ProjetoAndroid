package br.usp.trabalhoandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class VideoRecordFragment extends Fragment
{
    View root;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    Button btnRecord;
    VideoView videoView;
    MediaSessionCompat mMediaSession;
    PlaybackStateCompat.Builder mStateBuilder;
    RecyclerView videosRecyclerView;
    VideosAdapter adapter;
    private boolean isPlaying = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        root = inflater.inflate(R.layout.record_activity, container, false);

        getActivity().setTitle("Vídeos");

        btnRecord = root.findViewById(R.id.btnRecordTrain);
        videoView = root.findViewById(R.id.videoView);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchVideoRecordIntent();
            }
        });

        setupRecyclerView();

        return root;
    }

    private void setupRecyclerView()
    {
        videosRecyclerView = root.findViewById(R.id.videos_rv);
        videosRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        videosRecyclerView.setLayoutManager(llm);
        List<ExerciseVideo> list = new ArrayList<>();
        list.add(new ExerciseVideo("Video 1"));
        list.add(new ExerciseVideo("Video 2"));
        list.add(new ExerciseVideo("Video 3"));
        adapter = new VideosAdapter((AppCompatActivity) getActivity(), list);
        videosRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void dispatchVideoRecordIntent()
    {
        Intent videoRecIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (videoRecIntent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            startActivityForResult(videoRecIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_VIDEO_CAPTURE)
        {
            Uri videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            videoView.setMediaController(new MediaController(this.getActivity()));
            videoView.start();
            //videoView.pause();

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
        mMediaSession = new MediaSessionCompat(getActivity(), "debug");

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
                new MediaControllerCompat(getActivity(), mMediaSession);

        MediaControllerCompat.setMediaController(getActivity(), mediaController);
    }
}
