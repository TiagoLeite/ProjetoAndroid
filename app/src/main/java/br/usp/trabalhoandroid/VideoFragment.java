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
    View root;
    RecyclerView videosRecyclerView;
    VideosAdapter adapter;
    List<ExerciseVideo> videosList = new ArrayList<>();

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
        videosList = new ArrayList<>();
        videosList.add(new ExerciseVideo("Video 1", null));
        //videosList.add(new ExerciseVideo("Video 2"));
        //videosList.add(new ExerciseVideo("Video 3"));
        adapter = new VideosAdapter((AppCompatActivity) getActivity(), videosList);
        videosRecyclerView.setAdapter(adapter);
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
            ExerciseVideo video = new ExerciseVideo("Name", videoUri);
            videosList.add(video);
            adapter.notifyDataSetChanged();
            //Log.d("debug", data.getDataString());
            //Log.d("debug", videoUri+"");
            /*videoView.setVideoURI(videoUri);
            videoView.setMediaController(new MediaController(getActivity()));
            videoView.start();
            //videoView.pause();
            imageViewRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //playVideo();
                }
            });*/

        }
        Log.d("debug", resultCode + " " + requestCode);
    }

}
