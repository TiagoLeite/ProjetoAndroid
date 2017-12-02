package br.usp.trabalhoandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class VideoFragment extends Fragment
{
    static final int REQUEST_VIDEO_CAPTURE = 1;
    ImageView imageViewRecord;
    VideoView videoView;
    MediaSessionCompat mMediaSession;
    PlaybackStateCompat.Builder mStateBuilder;
    View root;
    RecyclerView videosRecyclerView;
    VideosAdapter adapter;
    private boolean isPlaying = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        root = inflater.inflate(R.layout.video_fragment, container, false);

        getActivity().setTitle("Vídeos");

        setupRecyclerView();
        setupFab();

        return root;
    }

    private void setupFab()
    {
        FloatingActionButton fab = root.findViewById(R.id.fabVideo);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dispatchVideoRecordIntent();
            }
        });
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
            videoView.setMediaController(new MediaController(getActivity()));
            videoView.start();
            //videoView.pause();
            imageViewRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //playVideo();
                }
            });

        }

        Log.d("debug", resultCode + " " + requestCode);

    }

    /*private void playVideo()
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
    }*/

}
