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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Button btnRecordTrain, btnRead, btnCalc;
    private BufferedWriter bufferedWriter;
    private int sizeSeriesA, sizeSeriesB;

    private static final int REQUEST_CODE = 0x11;
    private boolean isRecording = false;
    private Button btnRecordTest;

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

        btnRecordTrain = findViewById(R.id.btnRecordTrain);
        btnRecordTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                handleRecording("datasensor_train.txt");
            }
        });
        btnRecordTest = findViewById(R.id.btnRecordTest);
        btnRecordTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                handleRecording("datasensor_test.txt");
            }
        });

        btnRead = findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    readFile("datasensor_train.txt");
                }
                catch (Exception e)
                {
                    Log.d("debug", e.getMessage());
                }
            }
        });

        btnCalc = findViewById(R.id.btnCalc);
        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    calcSeries();
                }
                catch (Exception e)
                {

                }
            }
        });


        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

    }

    private void calcSeries() throws Exception
    {
        double[][] seriesDoctor = readFile("datasensor_train.txt");
        double[][] seriesUser = readFile("datasensor_test.txt");
        double distance = 0f;
        distance += Exercise.DTW(seriesDoctor[0], sizeSeriesA,
                seriesUser[0], sizeSeriesB,
                0.1);
        distance += Exercise.DTW(seriesDoctor[1], sizeSeriesA,
                seriesUser[1], sizeSeriesB,
                0.1);
        distance += Exercise.DTW(seriesDoctor[2], sizeSeriesA,
                seriesUser[2], sizeSeriesB,
                0.2);
        ((TextView)findViewById(R.id.tv_distance)).setText(String.format("%.2f", distance));
    }

    private void handleRecording(String filename)
    {
        if (isRecording)
        {
            stopRecordSensorValues();
            isRecording = false;
            Toast.makeText(this, "Stopped Recording!", Toast.LENGTH_LONG).show();
        }
        else
        {
            startRecordSensorValues(filename);
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
                //TODO: handle here
            }
            else
            {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean startRecordSensorValues(String fileName)
    {
        try
        {
            bufferedWriter = new BufferedWriter
                             (new FileWriter
                             (new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), fileName)));
            mSensorManager.registerListener( this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
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
        while ((line = bufferedReader.readLine()) != null)
        {
            tokens = line.split(" ");
            series[0][k] = (Double.parseDouble(tokens[0]))/10f;
            series[1][k] = (Double.parseDouble(tokens[1]))/10f;
            series[2][k++] = (Double.parseDouble(tokens[2]))/10f;
        }
        sizeSeriesA = k;
        sizeSeriesB = k;
        Log.d("debug", "size of " + fileName + ": " + k);
        return series;
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

    private void error()
    {
        //TODO: handle error
        finish();
    }
}

