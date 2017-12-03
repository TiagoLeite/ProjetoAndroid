package br.usp.trabalhoandroid;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.List;


public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.VideoViewHolder>
{
    AppCompatActivity activity;
    List<Exercise> videosList;

    public ExerciseAdapter(AppCompatActivity activity, List<Exercise> videos)
    {
        videosList = videos;
        this.activity = activity;
    }

    @Override
    public ExerciseAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.video_row, parent, false);
        VideoViewHolder holder = new VideoViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ExerciseAdapter.VideoViewHolder holder, int position) {

        holder.videoTitle.setText(videosList.get(position).getName());

        final int pos = holder.getAdapterPosition();

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                View root_view = view.getRootView();
                root_view.findViewById(R.id.list_exercises_container).setVisibility(View.GONE);
                root_view.findViewById(R.id.videoView).setVisibility(View.VISIBLE);
                root_view.findViewById(R.id.toolbar).setVisibility(View.GONE);
                //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                playVideo(videosList.get(pos), root_view);
            }
        });

        holder.train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(view.getContext(), "TRAIN",Toast.LENGTH_LONG).show();
                /*View root_view = view.getRootView();
                root_view.findViewById(R.id.list_exercises_container).setVisibility(View.GONE);
                root_view.findViewById(R.id.videoView).setVisibility(View.VISIBLE);
                root_view.findViewById(R.id.toolbar).setVisibility(View.GONE);
                //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                playVideo(videosList.get(pos), root_view);*/
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
        ImageView play, train;

        public VideoViewHolder(View itemView)
        {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.video_description);
            root = itemView.findViewById(R.id.row_root);
            play = itemView.findViewById(R.id.image_play);
            train = itemView.findViewById(R.id.image_train);
        }

    }

    private void finishPlay(View root)
    {
        root.findViewById(R.id.videoView).setVisibility(View.GONE);
        root.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        root.findViewById(R.id.list_exercises_container).setVisibility(View.VISIBLE);
    }

    private void playVideo(Exercise video, final View root)
    {
        VideoView videoView = root.findViewById(R.id.videoView);
        Uri videoUri = Uri.parse(video.getVideoUriString());
        videosList.add(video);
        videoView.setVideoURI(videoUri);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finishPlay(root);
            }
        });
        videoView.start();
        /*mMediaSession = new MediaSessionCompat(activity, "debug");
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);

        MediaControllerCompat mediaController =
                new MediaControllerCompat(activity, mMediaSession);

        MediaControllerCompat.setMediaController(activity, mediaController);*/
    }
}
