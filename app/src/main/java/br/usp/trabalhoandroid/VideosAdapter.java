package br.usp.trabalhoandroid;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder>
{
    AppCompatActivity activity;
    List<ExerciseVideo> videosList;
    ViewGroup parent;

    public VideosAdapter(AppCompatActivity activity,  List<ExerciseVideo> videos)
    {
        videosList = videos;
        this.activity = activity;
    }

    @Override
    public VideosAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.video_row, parent, false);
        VideoViewHolder holder = new VideoViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideosAdapter.VideoViewHolder holder, int position) {

        holder.videoTitle.setText(videosList.get(position).getDescription());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                View root_view = view.getRootView();
                root_view.findViewById(R.id.list_exercises_container).setVisibility(View.GONE);
                root_view.findViewById(R.id.videoView).setVisibility(View.VISIBLE);
                root_view.findViewById(R.id.toolbar).setVisibility(View.GONE);
                //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                Log.d("debug", "Clciked");
            }
        });

    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder
    {
        ViewGroup root;
        TextView videoTitle;

        public VideoViewHolder(View itemView)
        {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.video_description);
            root = itemView.findViewById(R.id.row_root);
        }

    }
}
