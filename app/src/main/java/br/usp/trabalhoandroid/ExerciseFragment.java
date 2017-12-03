package br.usp.trabalhoandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ExerciseFragment extends Fragment
{
    static final int REQUEST_VIDEO_CAPTURE = 1;
    View root;
    RecyclerView videosRecyclerView;
    ExerciseAdapter adapter;
    List<Exercise> videosList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.video_fragment, container, false);
        getActivity().setTitle("Exercícios");
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
        videosList = loadVideos();
        adapter = new ExerciseAdapter((AppCompatActivity) getActivity(), videosList);
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
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (resultCode != RESULT_OK)
            return;
        if (requestCode == REQUEST_VIDEO_CAPTURE)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            builder.setTitle(R.string.new_exercise);
            builder.setCancelable(false);
            builder.setIcon(R.drawable.icon);
            final View layoutView = inflater.inflate(R.layout.add_exercise_dialog, null);
            final EditText input = layoutView.findViewById(R.id.et_exercise_name);
            builder.setView(layoutView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Uri videoUri = data.getData();
                            Exercise video = new Exercise(input.getText().toString(),
                                    videoUri.toString());
                            videosList.add(0, video);
                            adapter.notifyDataSetChanged();
                            saveVideos(videosList);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            builder.create();
            builder.show();
        }
        Log.d("debug", resultCode + " " + requestCode);
    }

    @SuppressWarnings("unchecked")
    private List<Exercise> loadVideos()
    {
        try
        {
            FileInputStream fis = new FileInputStream(new File(Environment.getExternalStorageDirectory(),"/videos"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<Exercise> videos = (List<Exercise>)ois.readObject();
            ois.close();
            fis.close();
            return videos;
        }
        catch (Exception e)
        {
            Log.d("debug", e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveVideos(List<Exercise> videos)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(
                    new File(Environment.getExternalStorageDirectory(),"/videos"), false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(videos);
            oos.close();
            fos.close();
        }
        catch (Exception e)
        {
            Log.d("debug", e.getMessage());
        }
    }

}
