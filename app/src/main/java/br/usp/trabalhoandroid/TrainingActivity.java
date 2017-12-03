package br.usp.trabalhoandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;


public class TrainingActivity extends AppCompatActivity implements SensorEventListener {

    private Exercise userExercise, professionalExercise;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private static final int REQUEST_CODE = 0x11;
    private Button recButton;
    private boolean isRecording = false;
    private LineChart exerciseChart;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        exerciseChart = findViewById(R.id.exercise_chart);

        if (mAccelerometer == null || mSensorManager == null)
            Log.d("debug", "NULL");
        else
            Log.d("debug", "not NULL");


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        professionalExercise = (Exercise) getIntent().getExtras().getSerializable("exercise");

        setTitle(getResources().getString(R.string.training).concat(" ").concat(professionalExercise.getName()));

        userExercise = new Exercise();
        userExercise.setName(professionalExercise.getName());

        recButton = findViewById(R.id.bt_capture);
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                findViewById(R.id.rec_info).setVisibility(View.INVISIBLE);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                handleRecording();
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
        //userExercise.printSeries();
        showExerciseResults();
    }

    private void showExerciseResults()
    {
        List<Entry> entries = new ArrayList<>();
        List<Entry> entriesPro = new ArrayList<>();

        double series[][] =  userExercise.getSeries();
        double seriesPro[][] =  professionalExercise.getSeries();

        for (int i = 0; i < userExercise.getSizeSeries(); i++)
            entries.add(new Entry(i, (float) series[0][i]));

        LineDataSet dataSet = new LineDataSet(entries, "Usuário");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setCircleHoleRadius(0f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setColor(Color.RED);
        dataSet.setDrawHorizontalHighlightIndicator(true);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawCircles(false);
        dataSet.setCircleColors(Color.RED);
        dataSet.setDrawValues(false);
        dataSet.setValueTextSize(12);
        dataSet.setDrawFilled(true);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setFillColor(getResources().getColor(R.color.transparent));


        for (int i = 0; i < professionalExercise.getSizeSeries(); i++)
            entriesPro.add(new Entry(i, (float) seriesPro[0][i]));

        LineDataSet dataSetPro = new LineDataSet(entriesPro, "Profissional");
        dataSetPro.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetPro.setCircleHoleRadius(0f);
        dataSetPro.setCircleColor(Color.BLUE);
        dataSetPro.setCircleHoleRadius(0f);
        dataSetPro.setCircleColor(Color.BLUE);
        dataSetPro.setColor(Color.BLUE);
        dataSetPro.setDrawHorizontalHighlightIndicator(true);
        dataSetPro.setDrawCircleHole(false);
        dataSetPro.setDrawCircles(false);
        dataSetPro.setCircleColors(Color.BLUE);
        dataSetPro.setDrawValues(false);
        dataSetPro.setValueTextSize(12);
        dataSetPro.setDrawFilled(true);
        dataSetPro.setMode(LineDataSet.Mode.LINEAR);
        dataSetPro.setFillColor(getResources().getColor(R.color.transparent));

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        dataSets.add(dataSetPro);

        LineData data = new LineData(dataSets);
        exerciseChart.setData(data);
        exerciseChart.setVisibility(View.VISIBLE);
        exerciseChart.setDrawGridBackground(false);
        exerciseChart.getXAxis().setDrawGridLines(false);
        exerciseChart.getAxisLeft().setDrawGridLines(false);
        exerciseChart.getAxisRight().setDrawGridLines(false);
        exerciseChart.setDrawGridBackground(false);
        exerciseChart.setDrawBorders(true);
        exerciseChart.setBorderColor(getResources().getColor(R.color.gray));
        exerciseChart.getDescription().setEnabled(false);
        //exerciseChart.getLegend().setEnabled(false);
        exerciseChart.getXAxis().setEnabled(true);
        exerciseChart.getAxisLeft().setDrawGridLines(false);
        exerciseChart.getAxisRight().setDrawGridLines(false);
        exerciseChart.getXAxis().setDrawGridLines(false);
        exerciseChart.setScaleEnabled(false);
        exerciseChart.setPinchZoom(true);
        exerciseChart.setDoubleTapToZoomEnabled(true);
        exerciseChart.setVisibleXRangeMaximum(200f);
        exerciseChart.setVisibleYRangeMaximum(10f, YAxis.AxisDependency.LEFT);
        exerciseChart.invalidate();
        //chart.setDragOffsetX(10);
        XAxis xAxis = exerciseChart.getXAxis();
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(10f);
        findViewById(R.id.layout_rec).setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        toolbar.setVisibility(View.GONE);
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(R.string.new_exercise);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.icon);
        final View layoutView = inflater.inflate(R.layout.exercise_result_dialog, null);
        double result = userExercise.calcDistanceOfSeries(professionalExercise);
        ((TextView)layoutView.findViewById(R.id.tv_result)).
                setText(String.format("%.1f", result).concat(" %"));
        builder.setView(layoutView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create();
        builder.show();*/
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
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        //userExercise = null;
    }
}
