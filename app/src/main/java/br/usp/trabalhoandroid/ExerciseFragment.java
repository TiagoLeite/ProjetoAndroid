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
import android.util.Pair;
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
    private RecyclerView exercisesRecyclerView;
    private ExerciseAdapter adapter;
    private List<AppPair<Exercise, Exercise>> exerciseList = new ArrayList<>();
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
        getActivity().setTitle(getResources().getString(R.string.exercises));
        setupFab();
        setupRecyclerView();

        MyReceiver receiver = new MyReceiver();
        getActivity().registerReceiver(receiver, new IntentFilter("update_exercise"));

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Log.d("debug", "Create view");

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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupRecyclerView()
    {
        exercisesRecyclerView = root.findViewById(R.id.exercises_rv);
        exercisesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        exercisesRecyclerView.setLayoutManager(llm);
        exerciseList = loadExercises("exercises");
        adapter = new ExerciseAdapter((AppCompatActivity) getActivity(), exerciseList);
        exercisesRecyclerView.setAdapter(adapter);
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
    @SuppressWarnings("unchecked")
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
                            exerciseList.add(0, new AppPair<Exercise, Exercise>(currentExercise, null));
                            currentExercise.printSeries();
                            adapter.notifyDataSetChanged();
                            currentExercise = null;
                            saveExercises(exerciseList, "exercises");
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
    private List<AppPair<Exercise, Exercise>> loadExercises(String fileName)
    {
        try
        {
            FileInputStream fis = new FileInputStream(
                    new File(Environment.getExternalStorageDirectory(),("/"+fileName)));
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<AppPair<Exercise, Exercise>> pairList = (List<AppPair<Exercise, Exercise>>)ois.readObject();
            ois.close();
            fis.close();
            return pairList;
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }

    private void saveExercises(List<AppPair<Exercise, Exercise>> exercises, String fileName)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(
                    new File(Environment.getExternalStorageDirectory(),("/"+fileName)), false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(exercises);
            oos.close();
            fos.close();
        }
        catch (Exception e)
        {

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
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            return true;
        }
        catch (Exception e)
        {
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
                Toast.makeText(getActivity(), getResources().getString(R.string.denied), Toast.LENGTH_SHORT).show();
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
        return series;
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
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d("debug", this.getClass().toString()+"PAUSED");
        if(isRecording)
            stopRecording();

        saveExercises(exerciseList, "exercises");

    }

    public void setExerciseList(List<AppPair<Exercise, Exercise>> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public List<AppPair<Exercise, Exercise>> getExerciseList() {
        return exerciseList;
    }

    public void update()
    {
        adapter.notifyDataSetChanged();
    }


    @SuppressWarnings("unchecked")
    private class MyReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            AppPair<Exercise, Exercise> exercisePair =
                    (AppPair<Exercise, Exercise>)intent.getSerializableExtra("exercise_2");
            if (exercisePair != null)
            {
                int pos = 0;
                for (AppPair pair : exerciseList)
                {
                    if (pair.getId() == exercisePair.getId())
                    {
                        exerciseList.set(pos, exercisePair);
                        adapter.setDataset(exerciseList);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    pos++;
                }
            }
        }
    }
}
