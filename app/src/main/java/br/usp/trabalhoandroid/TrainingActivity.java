package br.usp.trabalhoandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class TrainingActivity extends AppCompatActivity implements SensorEventListener {

    private Exercise userExercise, professionalExercise;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private static final int REQUEST_CODE = 0x11;
    private Button recButton;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        professionalExercise = (Exercise) getIntent().getExtras().getSerializable("exercise");
        professionalExercise.printSeries();
        Log.d("debug", professionalExercise.getName());

        setTitle(getResources().getString(R.string.training).concat(" ").concat(professionalExercise.getName()));

        recButton = findViewById(R.id.bt_capture);
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                userExercise = new Exercise();
                userExercise.setName(professionalExercise.getName());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void startRecording()
    {
        startRecordSensorValues();
        Toast.makeText(this, getResources().getString(R.string.recording),
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
        Toast.makeText(this, getResources().getString(R.string.recorded), Toast.LENGTH_LONG).show();
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
        userExercise.updateSeries(x, z, y);
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
                Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void handleRecording()
    {
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beep);
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
        finish();
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
}
