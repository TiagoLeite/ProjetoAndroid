package br.usp.trabalhoandroid;


import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Button btnRecord;
    private Button btnRead;
    private BufferedWriter bufferedWriter;
    private String fileName = "datasensor.txt";

    private static final int REQUEST_CODE = 0x11;
    private boolean isRecording = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager == null)
        {
            error();
            return;
        }
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnRecord = findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                handleRecording();
            }
        });

        btnRead = findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    readFile();
                } catch (Exception e) {
                    Log.d("debug", e.getMessage());
                }
            }
        });


        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE); // without sdk version check

    }

    private void handleRecording()
    {
        if (isRecording)
        {
            stopRecordSensorValues();
            isRecording = false;
            Toast.makeText(this, "Stopped Recording!", Toast.LENGTH_LONG).show();
        }
        else
        {
            startRecordSensorValues();
            Toast.makeText(this, "Recording!", Toast.LENGTH_LONG).show();
            isRecording = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // save file
            }
            else
            {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean startRecordSensorValues()
    {
        try
        {
            bufferedWriter = new BufferedWriter
                             (new FileWriter
                             (new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), fileName)));
            mSensorManager.registerListener( this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("debug", "STARTED REC");
            return true;
        }
        catch (Exception e)
        {
            Log.d("debug", e.getMessage()+e.getCause());
            return false;
        }
    }

    private void stopRecordSensorValues()
    {
        try
        {
            bufferedWriter.close();
        }
        catch (Exception e)
        {
            Log.d("debug", e.getMessage());
        }
        finally
        {
            Log.d("debug", "Stop REC");
            mSensorManager.unregisterListener(this);
        }
    }

    public void readFile() throws Exception
    {
        String line;
        BufferedReader bufferedReader = new BufferedReader(
                        new FileReader(
                        new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), fileName)));
        while ((line = bufferedReader.readLine()) != null)
        {
            for (String s: line.split(" "))
            {
                System.out.print(s+" ");
            }
            System.out.println();
        }
    }


    private void error()
    {
        //TODO: handle error
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        try
        {
            bufferedWriter.append(Float.toString(x))
                    .append(" "+Float.toString(y))
                    .append(" "+Float.toString(z))
                    .append("\n");
        }
        catch (Exception e)
        {
            return;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

