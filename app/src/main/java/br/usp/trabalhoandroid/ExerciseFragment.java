package br.usp.trabalhoandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.*;
import java.util.*;

import static android.app.Activity.RESULT_OK;

public class ExerciseFragment extends Fragment implements SensorEventListener
{
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private View root;
    private Button recButton;
    private RecyclerView videosRecyclerView;
    private ExerciseAdapter adapter;
    private List<Exercise> exerciseList = new ArrayList<>();
    private int sizeSeriesA, sizeSeriesB;
    private static final int REQUEST_CODE = 0x11;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private boolean isRecording = false;
    private Exercise currentExercise;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.exercise_fragment, container, false);
        getActivity().setTitle("Exercícios");
        setupRecyclerView();
        setupFab();
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        ScreenReceiver mReceiver = new ScreenReceiver();
        getActivity().registerReceiver(mReceiver, filter);
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
        videosRecyclerView = root.findViewById(R.id.exercises_rv);
        videosRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        videosRecyclerView.setLayoutManager(llm);
        exerciseList = loadVideos();
        adapter = new ExerciseAdapter((AppCompatActivity) getActivity(), exerciseList);
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
    public void onActivityResult(int requestCode, int resultCode, final Intent data)
    {
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == REQUEST_VIDEO_CAPTURE)
        {
            currentExercise = new Exercise();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getLayoutInflater();
            builder.setTitle(R.string.new_exercise);
            builder.setCancelable(false);
            builder.setIcon(R.drawable.icon);
            final View layoutView = inflater.inflate(R.layout.add_exercise_dialog, null);
            recButton = layoutView.findViewById(R.id.button_capture);
            recButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    handleRecording();
                }
            });
            final EditText input = layoutView.findViewById(R.id.et_exercise_name);
            builder.setView(layoutView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Uri videoUri = data.getData();
                            currentExercise.setName(input.getText().toString());
                            currentExercise.setVideoUriString(videoUri.toString());
                            exerciseList.add(0, currentExercise);
                            currentExercise.printSeries();
                            adapter.notifyDataSetChanged();
                            currentExercise = null;
                            saveVideos(exerciseList);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            currentExercise = null;
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

    private void startRecording()
    {
        startRecordSensorValues();
        Toast.makeText(getActivity(), getResources().getString(R.string.recording),
                Toast.LENGTH_LONG).show();
        recButton.setText(getResources().getString(R.string.recording));
        isRecording = true;
    }

    private void stopRecording()
    {
        stopRecordSensorValues();
        isRecording = false;
        recButton.setText(getResources().getString(R.string.recorded));
        recButton.setOnClickListener(null);
        Toast.makeText(getActivity(), getResources().getString(R.string.recorded), Toast.LENGTH_LONG).show();
    }

    private boolean startRecordSensorValues()
    {
        try
        {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d("debug", "STARTED REC");
            return true;
        }
        catch (Exception e)
        {
            Log.d("debug", e.getMessage() + e.getCause());
            return false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        currentExercise.updateSeries(x, z, y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TODO: handle here
            } else {
                Toast.makeText(getActivity(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public double[][] readFile(String fileName) throws Exception
    {
        //TODO: Refactor: not limit series array size
        double[][] series = new double[3][];
        series[0] = new double[2048];
        series[1] = new double[2048];
        series[2] = new double[2048];
        String line, tokens[];
        BufferedReader bufferedReader = new BufferedReader(
                new FileReader(
                        new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), fileName)));
        int k = 0;
        while ((line = bufferedReader.readLine()) != null) {
            tokens = line.split(" ");
            series[0][k] = (Double.parseDouble(tokens[0])) / 10f;
            series[1][k] = (Double.parseDouble(tokens[1])) / 10f;
            series[2][k++] = (Double.parseDouble(tokens[2])) / 10f;
        }
        sizeSeriesA = k;
        sizeSeriesB = k;
        Log.d("debug", "size of " + fileName + ": " + k);
        return series;
    }

    private void calcSeries() throws Exception
    {
        double[][] seriesDoctor = readFile("datasensor_train.txt");
        double[][] seriesUser = readFile("datasensor_test.txt");
        double distance = 0f;
        distance += Exercise.DTW(seriesDoctor[0], sizeSeriesA,
                seriesUser[0], sizeSeriesB,
                .05);
        distance += Exercise.DTW(seriesDoctor[1], sizeSeriesA,
                seriesUser[1], sizeSeriesB,
                0.05);
        distance += Exercise.DTW(seriesDoctor[2], sizeSeriesA,
                seriesUser[2], sizeSeriesB,
                0.05);
        //((TextView)view.findViewById(R.id.tv_distance)).setText(String.format("%.2f", (1000f - distance) / 10f) + "%");
    }

    private void handleRecording()
    {
        final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.beep);
        if (isRecording)
        {
            stopRecording();
        }
        else
        {
            new CountDownTimer(3100, 1000)
            {
                public void onTick(long millisUntilFinished)
                {
                    mediaPlayer.start();
                }

                public void onFinish()
                {
                    startRecording();
                }

            }.start();
        }
    }

    private void error()
    {
        //TODO: handle error
        getActivity().finish();
    }

    private void stopRecordSensorValues()
    {
        Log.d("debug", "Stop REC");
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    private class ScreenReceiver extends BroadcastReceiver
    {
        public boolean wasScreenOn = true;
        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            String action = intent.getAction();
            if (action != null)
            {
                if (action.equals(Intent.ACTION_SCREEN_OFF))
                {
                    Log.d("debug", "OFF");
                    stopRecording();
                    wasScreenOn = false;
                }
                /*else if (action.equals(Intent.ACTION_SCREEN_ON))
                {
                    Log.d("debug", "ON");
                    // and do whatever you need to do here
                    wasScreenOn = true;
                }*/
            }
        }

    }
}
